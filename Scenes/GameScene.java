package b451_Project.Scenes;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Circle;

//this scene displays game content
public class GameScene extends SceneBase {

    Circle asd = null;

    public GameScene()
    {
        asd = new Circle(20);
        pane.getChildren().add(asd);
    }

    @Override
    public void enable()
    {
        super.enable();


    }

    @Override
    public void disable()
    {
        super.disable();

    }


    @Override
    protected void sceneSizeChanged(double w, double h) {

    }

    @Override
    protected void sceneMouseEvent(double posX, double posY, boolean p) {

    }

    @Override
    protected void sceneRedraw(double deltaT) {
        asd.setCenterX(100);
    }

    @Override
    protected void sceneKeyEvent(KeyCode k, boolean p) {

    }

}
