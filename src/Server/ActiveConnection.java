package Server;

import MessageEvent.MessageEvent;
import MessageEvent.MessageHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ActiveConnection {

    private Socket socket;

    private PrintWriter out;
    private BufferedReader in;

    private Thread listenerThread;
    private MessageHandler messageHandler;

    public ActiveConnection(Socket socket, MessageHandler messageHandler){
        this.socket = socket;
        this.messageHandler = messageHandler;

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

    private void send(String msg){
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

        System.out.println("Client connection closed.");
        // Remove connection from server's connection list
    }
}
