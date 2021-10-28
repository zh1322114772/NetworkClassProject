package b451_Project.game;


public class FriendlyEntity extends Entity{

    public FriendlyEntity(double x, double y, double vx, double xy, double range)
    {
        super(x, y, vx, xy, range);
    }

    @Override
    public void borderCollision(boolean top, boolean bottom, boolean left, boolean right) {

    }

    @Override
    public void collision(int[] collisionList, int from, int to) {

    }
}
