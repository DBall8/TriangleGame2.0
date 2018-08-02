package Server;
import GameManager.FrameEvent.FrameEvent;
import GameManager.FrameEvent.FrameEventHandler;
import GameManager.GameManager;
import Global.Settings;
import MessageEvent.MessageEvent;
import MessageEvent.MessageHandler;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Class for launching a game server
 */
public class Launcher extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        // Set window dimensions
        Settings.setWindowSize(800, 800);

        // Initialize variables
        GameManager game; // game simulation
        Server server; // server
        ServerProtocol protocol = new ServerProtocol(); // protocol controlling the interaction between the game and the
                                                        // server

        // Set up the game simulation
        game = new GameManager(new FrameEventHandler() {
            @Override
            public void handle(FrameEvent fe) {
                protocol.handleFrameUpdate(fe);
            }
        });

        // Set up the server
        server = new Server(8080, new MessageHandler() {
            @Override
            public void handle(MessageEvent event) {
                protocol.handleMessage(event);
            }
        });
        server.start();

        // Start the protocol
        protocol.prepare(server, game);

        // Set up the visuals
        Scene scene = new Scene(game, 800, 800);
        primaryStage.setTitle("Server");
        primaryStage.setScene(scene);
        primaryStage.show();

        // start the game
        game.start(scene, true);


    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop(){
        System.exit(0);
    }
}

