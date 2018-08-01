package Client;

import MessageEvent.MessageEvent;
import GameManager.FrameEvent.FrameEvent;
import GameManager.GameManager;
import org.json.JSONObject;

public class ClientProtocol{

    private ClientConnection conn;
    private GameManager game;

    private boolean ready = false;

    public void prepare(ClientConnection conn, GameManager game) {
        this.conn = conn;
        this.game = game;

        if(conn != null && game != null){
            ready = true;
        }
        else{
            System.err.println("NULL VALUE GIVEN TO PROTOCOL");
        }
    }

    public void handleFrameUpdate(FrameEvent event){
        if(!ready) return;
        JSONObject json = event.toJSON();
        conn.send(json.toString());

    }

    public void handleMessage(MessageEvent event){
        if(!ready) return;

        System.out.println(event.getMessage());
    }
}
