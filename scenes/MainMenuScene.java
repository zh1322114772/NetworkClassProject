package b451_Project.scenes;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import b451_Project.global.*;

// this is the main menu scene of the game

public class MainMenuScene extends SceneBase {

    private Button joinButton = null;
    private Button hostGameButton = null;
    private TextField ipTextField = null;
    private Label ipLabel = null;

    public MainMenuScene()
    {

        //init pane, text-field and buttons
        joinButton = new Button("Join a Game");
        hostGameButton = new Button("Host a Game");
        ipTextField = new TextField();
        ipLabel = new Label("Ip: ");

        //add host game, join game buttons to pane
        //add a text-field to enter ip address to pane
        pane.getChildren().add(joinButton);
        pane.getChildren().add(hostGameButton);
        pane.getChildren().add(ipTextField);
        pane.getChildren().add(ipLabel);
    }

    @Override
    protected void sceneSizeChanged(double w, double h) {

        //relocate text-field
        ipTextField.setLayoutX(w * 0.35);
        ipTextField.setLayoutY(h * 0.32);
        ipTextField.setPrefWidth(w * 0.3);
        ipTextField.setPrefHeight(h * 0.05);

        //relocate label
        ipLabel.setLayoutX(w * 0.3);
        ipLabel.setLayoutY(h * 0.32);
        ipLabel.setPrefWidth(w * 0.05);
        ipLabel.setPrefHeight(h * 0.05);

        //relocate buttons
        joinButton.setLayoutX(w * 0.4);
        joinButton.setLayoutY(h * 0.4);
        joinButton.setPrefWidth( w * 0.2);
        joinButton.setPrefHeight(h * 0.05);

        hostGameButton.setLayoutX( w * 0.4);
        hostGameButton.setLayoutY(h * 0.45);
        hostGameButton.setPrefWidth(w * 0.2);
        hostGameButton.setPrefHeight(h * 0.05);

        //when user click host a game
        hostGameButton.setOnAction((e) ->
        {
            WindowProperties.game.switchScene(WindowProperties.GameScene);
        });


    }

    @Override
    protected void sceneMouseEvent(double posX, double posY, boolean p) {

    }

    @Override
    protected void sceneRedraw(double deltaT) {

    }

    @Override
    protected void sceneKeyEvent(KeyCode k, boolean p) {

    }


}
