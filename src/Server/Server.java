package Server;

import MessageEvent.MessageHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class Server {

    private ServerSocket server;
    MessageHandler messageHandler;

    private List<ActiveConnection> connections;

    public Server(int port, MessageHandler messageHandler){

        this.messageHandler = messageHandler;

        connections = new LinkedList<ActiveConnection>();

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
                        addConnection(client);

                    } catch (IOException e) {
                        System.out.println("Error when acceptiong a client connection.");
                        e.printStackTrace();
                        break;
                    }
                }

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
        });
        t.setDaemon(true);
        t.start();

    }

    public synchronized void broadcast(String msg){
        for(ActiveConnection conn: connections){
            conn.send(msg);
        }
    }


    private synchronized void addConnection(Socket s){
        connections.add(new ActiveConnection(s, this));
    }
    synchronized void removeConnection(ActiveConnection connection){
        connections.remove(connection);
    }
}
