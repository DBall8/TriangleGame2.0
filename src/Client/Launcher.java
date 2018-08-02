package Client;
import MessageEvent.MessageEvent;
import MessageEvent.MessageHandler;
import GameManager.FrameEvent.FrameEvent;
import GameManager.FrameEvent.FrameEventHandler;
import GameManager.GameManager;
import Global.Settings;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Launches the client
 */
public class Launcher extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        // Set the window dimensions
        //Settings.setWindowSize(800, 800);

        // Initialize variables
        GameManager game; // game simulation
        ClientConnection conn; // connection to server
        ClientProtocol protocol = new ClientProtocol(); // Protocol for connection the game to the server

        // Build the game
        game = new GameManager(new FrameEventHandler() {
            @Override
            public void handle(FrameEvent fe) {
                protocol.handleFrameUpdate(fe);
            }
        });

        // Set up the connection
        try {
            conn = new ClientConnection("127.0.0.1", 8080, new MessageHandler() {
                @Override
                public void handle(MessageEvent event) {
                    protocol.handleMessage(event);
                }
            });
        } catch(IOException e){
            System.out.println("Could not establish connection to a server.");
            return;
        }

        // Set up the protocol
        protocol.prepare(conn, game);

        // Launch visuals
        Scene scene = new Scene(game, Settings.getWindowWidth(), Settings.getWindowHeight());

        primaryStage.setTitle("Client");
        primaryStage.setScene(scene);
        primaryStage.show();

        // start the game
        game.start(scene, false);

    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop(){
        System.exit(0);
    }
}

