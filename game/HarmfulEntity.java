package b451_Project.game;

public class HarmfulEntity extends Entity{
    public HarmfulEntity(float x, float y, float vx, float xy, float range)
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
