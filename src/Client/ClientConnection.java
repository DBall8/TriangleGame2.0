package Client;

import MessageEvent.MessageEvent;
import MessageEvent.MessageHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientConnection {

    private Socket socket;

    private PrintWriter out;
    private BufferedReader in;

    private Thread listenerThread;

    private MessageHandler messageHandler;

    public ClientConnection(String url , int port, MessageHandler messageHandler) throws IOException{

        this.messageHandler = messageHandler;

        try {

            socket = new Socket(url, port);

            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("Connection established.");

            listen();

        } catch (IOException e) {
            System.err.println("Could not establish connection.");
            throw e;
        }


    }

    public void send(String msg){
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
        if(listenerThread != null){
            listenerThread.interrupt();
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Connection closed.");
    }
}
