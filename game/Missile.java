package b451_Project.game;

import b451_Project.global.GameVariables;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Missile extends Entity implements HarmfulEntity, AIEntity{

    private static double EXPLODE_TIME = 10;
    private static float SENSITIVITY = 0.1f;
    private static float SPEED = 50;

    // missile explode after 15 second
    private double counter = 0;

    //chase target
    private Ship target = null;

    public Missile (float x, float y) {
        super(x, y, 0, 0, 12, 5);
    }

    @Override
    public void tick(ArrayList<Entity> e, double dt) {

        if(target == null)
        {
            //find target
            for(Entity en: e)
            {
                if(en instanceof Ship)
                {
                    if(en.hp <= 0) continue;

                    //ensure choose target between player1 and player2 equally
                    if(Math.random() > 0.5)
                    {
                        target = (Ship)en;
                    }
                }

            }

        }else
        {
            //if target no longer available
            if(!target.alive || target.hp <= 0)
            {
                target = null;
                return;
            }

            // check if missile survived more than 15 seconds
            else if(counter >= EXPLODE_TIME)
            {
                alive = false;

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

            }else
            {
                counter += dt;
                float targetDirection =  (float)Math.toDegrees(Math.atan2(target.y - y, target.x - x));
                rotation = targetDirection;

                //set vy vx
                vy = ((1 - SENSITIVITY) * vy) + (SENSITIVITY * (float) (Math.sin(Math.toRadians(rotation)) * SPEED));
                vx = ((1 - SENSITIVITY) * vx) + (SENSITIVITY * (float) (Math.cos(Math.toRadians(rotation)) * SPEED));

            }
        }

    }

    @Override
    public void collision(Entity[] collisionList, int from, int to) {
        for(int i= from ; i < to; i++)
        {
            //attack only if object has health
            if(collisionList[i].hp <=0)
            {
                continue;
            }

            //when asteroid collided with friendly entities
            if(collisionList[i] instanceof FriendlyEntity)
            {
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
    public void borderCollision(boolean top, boolean bottom, boolean left, boolean right) {

    }
}
