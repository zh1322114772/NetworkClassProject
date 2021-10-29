package b451_Project.game;

import java.io.Serializable;

/**
 * moveable object in the game
 * */

public abstract class Entity implements Serializable {
    public int ID;

    //object x, y location
    public float x = 0;
    public float y = 0;

    //object x, y velocity
    public float vx = 0;
    public float vy = 0;

    //object rotation
    public float rotation = 0;

    //object collision detection range
    public float collisionCircleRange = 0;

    //collision callback
    /**
     * object collision callback
     * @param collisionList objects that's collided with this object
     * @param from array index from
     * @param to array index to
     * */
    public abstract void collision(int[] collisionList, int from, int to);

    /**
     * @param x entity x location
     * @param y entity y location
     * @param vx entity x velocity
     * @param vy entity y velocity
     * @param range entity collision boundary range
     * */
    public Entity(float x, float y, float vx, float vy, float range)
    {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.collisionCircleRange = range;
    }

    /**
     * if object collided with screen boundary
     * */
    public abstract void borderCollision(boolean top, boolean bottom, boolean left, boolean right);

}
