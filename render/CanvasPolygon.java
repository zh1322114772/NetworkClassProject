package b451_Project.render;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * javafx canvas polygon object
 *
 * */
public class CanvasPolygon {


    private double x;
    private double y;
    private double radius;
    private  double deltaRadian;
    private double rotation;
    private Color color;
    private double[] polygonX;
    private double[] polygonY;
    private boolean visible = true;

    /**
     * @param v number of vertex points of the polygon
     * @param r polygon radius
     * */
    public CanvasPolygon(int v, float r)
    {
        x = 0;
        y = 0;
        radius = r;
        rotation = 0;
        polygonX = new double[v];
        polygonY = new double[v];
        color = Color.BLACK;
        deltaRadian = 6.28310f / (double)v;
    }

    /**
     * set polygon center location
     * @param x x location
     * @param y y location
     * */
    public void setLocation(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * set polygon rotation in degrees
     * @param d degree
     * */
    public void setRotation(float d)
    {
        this.rotation = Math.toRadians(d);
    }

    /**
     * set polygon color
     * @param c javafx Color
     * */
    public void setColor(Color c)
    {
        color = c;
    }

    /**
     * set polygon visible status
     * @param v true = visible , false invisible
     * */
    public void setVisible(boolean v)
    {
        visible = v;
    }

    /**
     * render polygon
     * @gc canvas graphic context
     * */
    public void draw(GraphicsContext gc)
    {
        //if is invisible
        if(!visible) return;

        //make polygon and add offset
        for(int i = 0; i< polygonX.length; i++)
        {
            double r = (deltaRadian * i) + rotation;
            polygonX[i] = (Math.cos(r) * radius) + x;
            polygonY[i] = (Math.sin(r) * radius) + y;
        }
        gc.setFill(color);
        gc.fillPolygon(polygonX, polygonY, polygonX.length);
;
    }


}
