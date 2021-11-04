package b451_Project.scenes;

import b451_Project.global.GameVariables;
import b451_Project.net.packets.ClientTickPacket;
import b451_Project.render.RenderFactory;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;

import java.security.Key;

//this scene displays game content
public class GameScene extends SceneBase {

    private RenderFactory renderFactory;

    //key status
    boolean w = false;
    boolean a = false;
    boolean s = false;
    boolean d = false;

    public GameScene()
    {

    }

    @Override
    public void enable()
    {
        renderFactory = new RenderFactory(this.canvas);
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
        renderFactory.render(deltaT);
    }

    @Override
    protected void sceneKeyEvent(KeyCode k, boolean p) {
        if(k == KeyCode.W)
        {
            w = p;
        }

        if(k == KeyCode.A)
        {
            a = p;
        }

        if(k == KeyCode.S)
        {
            s = p;
        }

        if(k == KeyCode.D)
        {
            d = p;
        }

        //set client key status
        GameVariables.client.setKeyPressed(w, a, s, d);
    }

    /**
     * get game scene render factory
     * */
    public RenderFactory getRenderFactory()
    {
        return renderFactory;
    }

}
