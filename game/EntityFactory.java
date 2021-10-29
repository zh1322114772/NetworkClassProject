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

    //use max priority queue to ensure remove object from back to front
    private PriorityQueue<Integer> maxQueue = new PriorityQueue<Integer>(20, Collections.reverseOrder());

    //indices of collided objects
    private int[] collisionList = new int[1024];

    /**
     * remove an object
     * @param i index
     * */
    public void removeObject(int i)
    {
        if(i < objects.size())
        {
            maxQueue.add(i);
        }
    }

    public void tick()
    {
        //remove object
        while(!maxQueue.isEmpty())
        {
            objects.remove(maxQueue.poll());
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
            int[] temp = new int[(int)Math.pow((double)objects.size() * EXPAND_FACTOR, 2)];
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
                        collisionList[to] = i;
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

    public ArrayList<Entity> getEntityList()
    {
        return objects;
    }

}
