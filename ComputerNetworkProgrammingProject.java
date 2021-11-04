package b451_Project;

import b451_Project.global.GameVariables;
import b451_Project.scenes.SceneBase;
import b451_Project.global.WindowVariables;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;

public class ComputerNetworkProgrammingProject extends Application {

    private SceneBase currentScene = null;
    private Stage stage = null;


    @Override
    public void start(Stage stage) throws IOException {

        this.stage = stage;
        WindowVariables.game = this;

        //init window
        stage.setTitle("Hello!");
        stage.setResizable(false);
        switchScene(WindowVariables.mainMenuScene);
        stage.show();

    }

    @Override
    public void stop()
    {
        currentScene.disable();

        //stop client and server
        if(GameVariables.server != null)
        {
            GameVariables.server.stop();
        }

        if(GameVariables.client != null)
        {
            GameVariables.client.stop();
        }
    }

    /**
     * switch stage scene
     * @param s target scene
     * */
    public void switchScene(SceneBase s)
    {
        Platform.runLater(() ->
        {
            if(currentScene != null)
            {
                currentScene.disable();
            }

            currentScene = s;
            stage.setScene(currentScene.getScene());
            s.enable();
        });
    }



    public static void main(String[] args) {
        launch();
    }
}
