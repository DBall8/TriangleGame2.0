package Client.ScreenControllers;

import Client.GameLauncher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LaunchScreenController {

    @FXML
    private TextField ipField;
    @FXML
    private TextField portField;
    @FXML
    private Button joinGameButton;
    @FXML
    private Label errorText;

    @FXML
    private void buttonAction(ActionEvent e){
        if(e.getSource() == joinGameButton) {
            String ip = ipField.getText();
            int port;
            try {
                port = Integer.parseInt(portField.getText());
            } catch (NumberFormatException err){
                showError("Port is not a valid number.");
                return;
            }

            if(ip == null || ip.equals("") || port < 0){
                showError("Please provide a valid IP and port.");
            }
            else{
                Stage stage = (Stage) joinGameButton.getScene().getWindow();
                try {
                    new GameLauncher(stage, ip, port);
                } catch (IOException err){
                    showError("Could not connect to server with address " + ip + ":" + port);
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
