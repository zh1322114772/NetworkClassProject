package b451_Project.game;

/**
 * player ship entity class
 * */
public class Ship extends FriendlyEntity{

    public Ship(float x, float y)
    {
        super(x, y, 0, 0, 30);
        rotation = 30;
    }

    @Override
    public void borderCollision(boolean top, boolean bottom, boolean left, boolean right)
    {
        //test
        System.out.println("ouch");
    }

    @Override
    public void collision(int[] collisionList, int from, int to)
    {
        System.out.println("ouch" + from + " " + to);
    }
}
