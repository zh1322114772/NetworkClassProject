package b451_Project.game;

import b451_Project.global.WindowVariables;

public class Asteroid extends HarmfulEntity{

    public Asteroid(float x, float y) {
        super(x, y, 0, 0, 10);
    }

    @Override
    public void borderCollision(boolean top, boolean bottom, boolean left, boolean right)
    {

    }

    @Override
    public void collision(int[] collisionList, int from, int to)
    {
        System.out.println("ouch" + from + " " + to);
    }
}
