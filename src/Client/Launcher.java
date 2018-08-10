package client;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Launches the client
 */
public class Launcher extends Application {

    public static ClientConnection connection;


    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root;
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("ClientLaunchScreen.fxml"));
            primaryStage.setTitle("Triangle Game");
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

    @Override
    public void stop(){
        if(connection != null){
            connection.close();
        }
        System.exit(0);
    }
}

