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

public class Launcher extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        Settings.setWindowSize(800, 800);

        GameManager game;
        Server server;
        ServerProtocol protocol = new ServerProtocol();

        game = new GameManager(new FrameEventHandler() {
            @Override
            public void handle(FrameEvent fe) {
                protocol.handleFrameUpdate(fe);
            }
        });

        Scene scene = new Scene(game, 800, 800);
        primaryStage.setTitle("Server");
        primaryStage.setScene(scene);
        primaryStage.show();

        game.start(scene);

        server = new Server(8080, new MessageHandler() {
            @Override
            public void handle(MessageEvent event) {
                protocol.handleMessage(event);
            }
        });
        server.start();

        protocol.prepare(server, game);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop(){
        System.exit(0);
    }
}

