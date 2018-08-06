package Server;

import Client.GameLauncher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LaunchScreenController {

    @FXML
    private TextField portField;
    @FXML
    private Button launchServerButton;

    @FXML
    private void buttonAction(ActionEvent e){
        if(e.getSource() == launchServerButton) {
            int port;
            try {
                port = Integer.parseInt(portField.getText());
            } catch (NumberFormatException err){
                System.err.println("Not a valid port number.");
                return;
            }

            if(port < 0){
                System.out.println("Please provide a valid IP and port.");
            }
            else{
                Stage stage = (Stage) launchServerButton.getScene().getWindow();
                try {
                    new ServerLauncher(stage, port);
                } catch (Exception err){
                    System.out.println("Could not launch server on port " + port);
                    err.printStackTrace();
                }
            }
        }
    }
}
