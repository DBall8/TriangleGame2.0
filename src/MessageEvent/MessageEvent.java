package MessageEvent;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageEvent {
    private JSONObject json;
    private String ID;

    public MessageEvent(String msg){
        try {
            this.json = new JSONObject(msg);
        } catch(JSONException e){
            System.err.println("Could not form JSON");
            System.err.println(msg);
            return;
        }
        if(json.has("ID")) {
            this.ID = json.getString("ID");
        }
    }

    public JSONObject getMessage(){ return json; }
    public String getID(){ return ID; }
}
