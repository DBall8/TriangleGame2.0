package Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    private void buttonAction(ActionEvent e){
        if(e.getSource() == joinGameButton) {
            String ip = ipField.getText();
            int port;
            try {
                port = Integer.parseInt(portField.getText());
            } catch (NumberFormatException err){
                System.err.println("Not a valid port number.");
                return;
            }

            if(ip == null || ip.equals("") || port < 0){
                System.out.println("Please provide a valid IP and port.");
            }
            else{
                Stage stage = (Stage) joinGameButton.getScene().getWindow();
                try {
                    new GameLauncher(stage, ip, port);
                } catch (IOException err){
                    System.out.println("Could not connect to server with address " + ip + ":" + port);
                    err.printStackTrace();
                }
            }
        }
    }
}
