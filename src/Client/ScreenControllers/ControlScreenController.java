package client.screenControllers;

import client.GameLauncher;
import gameManager.userInputHandler.UserInputHandler;
import global.Settings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControlScreenController extends ScreenController implements Initializable {
    @FXML
    private Button fButton;
    @FXML
    private Button revButton;
    @FXML
    private Button rButton;
    @FXML
    private Button lButton;
    @FXML
    private Button aimButton;
    @FXML
    private Button sButton;
    @FXML
    private Button bButton;
    @FXML
    private Button a1Button;
    @FXML
    private Button a2Button;

    @FXML
    private Button backButton;

    private UserInputHandler inputHandler;

    @FXML
    private void buttonAction(ActionEvent e){
        UserInputHandler.Binding binding = null;
        if(e.getSource() == fButton) binding = UserInputHandler.Binding.UP;
        if(e.getSource() == revButton) binding = UserInputHandler.Binding.DOWN;
        if(e.getSource() == rButton) binding = UserInputHandler.Binding.RIGHT;
        if(e.getSource() == lButton) binding = UserInputHandler.Binding.LEFT;
        if(e.getSource() == aimButton) binding = UserInputHandler.Binding.AIM;
        if(e.getSource() == sButton) binding = UserInputHandler.Binding.SHOOT;
        if(e.getSource() == bButton) binding = UserInputHandler.Binding.BOOST;
        if(e.getSource() == a1Button) binding = UserInputHandler.Binding.ABILITY1;
        if(e.getSource() == a2Button) binding = UserInputHandler.Binding.ABILITY2;

        if(binding != null) {
            waitForKeyPress((Button)e.getSource(), binding);
        }
    }

    @FXML
    private void backButtonAction(ActionEvent e){

        Stage stage = (Stage) backButton.getScene().getWindow();
        if(GameLauncher.getStatus() == GameLauncher.GAMESTATUS.UNSTARTED){
            try{
                switchScreen(stage, "ClientLaunchScreen.fxml");
            } catch (IOException err){
                //showError("Problem switchingn screens. Somebody messed up real bad good luck to them.");
                err.printStackTrace();
            }
        }
        else{
            GameLauncher.show(stage);
        }

    }

    private void waitForKeyPress(Button b, UserInputHandler.Binding binding){

        b.setDisable(true);
        b.setText("Press any key");
        Scene scene = b.getScene();

        scene.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                inputHandler.setBinding(binding, event.getCode());
                b.setDisable(false);
                scene.removeEventHandler(KeyEvent.KEY_PRESSED, this);
                b.setText(inputHandler.getBoundKey(binding));
            }
        });
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inputHandler = Settings.getUserInputHandler();

        fButton.setText(inputHandler.getBoundKey(UserInputHandler.Binding.UP));
        revButton.setText(inputHandler.getBoundKey(UserInputHandler.Binding.DOWN));
        rButton.setText(inputHandler.getBoundKey(UserInputHandler.Binding.LEFT));
        lButton.setText(inputHandler.getBoundKey(UserInputHandler.Binding.RIGHT));
        aimButton.setText(inputHandler.getBoundKey(UserInputHandler.Binding.AIM));
        sButton.setText(inputHandler.getBoundKey(UserInputHandler.Binding.SHOOT));
        bButton.setText(inputHandler.getBoundKey(UserInputHandler.Binding.BOOST));
        a1Button.setText(inputHandler.getBoundKey(UserInputHandler.Binding.ABILITY1));
        a2Button.setText(inputHandler.getBoundKey(UserInputHandler.Binding.ABILITY2));
    }
}
