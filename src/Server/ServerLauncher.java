package Server;

import Events.EventHandler;
import Events.FrameEvent.FrameEvent;
import Events.GameEndEvent;
import GameManager.GameManager;
import Global.Settings;
import MessageEvent.MessageEvent;
import MessageEvent.MessageHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;

public class ServerLauncher {

    private GameManager game; // game simulation
    private Server server; // server

    public ServerLauncher(Stage primaryStage, int port) throws Exception {
        Settings.setServer();

        // Initialize variables
        ServerProtocol protocol = new ServerProtocol(); // protocol controlling the interaction between the game and the

        // Set up the game simulation
        game = new GameManager(new EventHandler<FrameEvent>() {
            @Override
            public void handle(FrameEvent fe) {
                protocol.handleFrameUpdate(fe);
            }
        });
        game.setGameEndHandler(new EventHandler<GameEndEvent>() {
            @Override
            public void handle(GameEndEvent event) {
                handleGameEnd();
            }
        });

        // Set up the server
        server = new Server(port, new MessageHandler() {
            @Override
            public void handle(MessageEvent event) {
                protocol.handleMessage(event);
            }
        });
        server.start();
        Launcher.watchServer(server);

        // Start the protocol
        protocol.prepare(server, game);

        // Set up the visuals
        Scene scene = new Scene(game, Settings.getWindowWidth(), Settings.getWindowHeight());
        primaryStage.setScene(scene);
        primaryStage.show();

        // start the game
        game.start(scene, true);
    }

    public void handleGameEnd(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                game.reset();
            }
        }, 3 * 1000);
    }
}
