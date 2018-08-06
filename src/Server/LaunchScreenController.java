package Server;

import Client.GameLauncher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.event.KeyEvent;
import java.io.IOException;

public class LaunchScreenController {

    @FXML
    private TextField portField;
    @FXML
    private Button launchServerButton;
    @FXML
    private Label errorText;

    @FXML
    private void buttonAction(ActionEvent e){
        if(e.getSource() == launchServerButton) {
            int port;
            try {
                port = Integer.parseInt(portField.getText());
            } catch (NumberFormatException err){
                showError("Not a valid port number.");
                return;
            }

            if(port < 0){
                showError("Please provide a valid port, greater than 0.");
            }
            else{
                Stage stage = (Stage) launchServerButton.getScene().getWindow();
                try {
                    new ServerLauncher(stage, port);
                } catch (Exception err){
                    showError("Could not launch server on port " + port);
                    err.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void fieldChanged(){
        hideError();
    }

    private void hideError(){
        if(errorText.isVisible()){
            errorText.setVisible(false);
        }
    }

    private void showError(String text){
        errorText.setText(text);
        errorText.setVisible(true);
    }
}
