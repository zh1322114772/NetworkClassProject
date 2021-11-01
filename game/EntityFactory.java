package b451_Project.game;

import b451_Project.global.WindowVariables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

/**
 * This class manage all entities
 * */
public class EntityFactory {

    public final static double EXPAND_FACTOR = 1.5;

    //next object instance ID
    private int currentID = 0;

    //object array
    private ArrayList<Entity> objects = new ArrayList<Entity>();
    public ArrayList<ParticlePlayer> particles = new ArrayList<ParticlePlayer>();

    //indices of collided objects
    private Entity[] collisionList = new Entity[1024];


    public void tick()
    {
        //remove objects
        for(int i = objects.size() - 1; i>=0; i--)
        {
            if(!objects.get(i).alive)
            {
                objects.remove(i);
            }
        }

        //apply movement
        for(Entity e : objects)
        {
            e.x += e.vx;
            e.y += e.vy;
        }

        //check if array expansion is needed
        if(collisionList.length < (objects.size() * objects.size()))
        {
            Entity[] temp = new Entity[(int)Math.pow((double)objects.size() * EXPAND_FACTOR, 2)];
            //move data
            for(int i=0; i < collisionList.length; i++)
            {
                temp[i] = collisionList[i];
                collisionList = temp;
            }
        }

        //collision detection
        int from = 0;
        int to = 0;
        for(Entity e : objects)
        {
            boolean top = false;
            boolean bottom = false;
            boolean left = false;
            boolean right = false;

            if((e.x - e.collisionCircleRange) <= 0)
            {
                //hit left boundary
                left = true;
            }

            if((e.y - e.collisionCircleRange) <= 0)
            {
                //hit top boundary
                top = true;
            }

            if((e.x + e.collisionCircleRange) >= WindowVariables.WINDOW_WIDTH)
            {
                //hit right boundary
                right = true;
            }

            if((e.y + e.collisionCircleRange) >= WindowVariables.WINDOW_HEIGHT)
            {
                //hit bottom boundary
                bottom = true;
            }

            if(left || top || right || bottom) e.borderCollision(top, bottom, left, right);

            //object collision
            for(int i = 0; i< objects.size(); i++)
            {
                Entity n = objects.get(i);

                //not self
                if(e.ID != n.ID)
                {
                    if(Math.sqrt(Math.pow(n.x - e.x, 2) + Math.pow(n.y - e.y, 2)) <= (e.collisionCircleRange + n.collisionCircleRange))
                    {
                        collisionList[to] = n;
                        to++;
                    }
                }
            }

            //check if there's collision happened to current entity
            if(from != to)
            {
                e.collision(collisionList, from, to);
            }

            //ready for next entity
            from = to;
        }

    }

    private void assignID(Entity e)
    {
        e.ID = currentID;
        objects.add(e);
        currentID++;
        currentID %= 2147483647;
    }

    /**
     * create a new instance of ship entity
     * @param x ship x location
     * @param y ship y location
     * */
    public Ship makeShip(float x, float y)
    {
        Ship s = new Ship(x, y);
        assignID(s);
        return s;
    }

    /**
     * create a new instance of asteroid entity
     * @param x asteroid x location
     * @param y asteroid y location
     * */
    public Asteroid makeAsteroid(float x, float y)
    {
        Asteroid a = new Asteroid(x, y);
        assignID(a);
        return a;
    }

    public ArrayList<Entity> getEntityList()
    {
        return objects;
    }

}
