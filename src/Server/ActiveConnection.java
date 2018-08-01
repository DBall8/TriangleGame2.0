package Server;

import MessageEvent.MessageEvent;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ActiveConnection {

    private Socket socket;
    private String clientID;

    private PrintWriter out;
    private BufferedReader in;

    private Thread listenerThread;
    private Server server;

    public ActiveConnection(Socket socket, Server server){
        this.socket = socket;
        this.server = server;

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Connection with client at " + socket.getInetAddress() + " established.");

            listen();

        } catch(IOException e){
            System.err.println("Could not create connection with client.");
            e.printStackTrace();
        }
    }

    protected void send(String msg){
        if(out == null){
            System.err.println("Could not send message. No open write stream.");
            return;
        }
        out.println(msg);
    }

    private void listen(){
        listenerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String msg;
                try {
                    while ((msg = in.readLine()) != null) {
                        MessageEvent event = new MessageEvent(msg);
                        if(clientID == null){
                            clientID = event.getID();
                        }
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

    public void close(){
        if(listenerThread != null) {
            listenerThread.interrupt();
        }

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

    private void sendDisconnect(){
        JSONObject json = new JSONObject();
        json.put("ID", clientID);
        json.put("disconnect", true);
        server.messageHandler.handle(new MessageEvent(json.toString()));
    }
}
