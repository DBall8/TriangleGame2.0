package messageEvent;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class for handling messages sent between server and client
 */
public class MessageEvent {
    private JSONObject json; // Message converted to JSON
    private String ID; // ID of the client that sent the message, if sent by a client

    /**
     * Converts a string message into a JSON object
     * @param msg the message as a string
     */
    public MessageEvent(String msg){
        try {
            this.json = new JSONObject(msg);
        } catch(JSONException e){
            System.err.println("Could not form JSON");
            System.err.println(msg);
            return;
        }
        // If ID is present, mark it down
        if(json.has("ID")) {
            this.ID = json.getString("ID");
        }
    }

    // Getters
    public JSONObject getMessage(){ return json; }
    public String getID(){ return ID; }
}
