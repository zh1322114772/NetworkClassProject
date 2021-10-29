package b451_Project.game;


public class FriendlyEntity extends Entity{

    public FriendlyEntity(float x, float y, float vx, float xy, float range)
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
