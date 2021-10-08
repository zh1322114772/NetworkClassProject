package compnetproject.computernetworkproject;

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

import java.io.IOException;

public class ComputerNetworkProgrammingProject extends Application {

    Circle[] asteroids = new Circle[10];
    Polygon ship = new Polygon();
    boolean collision = false;

    @Override
    public void start(Stage stage) throws IOException {
        Group root = new Group();
        Scene scene = new Scene(root, 1280, 720);
        stage.setTitle("Hello!");
        stage.setScene(scene);

        Group grp = new Group();
        grp.getChildren().add(ship);
        player(scene);
        asteroid(grp);
        scene.setRoot(grp);
        stage.show();


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
                case S: if(ship.getLayoutY() < 340) ship.setLayoutY(ship.getLayoutY() + 5); checkCollision(); break;
                case W: if(ship.getLayoutY() > -340) ship.setLayoutY(ship.getLayoutY() - 5); checkCollision(); break;
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
            asteroids[i].setCenterX((Math.random() * 1240) + 1200);
            asteroids[i].setCenterY(Math.random() * 720);
            asteroids[i].setFill(Color.BROWN);
            g.getChildren().add(asteroids[i]);
            asteroidMovement(asteroids[i]);
        }
    }

    public void asteroidMovement(Circle c)
    {
        Path path = new Path();
        path.getElements().add(new MoveTo(c.getCenterX(),c.getCenterY()));
        path.getElements().add(new HLineTo(120));

        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.seconds(5));
        pathTransition.setDelay(Duration.seconds(0));
        pathTransition.setPath(path);
        pathTransition.setNode(c);
        pathTransition.setCycleCount(1);
        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransition.setOnFinished(actionEvent -> checkCollision());
        pathTransition.play();
        if(c.getCenterX() == 0)
        {
            c.setCenterX(1240);
            c.setCenterY(Math.random() * 720);
        }

    }

    public void checkCollision()
    {
        for(int i = 0; i < asteroids.length; i++)
        {
            Shape intersect = Circle.intersect(asteroids[i], ship);
            if(intersect.getBoundsInLocal().getWidth() != -1)
            {
                collision = true;
            }
        }

        if(collision)
        {
            //pathTransition.stop();
            System.out.println("Collision");
        }
    }

    public static void main(String[] args) {
        launch();
    }
}