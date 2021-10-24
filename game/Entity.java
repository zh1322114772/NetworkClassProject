package b451_Project.game;

/**
 * moveable object in the game
 * */

public abstract class Entity {
    public Entity ID;

    //object x, y location
    public double x = 0;
    public double y = 0;

    //object x, y velocity
    public double vx = 0;
    public double vy = 0;

    //for collision detection
    public double collisionCircleRange = 0;

    public abstract void collision();

}
