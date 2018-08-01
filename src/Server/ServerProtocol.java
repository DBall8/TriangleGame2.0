package Server;

import GameManager.FrameEvent.FrameEvent;
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

    public void handleFrameUpdate(FrameEvent event){
        if(!ready) return;
    }

    public void handleMessage(MessageEvent event){
        if(!ready) return;

        //System.out.println(event.getMessage());

        JSONObject json = new JSONObject(event.getMessage());
        FrameEvent frame = new FrameEvent(json);

        if(game.getEntity(frame.getID()) == null){
            Player p = new Player(frame.getID(), (int)frame.getX(), (int)frame.getY());
            game.addPlayer(p);
        }
        else{
            game.updateEntity(frame.getID(), frame.getX(), frame.getY(), frame.getXvel(), frame.getYvel(), frame.getAngle());
        }
    }
}
