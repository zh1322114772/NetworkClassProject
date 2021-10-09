package b451_Project.Scenes;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;


// this is the main menu scene of the game

public class MainMenuScene implements SceneBase {

    private Scene scene = null;
    private Pane pane = null;

    private Button quitButton = null;
    private Button joinButton = null;
    private Button hostGameButton = null;

    private TextField ipTextField = null;
    private Label ipLabel = null;

    @Override
    public Scene getScene() {
        return scene;
    }


    public MainMenuScene()
    {
        //init pane, scene, text-field and buttons
        pane = new Pane();
        scene = new Scene(pane);
        quitButton = new Button("Quit");
        joinButton = new Button("Join a Game");
        hostGameButton = new Button("Host a Game");
        ipTextField = new TextField();
        ipLabel = new Label("Ip: ");

        //add size changed event listener
        scene.widthProperty().addListener((observable, oldValue, newValue) ->
        {
            //relocate components
        });

        scene.heightProperty().addListener((observable, oldValue, newValue) ->
        {
            //relocate components


        });

        //add host game, join game and quit buttons to pane
        //add a textbox to enter ip address to pane
        pane.getChildren().add(quitButton);
        pane.getChildren().add(joinButton);
        pane.getChildren().add(hostGameButton);
        pane.getChildren().add(ipTextField);
    }

}
