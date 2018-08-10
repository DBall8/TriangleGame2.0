package client;

import messageEvent.MessageEvent;
import messageEvent.MessageHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Class for connecting to a game server
 */
public class ClientConnection {

    private Socket socket; // socket connecting to the server

    private PrintWriter out; // for writing messages
    private BufferedReader in; // for reading messages

    private Thread listenerThread; // a thread continually listening for messages

    private MessageHandler messageHandler; // class for sending received messages to

    /**
     * Constructor for ClientConnection
     * @param url server's url
     * @param port server's port
     * @param messageHandler the object to send all received messages to
     * @throws IOException throws when unable to connect to server
     */
    public ClientConnection(String url , int port, MessageHandler messageHandler) throws IOException{

        this.messageHandler = messageHandler;

        try {

            socket = new Socket(url, port);

            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("Connection established.");

            // Once set up properly, start listening for messages
            listen();

        } catch (IOException e) {
            System.err.println("Could not establish connection.");
            throw e;
        }


    }

    /**
     * Sends a message to the server
     * @param msg the message to send
     */
    public void send(String msg){
        // make sure out stream is ready
        if(out == null){
            System.err.println("Could not send message. No open write stream.");
            return;
        }
        out.println(msg);
    }

    /**
     * Continuously listends for messages from the server
     */
    private void listen(){
        listenerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String msg;
                try {
                    while ((msg = in.readLine()) != null) {
                        // pass any message to the message handler
                        messageHandler.handle(new MessageEvent(msg));
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
     * Closes up the connection
     */
    public void close(){
        // If still listening, stop listening
        if(listenerThread != null){
            listenerThread.interrupt();
        }

        // Close the connection
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Connection closed.");
    }
}
