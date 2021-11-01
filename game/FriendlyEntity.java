package b451_Project.game;


public class FriendlyEntity extends Entity{

    protected float hp;

    public FriendlyEntity(float x, float y, float vx, float xy, float range, float hp)
    {
        super(x, y, vx, xy, range);
        this.hp = hp;
    }

    @Override
    public void borderCollision(boolean top, boolean bottom, boolean left, boolean right) {

    }

    @Override
    public void collision(Entity[] collisionList, int from, int to) {

    }
}
