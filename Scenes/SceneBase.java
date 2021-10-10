package b451_Project.Scenes;
import javafx.scene.*;
import b451_Project.Static.WindowProperties;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

// base abstract class
public abstract class SceneBase {

    protected Scene scene;
    protected Pane pane;
    private Thread repaintThread;
    private volatile  boolean threadFlag;
    private boolean mousePressed = false;


    public SceneBase()
    {
        pane = new Pane();
        scene = new Scene(pane, WindowProperties.WINDOW_WIDTH, WindowProperties.WINDOW_HEIGHT);

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
        threadFlag = true;
        long desiredFrameTimeNano = 1000000000 / WindowProperties.WINDOW_FRAME_RATE;

        repaintThread = new Thread(()->
        {
            //time interval between two frames
            long time0 = System.nanoTime();
            long time1 = time0;
            long deltaTNano = 0;

            long waitTime = 0;
            //refresh scene at constant rate
           while(threadFlag)
           {
               waitTime = System.nanoTime();
               synchronized (this)
               {
                   time0 = System.nanoTime();
                   deltaTNano = time0 - time1;
                   sceneRedraw((double)deltaTNano/ 1000000000.0);
                   time1 = time0;
               }

               //busy wait till desired frame time, then render next frame
               waitTime = System.nanoTime() - waitTime;
               waitTime =  desiredFrameTimeNano - waitTime + System.nanoTime();
               while(waitTime > System.nanoTime()) {};
           }
        });

        synchronized (this)
        {
            sceneSizeChanged(scene.getWidth(), scene.getHeight());
        }

        repaintThread.start();
    }

    /** this disable method will be called when stage change from this instance scene to other instance scene
     *
     * */
    public void disable()
    {
        //stop repaint thread
        threadFlag = false;
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
    public Scene getScene()
    {
        return scene;
    }


}
