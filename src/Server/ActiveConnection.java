package server;

import messageEvent.MessageEvent;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * A class for handling an active connection to a client on the server side
 */
public class ActiveConnection {

    private Socket socket; // connection to the client
    private String clientID; // Player ID of the client

    private PrintWriter out; // for writing
    private BufferedReader in; // for reading

    private Thread listenerThread; // a thread continuously listening for messages from the client
    private Server server; // the server who created this connection

    /**
     * Constructor
     * @param socket the socket conencting to the client
     * @param server the server that created this connection
     */
    public ActiveConnection(Socket socket, Server server){
        this.socket = socket;
        this.server = server;

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Connection with client at " + socket.getInetAddress() + " established.");

            // once streams are set up, start listening
            listen();

        } catch(IOException e){
            System.err.println("Could not create connection with client.");
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to the client
     * @param msg the message to send
     */
    protected void send(String msg){
        // make sure the stream is ready
        if(out == null){
            System.err.println("Could not send message. No open write stream.");
            return;
        }
        out.println(msg);
    }

    /**
     * Continuously listens for messages from the client
     */
    private void listen(){
        listenerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String msg;
                try {
                    while ((msg = in.readLine()) != null) {
                        // Create a message event when a message is received
                        MessageEvent event = new MessageEvent(msg);
                        // If havent found the ID already, mark it down
                        if(clientID == null){
                            clientID = event.getID();
                        }
                        // pass the message to the message handler
                        server.messageHandler.handle(event);
                    }
                }
                catch(IOException e){
                    System.err.println("Failed to receive message.");
                    e.printStackTrace();
                }
                finally{
                    close();
                }
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    /**
     * Closes this connection to the client
     */
    public void close(){
        // If still listening, stop the listener
        if(listenerThread != null) {
            listenerThread.interrupt();
        }

        // Close the connection
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Could not close socket");
            e.printStackTrace();
        }

        // remove self from server's list off active connections
        server.removeConnection(this);
        // tell the protocol that this client disconnected.
        sendDisconnect();

        System.out.println("Client connection closed.");
        // Remove connection from server's connection list
    }

    /**
     * Sends a disconnection message to the server game simulation
     */
    private void sendDisconnect(){
        // Construct a message that just has this client's player ID and the value "disconnect"
        JSONObject json = new JSONObject();
        json.put("ID", clientID);
        json.put("disconnect", true);
        server.messageHandler.handle(new MessageEvent(json.toString()));
    }
}
