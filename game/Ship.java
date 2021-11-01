package b451_Project.game;

import b451_Project.global.WindowVariables;

/**
 * player ship entity class
 * */
public class Ship extends FriendlyEntity{

    public Ship(float x, float y)
    {
        super(x, y, 0, 0, 20, 100);
        rotation = 30;
    }

    @Override
    public void borderCollision(boolean top, boolean bottom, boolean left, boolean right)
    {
        if(top)
        {
            y = collisionCircleRange;
            vy = 0;
        }

        if(bottom)
        {
            y = WindowVariables.WINDOW_HEIGHT - collisionCircleRange;
            vy = 0;
        }

        if(left)
        {
            x = collisionCircleRange;
            vx = 0;
        }

        if(right)
        {
            x = WindowVariables.WINDOW_WIDTH - collisionCircleRange;
            vx = 0;
        }
    }

    @Override
    public void collision(Entity[] collisionList, int from, int to)
    {
        System.out.println("ouch" + from + " " + to);
    }
}
