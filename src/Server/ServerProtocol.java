package server;

import animation.Animation;
import events.frameEvent.ClientFrameEvent;
import events.frameEvent.FrameEvent;
import events.HitEvent;
import gameManager.GameManager;
import messageEvent.MessageEvent;
import objects.entities.Player;
import objects.entities.projectiles.Projectile;
import physics.Physics;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for handling all the interactions between the server side game simulation and the server
 */
public class ServerProtocol {

    private static final int REBOUND_DIST = 50;

    private Server server; // the game server
    private GameManager game; // the server game simulation

    private boolean ready = false; // true when both a server and game have been created

    private List<String> disconnectedIDs = new ArrayList<>();

    /**
     * Sets up the protocol and allows it to start
     * @param server the server
     * @param game the game simulation
     */
    public void prepare(Server server, GameManager game) {
        this.server = server;
        this.game = game;

        // make sure neither are null
        if(server != null && game != null){
            ready = true;
        }
        else{
            System.err.println("NULL VALUE GIVEN TO PROTOCOL");
        }
    }

    /**
     * Handles a new frame of the server simulation
     * @param event the server frame event containing the states of all players
     */
    public void handleFrameUpdate(FrameEvent event){
        if(!ready) return; // make sure protocol is ready

        JSONObject json = event.toJSON();

        addDisconnectsToFrame(json);

        // send the server's frame to all clients
        server.broadcast(json.toString());
    }

    /**
     * Handles a message received from a client
     * @param event the message event received
     */
    public void handleMessage(MessageEvent event){
        if(!ready) return; // make sure protocol is ready

        // get the JSON object from the message
        JSONObject json = event.getMessage();

        // If the message is a disconnection message, remove the client's player
        if(json.has("disconnect")){
            game.removePlayer(json.getString("ID"));
            addDisconnectedID(json.getString("ID"));
            return;
        }

        // Otherwise, this is a client's frame update
        ClientFrameEvent frame = new ClientFrameEvent(json);

        Player player = game.getPlayer(frame.getID());
        // If the player does not exist yet in this simulation, create the new player
        if(player == null){
            Player p = new Player(frame.getID(), (int)frame.getX(), (int)frame.getY(), frame.getAngle());
            game.addPlayer(p);
        }
        // Otherwise, update the existing player, but dont take health updates
        else{
            // If the player has moved too far from where they were last, reject the new position
            if(Physics.getDistance(player.getX(), player.getY(), frame.getX(), frame.getY()) > REBOUND_DIST){
                game.updatePlayer(frame.getID(), player.getX(), player.getY(), frame.getXvel(), frame.getYvel(), frame.getAngle());
            }
            // Otherwise, keep the position
            else{
                game.updatePlayer(frame.getID(), frame.getX(), frame.getY(), frame.getXvel(), frame.getYvel(), frame.getAngle());
            }

        }

        // Fire all new projectiles
        if(frame.getNewProjectiles() != null) {
            for (Projectile p : frame.getNewProjectiles()) {
                Projectile existingP = game.getProjectile(p.getID());
                if(existingP == null || !existingP.getID().equals(p.getID())) {
                    game.addProjectile(p);
                }
            }
        }

        // update all damages
        if(frame.getNewHits() != null){
            for(HitEvent hit: frame.getNewHits()){
                Player p = game.getPlayer(hit.getPlayerID());
                if(p != null){
                    p.damage(hit.getDamage());
                }
            }
        }

        if(frame.getNewAnimations() != null){
            for(Animation a: frame.getNewAnimations()){
                Player p = game.getPlayer(a.getOwnerID());
                if(p != null){
                    p.addAnimation(a, true);
                }
            }
        }
    }

    private synchronized void addDisconnectsToFrame(JSONObject json){

        if(disconnectedIDs.size() <= 0) return;

        JSONArray disconIDs = new JSONArray();
        for(String id: disconnectedIDs){
            disconIDs.put(id);
        }

        json.put("disconnects", disconIDs);
        disconnectedIDs.clear();
    }

    private synchronized void addDisconnectedID(String id){
        disconnectedIDs.add(id);
    }

}
