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
    PathTransition[] pathTransitions = new PathTransition[10];
    Polygon ship = new Polygon();
    boolean collision = false;
    Group grp = new Group();

    private SceneBase currentScene = null;
    private Stage stage = null;


    @Override
    public void start(Stage stage) throws IOException {

        this.stage = stage;
        WindowProperties.game = this;

        //init window
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
    public void switchScene(SceneBase s)
    {
        if(currentScene != null)
        {
            currentScene.disable();
        }

        currentScene = s;
        stage.setScene(currentScene.getScene());
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
                case S: if(ship.getLayoutY() < 340) ship.setLayoutY(ship.getLayoutY() + 5); break;
                case W: if(ship.getLayoutY() > -340) ship.setLayoutY(ship.getLayoutY() - 5); break;
                default: checkCollision(); break;
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

    public void asteroidMovement(Circle c, int i)
    {
        Path path = new Path();
        path.getElements().add(new MoveTo(c.getCenterX(),c.getCenterY()));
        path.getElements().add(new HLineTo(120));

        pathTransitions[i] = new PathTransition();
        pathTransitions[i].setDuration(Duration.seconds(5));
        pathTransitions[i].setDelay(Duration.seconds(0));
        pathTransitions[i].setPath(path);
        pathTransitions[i].setNode(c);
        pathTransitions[i].setCycleCount(1);
        pathTransitions[i].setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransitions[i].setOnFinished(actionEvent -> {
            checkCollision();
            c.setCenterX((Math.random() * 1240) + 1200);
            c.setCenterY(Math.random() * 720);
        });
        pathTransitions[i].play();
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
        else //Repeat pathTransition for asteroids when collision is false
        {
            for(int i = 0; i < 10; i++)
            {
                pathTransitions[i].play();
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
