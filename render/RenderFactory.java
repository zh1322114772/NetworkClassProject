package b451_Project.render;
import b451_Project.global.ConfigVariables;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.*;
import java.lang.Math;

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
        //set to -1 to indicate update per frame
        public float objectUpdateInterval;
        public float objectIntervalCounter = 0;

        //x position
        public float oldX = 0;
        public float intX = 0;
        public float newX = 0;

        //y position
        public float oldY = 0;
        public float intY = 0;
        public float newY = 0;

        //rotation
        public float newRotate = 0;
        public float intRotate = 0;
        public float oldRotate = 0;

        //layer
        public int layer = 0;

        public RenderableObjectWrapper(T obj, float lifeSpan, float objectUpdateInterval)
        {
            this.lifeSpan = lifeSpan;
            this.obj = obj;
            this.objectUpdateInterval = objectUpdateInterval;
        }

    }

    private ArrayList<HashMap<Integer ,RenderableObjectWrapper<CanvasPolygon>>> polygons;
    private HashMap<Integer ,RenderableObjectWrapper<ParticleGenerator>> particles;
    private Canvas canvas;
    private int counter = 0;

    public RenderFactory(Canvas p)
    {
        this.canvas = p;
        polygons = new ArrayList<HashMap<Integer ,RenderableObjectWrapper<CanvasPolygon>>>();

        //five layers
        for(int i=0;i < 5; i++)
        {
            polygons.add(new HashMap<Integer ,RenderableObjectWrapper<CanvasPolygon>>());
        }

        particles = new HashMap<Integer, RenderableObjectWrapper<ParticleGenerator>>();
    }

    private int getID()
    {
        counter++;
        counter %= 2147483647;
        return counter;
    }

    /**
     * create a new instance of polygon from factory
     * @param lifeSpan the life span of the renderable object in second, set -1 for unlimited time
     * @param objUpdateInterval set the object update interval in second, set -1 to update object per frame
     * @param c color of the polygon
     * @param vertices number of vertex points of the polygon
     * @param rad polygon radius
     * @param viewOrder set z-depth value of the polygon from 0-5
     * @param rotate rotate polygon in degree
     * @param x polygon x center location
     * @param y polygon y center location
     * @return polygon ID
     * */
    public synchronized long makePolygon(float lifeSpan, float objUpdateInterval, Color c, int vertices, float rad, int viewOrder,float rotate, float x, float y)
    {
        //radius of a polygon cannot be less than zero
        //must need 3 or more vertices to form a polygon
        if(rad <= 0 || vertices < 3)
        {
            rad = 1;
            vertices = 3;
        }

        RenderableObjectWrapper<CanvasPolygon> polygon = new RenderableObjectWrapper<CanvasPolygon>(new CanvasPolygon(vertices, rad), lifeSpan, objUpdateInterval);
        polygon.newX = x;
        polygon.newY =y;
        polygon.intX = x;
        polygon.intY = y;
        polygon.oldX = x;
        polygon.oldY = y;
        polygon.oldRotate = rotate;
        polygon.intRotate = rotate;
        polygon.newRotate = rotate;

        //layer must be 0 - 4
        if(viewOrder < 0)
        {
            polygon.layer = 0;
        }else if(viewOrder > 4)
        {
            polygon.layer = 4;
        }else
        {
            polygon.layer = viewOrder;
        }

        //set init position
        polygon.obj.setLocation(x, y);
        polygon.obj.setRotation(rotate);
        polygon.obj.setColor(c);

        //add to hashmap
        int id = getID();
        polygons.get(polygon.layer).put(id, polygon);

        return (long)id + ((long)polygon.layer << 32);
    }

    /**
     * set the rotation in degrees of the polygon
     * @param polygonID polygon id
     * @param deg degree
     * */
    public synchronized void setPolygonRotation(long polygonID, float deg)
    {
        int id = (int)(polygonID & (0xffffffffl));
        int layer = (int)(polygonID >> 32);

        RenderableObjectWrapper<CanvasPolygon> polygon = polygons.get(layer).get(id);
        if(polygon != null)
        {
            polygon.newRotate = deg;
        }
    }

    /**
     * set polygon x and y location
     * @param polygonID polygon id
     * @param x polygon x center location
     * @param y polygon y center location
     * */
    public synchronized void setPolygonCenterLocation(long polygonID, float x, float y)
    {
        int id = (int)(polygonID & (0xffffffffl));
        int layer = (int)(polygonID >> 32);

        RenderableObjectWrapper<CanvasPolygon> polygon = polygons.get(layer).get(id);
        if(polygon != null)
        {
            polygon.newX = x;
            polygon.newY = y;
        }
    }

    /**
     * destroy a visible polygon
     * @param polygonID
     * */
    public synchronized void setPolygonDestroy(long polygonID)
    {
        int id = (int)(polygonID & (0xffffffffl));
        int layer = (int)(polygonID >> 32);

        RenderableObjectWrapper<CanvasPolygon> polygon = polygons.get(layer).get(id);
        if(polygon != null)
        {
            polygon.lifeSpan = 0;
        }
    }

    /**
     * generate a new instance of particle generator
     * @param generateInterval time interval to generate a new particle
     * @param direction particle shooting direction (in degrees)
     * @param dRange random direction range, that is set particle's direction within range of random(direction-dRange, direction + dRange)
     * @param velocity particle initial velocity
     * @param vRange random velocity range, that is set particle's initial velocity within range of random(velocity - vRange, velocity + vRange)
     * @param color particle color
     * @param particleLifeSpan particle survival time in seconds
     * @param generatorLifeSpan particle generator life span, set -1 to unlimited time
     * @param x particle generator x location
     * @param y particle generator y location
     * @param friction particle friction
     * @param orderView z-depth value must between 0-4
     * */
    public synchronized int makeParticleGenerator(float generateInterval, float direction, float dRange, float velocity, float vRange, Color color, float particleLifeSpan, float generatorLifeSpan, float x, float y, float friction, int orderView)
    {
        ParticleGenerator p = new ParticleGenerator(generateInterval, direction, dRange, velocity, vRange, color, particleLifeSpan, x, y, friction, orderView, this);
        RenderableObjectWrapper<ParticleGenerator> rp = new RenderableObjectWrapper<ParticleGenerator>(p, generatorLifeSpan, 1.0f/ ConfigVariables.GAME_TICK_RATE);

        rp.newX = x;
        rp.newY =y;
        rp.intX = x;
        rp.intY = y;
        rp.oldX = x;
        rp.oldY = y;
        rp.oldRotate = direction;
        rp.intRotate = direction;
        rp.newRotate = direction;

        //add to hashmap
        int id = getID();
        particles.put(id, rp);
        return id;
    }

    /**
     * set particle generator direction
     * @param generatorID particle generator id
     * */
    public synchronized void setParticleGeneratorRotation(int generatorID, float deg)
    {
        RenderableObjectWrapper<ParticleGenerator> p = particles.get(generatorID);
        if(p != null)
        {
            p.newRotate = deg;
        }
    }

    /**
     * set particle generator location
     * @param generatorID particle generator id
     * @param x particle generator x location
     * @param y particle generator y location
     * */
    public synchronized void setParticleGeneratorLocation(int generatorID, float x, float y)
    {
        RenderableObjectWrapper<ParticleGenerator> p = particles.get(generatorID);
        if(p != null)
        {
            p.newX = x;
            p.newY = y;
        }
    }

    /**
     * destroy a particle generator
     * @param generatorID generator id
     * */
    public synchronized void setParticleGeneratorDestroy(int generatorID)
    {
        RenderableObjectWrapper<ParticleGenerator> p = particles.get(generatorID);
        if(p != null)
        {
            p.lifeSpan = 0;
            p.obj.setDestroy();
        }
    }

    /**
     * draw a frame
     * @param deltaT time interval between two frames
     * */
    public synchronized void render(double deltaT)
    {
        //refresh
        canvas.getGraphicsContext2D().setFill(Color.WHITE);
        canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        //render all particles
        for(Integer i : particles.keySet())
        {
            RenderableObjectWrapper<ParticleGenerator> p = particles.get(i);
            p.obj.tick(deltaT);
        }

        //update particle generators
        Iterator<RenderableObjectWrapper<ParticleGenerator>> particleIterator = particles.values().iterator();
        while(particleIterator.hasNext())
        {
            RenderableObjectWrapper<ParticleGenerator> p = particleIterator.next();
            if(p.lifeSpan != -1)
            {
                p.lifeSpan -= deltaT;
                //early stop generating particles
                if((p.lifeSpan - p.obj.getSpan()) < p.obj.getSpan())
                {
                    p.obj.stop();
                }

                //remove died particle generator
                if(p.lifeSpan <= 0)
                {
                    p.obj.setDestroy();
                    particleIterator.remove();
                    continue;
                }

            }

            //make animation between last and newest point
            p.objectIntervalCounter += deltaT;
            if (p.objectIntervalCounter >= p.objectUpdateInterval) {
                p.objectIntervalCounter %= p.objectUpdateInterval;

                p.intRotate = p.newRotate;
                p.intX = p.newX;
                p.intY = p.newY;
            }

            //non-linear animation
            p.oldX = p.oldX + ((p.intX - p.oldX) * 0.2f);
            p.oldY = p.oldY + ((p.intY - p.oldY) * 0.2f);
            p.oldRotate = p.oldRotate + ((p.intRotate - p.oldRotate) * 0.2f);

            p.obj.setLocation(p.oldX , p.oldY);
            p.obj.setRotation(p.oldRotate);
        }

        for(int i = 4; i>=0; i--)
        {
            //update time span, delete all died polygons and render all polygons
            Iterator<RenderableObjectWrapper<CanvasPolygon>> polygonIterator = polygons.get(i).values().iterator();
            while (polygonIterator.hasNext()) {
                RenderableObjectWrapper<CanvasPolygon> p = polygonIterator.next();

                //update lifespan
                if (p.lifeSpan != -1) {
                    p.lifeSpan -= deltaT;

                    if (p.lifeSpan <= 0) {
                        polygonIterator.remove();
                        continue;
                    }
                }

                //update location and rotation
                if (p.objectUpdateInterval == -1) {
                    p.obj.setLocation(p.newX, p.newY);
                    p.obj.setRotation(p.newRotate);
                } else {
                    //make animation between last and newest point
                    p.objectIntervalCounter += deltaT;
                    if (p.objectIntervalCounter >= p.objectUpdateInterval) {
                        p.objectIntervalCounter %= p.objectUpdateInterval;

                        p.intRotate = p.newRotate;
                        p.intX = p.newX;
                        p.intY = p.newY;
                    }

                    //non-linear animation
                    p.oldX = p.oldX + ((p.intX - p.oldX) * 0.2f);
                    p.oldY = p.oldY + ((p.intY - p.oldY) * 0.2f);
                    p.oldRotate = p.oldRotate + ((p.intRotate - p.oldRotate) * 0.2f);

                    p.obj.setLocation(p.oldX , p.oldY);
                    p.obj.setRotation(p.oldRotate);
                }

                //render
                p.obj.draw(canvas.getGraphicsContext2D());
            }
        }

    }

}
