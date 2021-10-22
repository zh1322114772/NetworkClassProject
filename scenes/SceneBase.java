package b451_Project.scenes;
import b451_Project.utils.Timer;
import javafx.scene.*;
import b451_Project.global.WindowVariables;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

// base abstract class
public abstract class SceneBase {

    protected Scene scene;
    protected Pane pane;
    private boolean mousePressed = false;
    private Timer timer;

    public SceneBase()
    {
        timer = new Timer(true);
        pane = new Pane();
        scene = new Scene(pane, WindowVariables.WINDOW_WIDTH, WindowVariables.WINDOW_HEIGHT);

        //size changed listener
        scene.widthProperty().addListener((observable, oldValue, newValue) ->
        {
            synchronized (this)
            {
                sceneSizeChanged(newValue.doubleValue(), scene.getHeight());
            }
        });

        scene.heightProperty().addListener((observable, oldValue, newValue) ->
        {
            synchronized (this)
            {
                sceneSizeChanged(scene.getWidth(), newValue.doubleValue());
            }
        });

        //key pressed and key released listeners
        scene.setOnKeyPressed((keyEvent) ->
        {
            synchronized (this)
            {
                sceneKeyEvent(keyEvent.getCode(), true);
            }
        });

        scene.setOnKeyReleased((keyEvent) ->
        {
            synchronized (this)
            {
                sceneKeyEvent(keyEvent.getCode(), false);
            }
        });

        //mouse event
        scene.setOnMousePressed((event)->
        {
            synchronized (this)
            {
                mousePressed = true;
                sceneMouseEvent(event.getX(), event.getY(), mousePressed);
            }
        });

        scene.setOnMouseReleased((event) ->
        {
            synchronized (this)
            {
                mousePressed = false;
                sceneMouseEvent(event.getX(), event.getY(), mousePressed);
            }

        });

        scene.setOnMouseMoved((event) ->
        {
            synchronized (this)
            {
                sceneMouseEvent(event.getX(), event.getY(), mousePressed);
            }
        });



    }

    /**
     * this enable method will be called when stage change this instance scene as current scene
     * */
    public void enable()
    {
        //start repaint thread
        timer.start((deltaT) ->
        {
            synchronized (this)
            {
                sceneRedraw(deltaT);
            }
        }, 1.0 / WindowVariables.WINDOW_FRAME_RATE);

        synchronized (this)
        {
            sceneSizeChanged(scene.getWidth(), scene.getHeight());
        }

    }

    /** this disable method will be called when stage change from this instance scene to other instance scene
     *
     * */
    public void disable()
    {
        //stop repaint thread
        timer.stop();
    }

    /**
     * scene size changed callback method
     * @param w new scene width
     * @param h new scene height
     */
    protected abstract void sceneSizeChanged(double w, double h);

    /**
     * mouse event callback
     * @param posX mouse X position
     * @param posY mouse Y position
     * @param p true if mouse pressed
     * */
    protected abstract void sceneMouseEvent(double posX, double posY, boolean p);

    /**
     * simple timer method, this method will be called on every frame time
     * @param deltaT time interval measured between two frames in second
     * */
    protected abstract void sceneRedraw(double deltaT);

    /**
     * key callback method
     * @param k keycode
     * @param p true if key k pressed
     * */
    protected abstract void sceneKeyEvent(KeyCode k, boolean p);


    /**
     * get javafx scene instance
     * @return scene
     */
    final public Scene getScene()
    {
        return scene;
    }


}
