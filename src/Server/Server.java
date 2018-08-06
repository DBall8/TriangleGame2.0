package Server;

import MessageEvent.MessageHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

/**
 * Class for creating a server that listens for connecting game clients
 */
public class Server {

    private ServerSocket server; // the server socket
    MessageHandler messageHandler; // the object to handle all messages received

    private List<ActiveConnection> connections; // a list of all connected clients

    Thread listener;

    /**
     * Constructor
     * @param port the port for the server to listen on
     * @param messageHandler the object to handle all received messages
     */
    public Server(int port, MessageHandler messageHandler) throws Exception{

        this.messageHandler = messageHandler;

        connections = new LinkedList<ActiveConnection>();

        try {
            server = new ServerSocket(port);

        } catch (Exception e) {
            close();
            throw e;
        }
    }

    /**
     * Starts the server running
     */
    public void start(){

        listener = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Listening on " + server.getInetAddress() + ":" + server.getLocalPort());

                // Continuously listens for new clients
                while(true){
                    try {
                        // Once a new client is accepted, create a new connection and keep listening
                        Socket client = server.accept();
                        addConnection(client);

                    } catch (IOException e) {
                        System.out.println("Error when acceptiong a client connection.");
                        e.printStackTrace();
                        break;
                    }
                }

                close();
            }
        });
        listener.setDaemon(true);
        listener.start();

    }

    public void close(){

        if(listener != null){
            listener.interrupt();
        }

        // Once closed, close all connections.
        try {
            for(ActiveConnection c: connections){
                c.close();
            }
            server.close();
            System.out.println("Server closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Sends a message to all connected clients
     * @param msg the message to send
     */
    public synchronized void broadcast(String msg){
        for(ActiveConnection conn: connections){
            conn.send(msg);
        }
    }

    /**
     * Adds a new client connection to the list
     * @param s the socket to use in creating the connection
     */
    private synchronized void addConnection(Socket s){
        connections.add(new ActiveConnection(s, this));
    }

    /**
     * Removes a client connection from the list
     * @param connection the connection to remove
     */
    synchronized void removeConnection(ActiveConnection connection){
        connections.remove(connection);
    }
}
