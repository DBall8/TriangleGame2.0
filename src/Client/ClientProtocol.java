package Client;

import GameManager.FrameEvent.ClientFrameEvent;
import GameManager.FrameEvent.ServerFrameEvent;
import MessageEvent.MessageEvent;
import GameManager.FrameEvent.FrameEvent;
import GameManager.GameManager;
import Objects.Entities.Player;
import Objects.Entities.Projectile;
import org.json.JSONObject;

/**
 * Class for handling the interactions between the Game and the messages coming from the server
 */
public class ClientProtocol{

    private ClientConnection conn; // connection to the server
    private GameManager game; // Local side game simulation

    private boolean ready = false; // true when both a game and connection have been created

    /**
     * Set up the protocol to be ready to run
     * @param conn the connection to the server
     * @param game the game simulation
     */
    public void prepare(ClientConnection conn, GameManager game) {
        this.conn = conn;
        this.game = game;

        // ensure neither are null
        if(conn != null && game != null){
            ready = true;
        }
        else{
            System.err.println("NULL VALUE GIVEN TO PROTOCOL");
        }
    }

    /**
     * Called whenever a new frame is generated by the game simulation
     * @param event the event containing the local state of the player
     */
    public void handleFrameUpdate(FrameEvent event){
        if(!ready) return; // make sure protocol is set up all the way

        // Package player state into a JSON object and send it to the server
        JSONObject json = event.toJSON();
        conn.send(json.toString());
    }

    /**
     * Handles receiving a message from the server
     * @param event the server's message containing the state of all players
     */
    public void handleMessage(MessageEvent event){
        if(!ready) return; // make sure the protocol is set up all the way

        // Recreate a server frame event from the JSON received
        ServerFrameEvent serverFrame = new ServerFrameEvent(event.getMessage());

        // Go through each connected client's state and update their corresponding player locally
        for(ClientFrameEvent clientFrame: serverFrame.getClientFrames()){

            // If the player is this client's player
            if(clientFrame.getID().equals(game.getPlayerID())){
                // Make sure hasnt traveled too far
            }
            // Add new player if doesnt already exist
            else if(game.getPlayer(clientFrame.getID()) == null){
                Player p = new Player(clientFrame.getID(), (int)clientFrame.getX(), (int)clientFrame.getY());
                game.addPlayer(p);
            }
            // update existing player
            else{
                game.updatePlayer(clientFrame.getID(), clientFrame.getX(), clientFrame.getY(), clientFrame.getXvel(), clientFrame.getYvel(), clientFrame.getAngle());
            }

            // Fire all new projectiles
            if(clientFrame.getNewProjectiles() != null) {
                for (Projectile p : clientFrame.getNewProjectiles()) {
                    Projectile existingP = game.getProjectile(p.getID());
                    if(existingP == null || !existingP.getID().equals(p.getID())) {
                        game.addProjectile(p);
                    }
                }
            }
        }
    }
}
