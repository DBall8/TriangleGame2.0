package Client;

import GameManager.GameManager;
import Events.EventHandler;
import Events.FrameEvent.FrameEvent;
import Global.Settings;
import MessageEvent.*;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GameLauncher{

    // Initialize variables
    GameManager game; // game simulation
    ClientConnection conn; // connection to server
    ClientProtocol protocol = new ClientProtocol(); // Protocol for connection the game to the server

    public GameLauncher(Stage primaryStage, String ip, int port) throws IOException{

        // Build the game
        game = new GameManager(new EventHandler<FrameEvent>() {
            @Override
            public void handle(FrameEvent fe) {
                protocol.handleFrameUpdate(fe);
            }
        });

        // Set up the connection
        conn = new ClientConnection(ip, port, new MessageHandler() {
            @Override
            public void handle(MessageEvent event) {
                protocol.handleMessage(event);
            }
        });

        // Set up the protocol
        protocol.prepare(conn, game);

        // Launch visuals
        Scene scene = new Scene(game, Settings.getWindowWidth(), Settings.getWindowHeight());

        primaryStage.setScene(scene);
        primaryStage.show();

        // start the game
        game.start(scene, false);
    }


}
