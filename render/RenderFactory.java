package b451_Project.render;
import b451_Project.global.ConfigVariables;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
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

        public RenderableObjectWrapper(T obj, float lifeSpan, float objectUpdateInterval)
        {
            this.lifeSpan = lifeSpan;
            this.obj = obj;
            this.objectUpdateInterval = objectUpdateInterval;
        }

    }

    private ArrayList<RenderableObjectWrapper<Polygon>> polygons;
    private ArrayList<RenderableObjectWrapper<ParticleGenerator>> particles;
    private Queue<Integer> polygonRecycleList;
    private Queue<Integer> particleRecycleList;
    private Pane pane;

    public RenderFactory(Pane p)
    {
        this.pane = p;
        polygons = new ArrayList<RenderableObjectWrapper<Polygon>>();
        particles = new ArrayList<RenderableObjectWrapper<ParticleGenerator>>();
        polygonRecycleList = new LinkedList<Integer>();
        particleRecycleList = new LinkedList<Integer>();
    }

    /**
     * create a new instance of polygon from factory
     * @param lifeSpan the life span of the renderable object in second, set -1 for unlimited time
     * @param objUpdateInterval set the object update interval in second, set -1 to update object per frame
     * @param c color of the polygon
     * @param vertices number of vertex points of the polygon
     * @param rad polygon radius
     * @param viewOrder set z-depth value of the polygon
     * @param rotate rotate polygon in degree
     * @param x polygon x center location
     * @param y polygon y center location
     * @return polygon ID
     * */
    public synchronized int makePolygon(float lifeSpan, float objUpdateInterval, Color c, int vertices, float rad, float viewOrder,float rotate, float x, float y)
    {
        //radius of a polygon cannot be less than zero
        //must need 3 or more vertices to form a polygon
        if(rad <= 0 || vertices < 3)
        {
            rad = 1;
            vertices = 3;
        }

        //check if there's reusbale polygon
        int id;
        if(polygonRecycleList.isEmpty())
        {
            polygons.add(new RenderableObjectWrapper<Polygon>(new Polygon(), lifeSpan, objUpdateInterval));
            id = polygons.size() - 1;
        }else
        {
            id = polygonRecycleList.poll().intValue();
            polygons.get(id).lifeSpan = lifeSpan;
            polygons.get(id).objectUpdateInterval = objUpdateInterval;
        }

        RenderableObjectWrapper<Polygon> polygon = polygons.get(id);

        polygon.newX = x;
        polygon.newY =y;
        polygon.intX = x;
        polygon.intY = y;
        polygon.oldX = x;
        polygon.oldY = y;
        polygon.oldRotate = rotate;
        polygon.intRotate = rotate;
        polygon.newRotate = rotate;

        polygon.obj.setViewOrder(viewOrder);
        polygon.obj.getPoints().removeAll();
        Double[] v = new Double[vertices * 2];
        double deltaRadian = Math.toRadians(360.0/vertices);

        for(int i = 0; i < vertices; i++)
        {
            v[(i * 2)] = Math.cos(i * deltaRadian) * rad;
            v[(i * 2) + 1] = Math.sin(i * deltaRadian) * rad;
        }

        polygon.obj.getPoints().addAll(v);
        polygon.obj.setFill(c);
        Platform.runLater(() ->
        {
            pane.getChildren().add(polygon.obj);
        });
        return id;
    }

    /**
     * set the rotation in degrees of the polygon
     * @param polygonID polygon id
     * @param deg degree
     * */
    public synchronized void setPolygonRotation(int polygonID, float deg)
    {
        if(polygonID < polygons.size() && polygonID >= 0)
        {
            RenderableObjectWrapper<Polygon> polygon = polygons.get(polygonID);
            polygon.newRotate = deg;
        }
    }

    /**
     * set polygon x and y location
     * @param polygonID polygon id
     * @param x polygon x center location
     * @param y polygon y center location
     * */
    public synchronized void setPolygonCenterLocation(int polygonID, float x, float y)
    {
        if(polygonID < polygons.size() && polygonID >= 0)
        {
            RenderableObjectWrapper<Polygon> polygon = polygons.get(polygonID);
            polygon.newX = x;
            polygon.newY = y;
        }
    }

    /**
     * destroy a visible polygon
     * @param polygonID
     * */
    public synchronized void setPolygonDestroy(int polygonID)
    {
        if(polygonID < polygons.size() && polygonID >= 0)
        {
            RenderableObjectWrapper<Polygon> polygon = polygons.get(polygonID);
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
     * @param orderView z-depth value
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

        particles.add(rp);
        return particles.size() - 1;
    }

    /**
     * set particle generator direction
     * @param generatorID particle generator id
     * */
    public synchronized void setParticleGeneratorRotation(int generatorID, float deg)
    {
        if(generatorID < particles.size() && generatorID >= 0)
        {
            RenderableObjectWrapper<ParticleGenerator> p = particles.get(generatorID);
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
        if(generatorID < particles.size() && generatorID >= 0)
        {
            RenderableObjectWrapper<ParticleGenerator> p = particles.get(generatorID);
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
        if(generatorID < particles.size() && generatorID >= 0)
        {
            RenderableObjectWrapper<ParticleGenerator> p = particles.get(generatorID);
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
        Platform.runLater(() ->
        {

            //render all particles
            for(RenderableObjectWrapper<ParticleGenerator> p : particles)
            {
                p.obj.tick(deltaT);
            }

            //update particle generators
            for(int i = particles.size() - 1; i >=0; i--)
            {
                RenderableObjectWrapper<ParticleGenerator> p = particles.get(i);
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
                        particles.remove(i);
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

            //render all polygons
            for (RenderableObjectWrapper<Polygon> p : polygons) {
                //update lifespan
                if (p.lifeSpan != -1) {
                    p.lifeSpan -= deltaT;
                    //recycle polygon
                    if (p.lifeSpan <= 0) {
                        p.lifeSpan = 0;
                        pane.getChildren().remove(p.obj);
                        continue;
                    }
                }

                //update location and rotation
                if (p.objectUpdateInterval == -1) {
                    p.obj.setLayoutX(p.newX);
                    p.obj.setLayoutY(p.newY);
                    p.obj.setRotate(p.newRotate);

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

                    p.obj.setLayoutX(p.oldX);
                    p.obj.setLayoutY(p.oldY);
                    p.obj.setRotate(p.oldRotate);
                }

            }

        });
    }

}
