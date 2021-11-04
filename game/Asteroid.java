package b451_Project.game;

import b451_Project.global.GameVariables;
import b451_Project.global.WindowVariables;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Asteroid extends Entity implements HarmfulEntity, AIEntity{

    private float randomRotation;

    public Asteroid(float x, float y) {
        super(x, y, 0, 0, 20, 10);
        randomRotation = (float)(Math.random() * 30) - 15f;
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
    public void collision(Entity[] collisionList, int from, int to)
    {
        for(int i= from ; i < to; i++)
        {
            //when asteroid collided with friendly entities
            if(collisionList[i] instanceof FriendlyEntity)
            {
                //attack only if object has health
                if(collisionList[i].hp <=0)
                {
                    continue;
                }

                //explosion effect
                ParticlePlayer p = new ParticlePlayer();
                Color c = Color.ORANGE;
                p.r = (float) c.getRed();
                p.b = (float)c.getBlue();
                p.g = (float)c.getGreen();
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
    public void tick(ArrayList<Entity> e, double dt) {
        rotation += randomRotation;
    }
}
