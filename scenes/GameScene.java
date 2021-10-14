package b451_Project.scenes;

import b451_Project.render.RenderFactory;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;

//this scene displays game content
public class GameScene extends SceneBase {

    private RenderFactory renderFactory;
    float order = 100;

    public GameScene()
    {

    }

    @Override
    public void enable()
    {
        renderFactory = new RenderFactory(this.pane);
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

        Platform.runLater(() ->
        {
            renderFactory.render(deltaT);
        });

    }

    @Override
    protected void sceneKeyEvent(KeyCode k, boolean p) {

    }

}
