package Server;
import Events.EventHandler;
import Events.FrameEvent.FrameEvent;
import GameManager.GameManager;
import Global.Settings;
import MessageEvent.MessageEvent;
import MessageEvent.MessageHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Class for launching a game server
 */
public class Launcher extends Application {

    private static Server server;

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root;
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("ServerLaunchScreen.fxml"));
            primaryStage.setTitle("Server");
            primaryStage.setScene(new Scene(root, 800, 800));
            primaryStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void watchServer(Server server){
        Launcher.server = server;
    }

    @Override
    public void stop(){
        // Make sure server is closed all the way so it doesn't clog up any ports
        if(server != null){
            server.close();
        }
        System.exit(0);
    }
}

