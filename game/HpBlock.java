package b451_Project.game;

import b451_Project.global.GameVariables;
import b451_Project.global.WindowVariables;
import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * recover ship's health
 * */
public class HpBlock extends Entity implements NeutralEntity, AIEntity {
    /**
     * @param x     entity x location
     * @param y     entity y location
     * @param vx    entity x velocity
     * @param vy    entity y velocity
     */
    public HpBlock(float x, float y, float vx, float vy)
    {
        super(x, y, vx, vy, 35, 75);
    }

    @Override
    public void collision(Entity[] collisionList, int from, int to)
    {
        for(int i = from; i < to; i++)
        {
            if(collisionList[i] instanceof Ship)
            {
                //make ship shoot faster
                GameVariables.server.shootingInterval -= 0.05;

                collisionList[i].hp += hp;
                if(collisionList[i].hp > 100) collisionList[i].hp = 100;
                hp = 0;

                //particle effect
                ParticlePlayer p = new ParticlePlayer();
                Color c = Color.RED;
                p.r = (float) c.getRed();
                p.b = (float) c.getBlue();
                p.g = (float) c.getGreen();
                p.a = 0.5f;
                p.generateInterval = 0.0005f;
                p.direction = 0;
                p.dRange = 180;
                p.velocity = 20;
                p.vRange = 5;
                p.friction = 0.95f;
                p.orderView = 0;
                p.particleLifeSpan = 0.3f;
                p.generatorLifeSpan = 0.5f;
                p.radius = 4;
                p.rRange = 4;
                p.x = x;
                p.y = y;


                GameVariables.entityFactory.particles.add(p);
                alive = false;
                break;
            }
        }
    }

    @Override
    public void borderCollision(boolean top, boolean bottom, boolean left, boolean right)
    {
        //if asteroid is out of screen
        if((x < -200) || (x > WindowVariables.WINDOW_WIDTH + 200) || (y < -200) || (y > WindowVariables.WINDOW_HEIGHT + 200))
        {
            alive = false;
        }
    }

    @Override
    public void tick(ArrayList<Entity> en, double d) {
        for(Entity e : en)
        {
            //make attraction effect
            if(e instanceof Ship)
            {
                double distance = Math.sqrt(Math.pow(e.x - x, 2) + Math.pow(e.y - y, 2));
                double direction = Math.atan2(e.y -y, e.x - x);
                if(distance < 250)
                {
                    double speed = Math.pow(500.0 / distance, 2);
                    vx = (float)(Math.cos(direction) * speed);
                    vy = (float)(Math.sin(direction) * speed);
                }

            }
        }
    }
}
