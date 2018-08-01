package Client;
import MessageEvent.MessageEvent;
import MessageEvent.MessageHandler;
import GameManager.FrameEvent.IFrameEvent;
import GameManager.FrameEvent.FrameEventHandler;
import GameManager.GameManager;
import Global.Settings;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Launcher extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        Settings.setWindowSize(800, 800);

        GameManager game;
        ClientConnection conn;
        ClientProtocol protocol = new ClientProtocol();

        game = new GameManager(new FrameEventHandler() {
            @Override
            public void handle(IFrameEvent fe) {
                protocol.handleFrameUpdate(fe);
            }
        });

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

        protocol.prepare(conn, game);

        Scene scene = new Scene(game, 800, 800);

        primaryStage.setTitle("Client");
        primaryStage.setScene(scene);
        primaryStage.show();

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

