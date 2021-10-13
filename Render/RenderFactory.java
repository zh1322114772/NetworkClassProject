package b451_Project.Render;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.Queue;
import java.util.Stack;

//this render factory manage all renderable objects of the game
public class RenderFactory {

    //This sub-class simply wrap the object with some extra parameters for rendering
    private class RenderableObjectWrapper<T>
    {
        public T obj;

        // set the life span time in second of an object
        // if the life span counts to zero, then the renderFactory automatically destroy the object
        // set this variable to -1 to indicate no time limit
        public float lifeSpan;
        //set the object position update interval in second
        public float objectUpdateInterval;

        //record the last x position of the object
        public float oldX = 0;
        //newest x position of the object
        public float newX = 0;
        //record the last y position of the object
        public float oldY = 0;
        //newest y position of the object
        public float newY = 0;

        public RenderableObjectWrapper(T obj, float lifeSpan, float objectUpdateInterval)
        {
            this.lifeSpan = lifeSpan;
            this.obj = obj;
            this.objectUpdateInterval = objectUpdateInterval;
        }

    }

    //maintain all circles and polygons
    private ArrayList<RenderableObjectWrapper<Circle>> circles;
    private ArrayList<RenderableObjectWrapper<Polygon>> polygons;

    //contain all destroyed object indices, for re-use
    private Queue<Integer> circleRecycleList;
    private Queue<Integer> polygonRecycleList;

    private Pane pane;

    public RenderFactory(Pane p)
    {
        this.pane = pane;
        circles = new ArrayList<RenderableObjectWrapper<Circle>>();
        polygons = new ArrayList<RenderableObjectWrapper<Polygon>>();
    }

    /**
     * create a new instance of circle from factory
     * @param lifeSpan the life span of the renderable object in second, set -1 for unlimited time
     * @param objUpdateInterval set the object update interval in second, set -1 to update object per frame
     * @return circle ID
     * */
    public long makeCircle(float lifeSpan, float objUpdateInterval)
    {
        //check if there's reusbale circle
        if(circleRecycleList.isEmpty())
        {
            circles.add(new RenderableObjectWrapper<Circle>(new Circle(), lifeSpan, objUpdateInterval));
            return circles.size() - 1;
        }

        //reuse
        Integer i = circleRecycleList.poll();
        circles.get(i).lifeSpan = lifeSpan;
        circles.get(i).objectUpdateInterval = objUpdateInterval;
        return i;
    }

    /**
     * create a new instance of polygon from factory
     * @param lifeSpan the life span of the renderable object in second, set -1 for unlimited time
     * @param objUpdateInterval set the object update interval in second, set -1 to update object per frame
     * @return triangle ID
     * */
    public long makePolygon(float lifeSpan, float objUpdateInterval)
    {
        //check if there's reusbale polygon
        if(polygonRecycleList.isEmpty())
        {
            polygons.add(new RenderableObjectWrapper<Polygon>(new Polygon(), lifeSpan, objUpdateInterval));
            return polygons.size() - 1;
        }

        //reuse
        Integer i = polygonRecycleList.poll();
        polygons.get(i).lifeSpan = lifeSpan;
        polygons.get(i).objectUpdateInterval = objUpdateInterval;
        return i;
    }

    /**
     * draw a frame
     * @param deltaT time interval between two frames
     * */
    public void render(double deltaT)
    {

    }

}
