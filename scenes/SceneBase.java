package b451_Project.scenes;
import b451_Project.render.CanvasPolygon;
import b451_Project.utils.Timer;
import javafx.animation.AnimationTimer;
import javafx.scene.*;
import b451_Project.global.WindowVariables;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

// base abstract class
public abstract class SceneBase {

    protected Scene scene;
    protected Pane pane;
    protected Canvas canvas;
    private boolean mousePressed = false;
    private AnimationTimer timer;

    private long oldTimestamp = 0;

    public SceneBase()
    {
        pane = new Pane();
        canvas = new Canvas(WindowVariables.WINDOW_WIDTH, WindowVariables.WINDOW_HEIGHT);
        pane.getChildren().add(canvas);
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
        oldTimestamp = System.nanoTime();
        timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                synchronized (this)
                {
                    sceneRedraw((double)(l - oldTimestamp)/1e+9);
                }
                oldTimestamp = l;
            }
        };
        timer.start();

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
