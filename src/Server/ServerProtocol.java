package Server;

import GameManager.FrameEvent.ClientFrameEvent;
import GameManager.FrameEvent.IFrameEvent;
import GameManager.GameManager;
import MessageEvent.MessageEvent;
import Objects.Entities.Player;
import org.json.JSONObject;

public class ServerProtocol {

    private Server server;
    private GameManager game;

    private boolean ready = false;

    public void prepare(Server server, GameManager game) {
        this.server = server;
        this.game = game;

        if(server != null && game != null){
            ready = true;
        }
        else{
            System.err.println("NULL VALUE GIVEN TO PROTOCOL");
        }
    }

    public void handleFrameUpdate(IFrameEvent event){
        if(!ready) return;
        server.broadcast(event.toJSON().toString());
    }

    public void handleMessage(MessageEvent event){
        if(!ready) return;

        //System.out.println(event.getMessage());

        JSONObject json = event.getMessage();

        if(json.has("disconnect")){
            // Disconenct this ID
            game.removePlayer(json.getString("ID"));
            return;
        }

        ClientFrameEvent frame = new ClientFrameEvent(json);

        if(game.getPlayer(frame.getID()) == null){
            Player p = new Player(frame.getID(), (int)frame.getX(), (int)frame.getY());
            game.addPlayer(p);
        }
        else{
            game.updatePlayer(frame.getID(), frame.getX(), frame.getY(), frame.getXvel(), frame.getYvel(), frame.getAngle());
        }
    }
}
