package client.screenControllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ScreenController {

    protected void switchScreen(Stage stage, String url) throws IOException{
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource(url));
        stage.setScene(new Scene(root));
    }
}
