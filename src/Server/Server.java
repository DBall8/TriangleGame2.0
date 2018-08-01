package Server;

import MessageEvent.MessageHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket server;
    private MessageHandler messageHandler;

    public Server(int port, MessageHandler messageHandler){

        this.messageHandler = messageHandler;

        try {
            server = new ServerSocket(port);

        } catch (IOException e) {
            System.out.println("Could open server.");
            e.printStackTrace();
        }
    }

    public void start(){

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Listening on " + server.getInetAddress());

                while(true){
                    try {
                        Socket client = server.accept();
                        new ActiveConnection(client, messageHandler);
                        // Create a client connection

                    } catch (IOException e) {
                        System.out.println("Error when acceptiong a client connection.");
                        e.printStackTrace();
                        break;
                    }
                }

                try {
                    server.close();
                    System.out.println("Server closed.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.setDaemon(true);
        t.start();

    }
}
