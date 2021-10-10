package b451_Project;

import b451_Project.Scenes.SceneBase;
import b451_Project.Static.WindowProperties;

import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.action.Action;
//import org.w3c.dom.Text;
import javafx.scene.text.Text;

import java.io.IOException;

public class ComputerNetworkProgrammingProject extends Application {

    Circle[] asteroids = new Circle[10];
    PathTransition[] asteroidPath = new PathTransition[10];
    Polygon ship = new Polygon();
    int velocity = 2;
    int acceleration = 2;
    KeyCode key = KeyCode.W; //Used to check for previous button press
    boolean collision = false;
    Group grp = new Group();

    private SceneBase currentScene = null;

    @Override
    public void start(Stage stage) throws IOException {

        WindowProperties.stage = stage;
        stage.setTitle("Hello!");
        stage.setResizable(false);
        switchScene(WindowProperties.MainMenuScene);
        stage.show();

        //Group root = new Group();
        //Scene scene = new Scene(root, 1280, 720);
        //stage.setScene(scene);
        //grp.getChildren().add(ship);
        //player(scene);
        //asteroid(grp);
        //scene.setRoot(grp);



    }

    @Override
    public void stop()
    {
        currentScene.disable();
    }

    /**
     * switch stage scene
     * @param s target scene
     * */
    private void switchScene(SceneBase s)
    {
        if(currentScene != null)
        {
            currentScene.disable();
        }

        currentScene = s;
        WindowProperties.stage.setScene(currentScene.getScene());
        s.enable();
    }

    public void player(Scene s) throws IOException
    {
        ship.getPoints().addAll(new Double[]{160.0, 360.0,
                100.0, 340.0,
                100.0, 380.0});
        ship.setFill(Color.BLUE);

        playerMovement(s);
    }

    public void playerMovement(Scene s) throws IOException
    {
        s.setOnKeyPressed(event -> {
            switch (event.getCode())
            {
                case S:
                    if(key != event.getCode()) velocity = 2; //Reset velocity to 2 if the ship changes direction.
                    if(ship.getLayoutY() < 340) {
                        ship.setLayoutY(ship.getLayoutY() + velocity);
                        velocity = (velocity+acceleration > 20)?20:velocity+acceleration; //Set velocity limit to 20
                    }
                    key = event.getCode();
                    break;
                case W:
                    if(key != event.getCode()) velocity = 2;
                    if(ship.getLayoutY() > -340) {
                        ship.setLayoutY(ship.getLayoutY() - velocity);
                        velocity = (velocity+acceleration > 20)?20:velocity+acceleration;
                    }
                    key = event.getCode();
                    break;
            }
        });
    }

    public void asteroid(Group g)
    {

        for(int i = 0; i < 10; i++)
        {
            asteroids[i] = new Circle();
            asteroids[i].setRadius(20);
            asteroids[i].setCenterX((Math.random() * 1240) + 600);
            asteroids[i].setCenterY(Math.random() * 720);
            asteroids[i].setFill(Color.BROWN);
            g.getChildren().add(asteroids[i]);
            asteroidMovement(asteroids[i], i);
        }
    }

    public void asteroidMovement(Circle ast, int i)
    {
        Path path = new Path();
        path.getElements().add(new MoveTo(ast.getCenterX(),ast.getCenterY()));
        path.getElements().add(new HLineTo(120));

        asteroidPath[i] = new PathTransition();
        asteroidPath[i].setDuration(Duration.seconds(Math.random()*5 + 3));
        asteroidPath[i].setDelay(Duration.seconds(0));
        asteroidPath[i].setPath(path);
        asteroidPath[i].setNode(ast);
        asteroidPath[i].setCycleCount(1);
        asteroidPath[i].setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        asteroidPath[i].setOnFinished(actionEvent -> {
            checkCollision();
            if(!collision) asteroidPath[i].play();
        });
        asteroidPath[i].play();
    }

    public void checkCollision()
    {
        for(int i = 0; i < asteroids.length; i++)
        {
            Shape intersect = Circle.intersect(asteroids[i], ship);
            if(intersect.getBoundsInLocal().getWidth() != -1)
            {
                collision = true;
                break;
            }
        }

        if(collision) //Stop all pathTransition when collision is true
        {
            for(int i = 0; i < 10; i++)
            {
                pathTransitions[i].stop();
            }
            Text collisionText = new Text();
            collisionText.setText("Ship collided with asteroid.");
            collisionText.setX(600);
            collisionText.setY(340);
            grp.getChildren().add(collisionText);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
