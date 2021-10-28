package b451_Project.game;

/**
 * player ship entity class
 * */
public class Ship extends FriendlyEntity{

    public Ship(double x, double y)
    {
        super(x, y, 0, 0, 30);
    }

    @Override
    public void borderCollision(boolean top, boolean bottom, boolean left, boolean right)
    {

    }

    @Override
    public void collision(int[] collisionList, int from, int to) {


    }
}
