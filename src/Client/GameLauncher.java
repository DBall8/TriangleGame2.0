package client;

import client.screenControllers.ScreenController;
import events.GameEndEvent;
import gameManager.GameManager;
import events.EventHandler;
import events.frameEvent.FrameEvent;
import global.Settings;
import messageEvent.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class GameLauncher extends ScreenController {

    // Initialize variables
    static GameManager game; // game simulation
    static ClientConnection conn; // connection to server
    static ClientProtocol protocol = new ClientProtocol(); // Protocol for connection the game to the server

    static Scene scene = null;

    public enum GAMESTATUS{
        UNSTARTED,
        PLAYING,
        PAUSED,
        WAITING
    }

    private static GAMESTATUS status = GAMESTATUS.UNSTARTED;


    private GameLauncher(){}

    public static void set(String ip, int port) throws IOException{

        // Build the game
        getInstance().game = new GameManager(new EventHandler<FrameEvent>() {
            @Override
            public void handle(FrameEvent fe) {
                protocol.handleFrameUpdate(fe);
            }
        });
        getInstance().game.setGameEndHandler(new EventHandler<GameEndEvent>() {
            @Override
            public void handle(GameEndEvent event) {
                handleGameEnd();
            }
        });

        // Set up the connection
        getInstance().conn = new ClientConnection(ip, port, new MessageHandler() {
            @Override
            public void handle(MessageEvent event) {
                protocol.handleMessage(event);
            }
        });

        // Set up the protocol
        getInstance().protocol.prepare(getInstance().conn, getInstance().game);
    }

    public static void show(Stage primaryStage){
        // Launch visuals
        if(getInstance().scene == null) {
            getInstance().scene = new Scene(getInstance().game, Settings.getWindowWidth(), Settings.getWindowHeight());
        }

        primaryStage.setScene(getInstance().scene);
        primaryStage.show();

        getInstance().scene.addEventHandler(KeyEvent.KEY_PRESSED, new javafx.event.EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ESCAPE){
                    if(getInstance().status == GAMESTATUS.PLAYING){
                        getInstance().status = GAMESTATUS.PAUSED;
                        getInstance().game.pause();

                        Parent root = null;
                        try {
                            root = FXMLLoader.load(getClass().getClassLoader().getResource("ControlScreen.fxml"));
                            primaryStage.setScene(new Scene(root));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                    else if(getInstance().status == GAMESTATUS.PAUSED){
                        getInstance().status = GAMESTATUS.PLAYING;
                        getInstance().game.play();
                    }
                }
            }
        });

        if(getInstance().status == GAMESTATUS.UNSTARTED){
            getInstance().status = GAMESTATUS.PLAYING;
            // start the game
            getInstance().game.start(scene, false);
        }
        else{
            getInstance().status = GAMESTATUS.PLAYING;
            // start the game
            getInstance().game.play();
        }
    }

    private static void handleGameEnd(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                game.reset();
            }
        }, 3 * 1000);
    }

    public static GAMESTATUS getStatus(){ return getInstance().status; }


    private static class SingletonHelper{
        private final static GameLauncher instance = new GameLauncher();
    }

    public static GameLauncher getInstance(){ return SingletonHelper.instance; }

}
