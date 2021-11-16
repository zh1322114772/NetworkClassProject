package b451_Project.net;

import b451_Project.game.*;
import b451_Project.global.ConfigVariables;
import b451_Project.global.GameVariables;
import b451_Project.global.WindowMsgBox;
import b451_Project.global.WindowVariables;
import b451_Project.net.packets.*;
import b451_Project.render.ParticleGenerator;
import b451_Project.render.RenderFactory;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class GameClient extends TCPClient{

    class Triplet<A, B, C>
    {
        public A first;
        public B second;
        public C third;
        Triplet(A a, B b, C c)
        {
            first = a;
            second = b;
            third = c;
        }
    }

    private boolean wPressed = false;
    private boolean sPressed = false;
    private boolean aPressed = false;
    private boolean dPressed = false;
    private boolean joinedGame = false;

    //map from Object ID -> (Polygon ID, Validate, Particle Generator ID)
    private HashMap<Integer, Triplet<Long, Boolean, ArrayList<Integer>>> tickObject2RenderObject;



    public GameClient(String hostAddress) throws IOException
    {
        super(hostAddress);
        tickObject2RenderObject = new HashMap<Integer, Triplet<Long, Boolean, ArrayList<Integer>>>();
    }

    /**
     * set pressed key that's ready to send
     * */
    public void setKeyPressed(boolean w, boolean a, boolean s, boolean d)
    {
        synchronized (this)
        {
            wPressed = w;
            aPressed = a;
            sPressed = s;
            dPressed = d;
        }
    }

    @Override
    protected void tick(double d) {

        if(joinedGame)
        {
            ClientTickPacket ctp = new ClientTickPacket();
            synchronized (this)
            {
                ctp.aKeyPressed = aPressed;
                ctp.dKeyPressed = dPressed;
                ctp.sKeyPressed = sPressed;
                ctp.wKeyPressed = wPressed;
            }
            sendPacket(ctp);
        }

    }

    @Override
    protected void packetReceived(PacketBase p) {

        //server responded with join packet
        if(p instanceof JoinPacket)
        {
            joinedGame = true;
            //switch to game scene
            WindowVariables.game.switchScene(WindowVariables.gameScene);
        }

        //responded with message packet
        if(p instanceof MsgPacket)
        {
            MsgPacket msg = (MsgPacket)p;
            WindowMsgBox.InfoMessage(msg.header, msg.text);
        }

        //responded with tick packet
        if(p instanceof TickPacket)
        {
            if(WindowVariables.gameScene.getRenderFactory() == null) return;
            TickPacket tp = (TickPacket) p;

            //set verify flag to false
            for(Integer i: tickObject2RenderObject.keySet())
            {
                tickObject2RenderObject.get(i).second = false;
            }

            //update score
            GameVariables.currentScore = tp.survivalTime;
            GameVariables.highestScore = tp.highestSurvivalTime;

            //update render frame
            RenderFactory rf = WindowVariables.gameScene.getRenderFactory();
            for(Entity e : tp.entities)
            {
                Triplet<Long, Boolean, ArrayList<Integer>> triplet = tickObject2RenderObject.get(e.ID);
                //if element doesn't exist, then it must be new object
                if(triplet == null)
                {
                    Long id = 0l;
                    ArrayList<Integer> ef = new ArrayList<Integer>();

                    if(e instanceof Ship)
                    {
                        //particle effects
                        Integer flame0 = rf.makeParticleGenerator(0.02f, 90, 10, 16, 4, 4, 4, Color.color(1, 0, 0, 0.5), 0.5f, -1, e.x, e.y, 0.98f, 2);
                        Integer flame1 = rf.makeParticleGenerator(0.02f, 90, 7, 16, 4, 4, 4, Color.color(1, 1, 0, 0.5), 0.5f, -1, e.x, e.y, 0.98f, 2);
                        ef.add(flame0);
                        ef.add(flame1);

                        //ship
                        id = rf.makePolygon(-1, 1.0f/ ConfigVariables.GAME_TICK_RATE, Color.BLUE, 3, 40, 1, e.rotation, e.x, e.y);
                    }else if(e instanceof Asteroid)
                    {
                        float direction = (float)Math.toDegrees(Math.atan2((double)e.vy, (double)e.vx));

                        Integer flame0 = rf.makeParticleGenerator(0.02f, direction, 30, 17, 4, 4, 4, Color.color(0.3, 0.3, 0.3, 0.5), 0.15f, -1, e.x, e.y, 0.8f, 2);
                        Integer flame1 = rf.makeParticleGenerator(0.02f, direction, 30, 17, 4, 4, 4, Color.color(0.5, 0.5, 0.5, 0.5), 0.15f, -1, e.x, e.y, 0.8f, 2);
                        ef.add(flame0);
                        ef.add(flame1);

                        //asteroid
                        id = rf.makePolygon(-1, 1.0f/ ConfigVariables.GAME_TICK_RATE, Color.BROWN, 8, 40, 0, e.rotation, e.x, e.y);
                    }else if(e instanceof Missile)
                    {

                        Integer flame0 = rf.makeParticleGenerator(0.02f, e.rotation + 180, 15, 16, 4, 4, 4, Color.color(0.0, 0.6, 0.1, 0.5), 0.15f, -1, e.x, e.y, 0.98f, 2);
                        Integer flame1 = rf.makeParticleGenerator(0.02f, e.rotation + 180, 15, 16, 4, 4, 4, Color.color(0.0, 1.0, 0.0, 0.5), 0.15f, -1, e.x, e.y, 0.98f, 2);
                        ef.add(flame0);
                        ef.add(flame1);

                        //missile
                        id = rf.makePolygon(-1, 1.0f/ ConfigVariables.GAME_TICK_RATE, Color.LIGHTGREY, 3, 30, 2, e.rotation, e.x, e.y);
                    }else if(e instanceof FriendlyMissile)
                    {
                        Integer flame0 = rf.makeParticleGenerator(0.02f, e.rotation + 180, 15, 16, 4, 4, 4, Color.color(0.0, 0.1, 0.8, 0.5), 0.15f, -1, e.x, e.y, 0.98f, 2);
                        Integer flame1 = rf.makeParticleGenerator(0.02f, e.rotation + 180, 15, 16, 4, 4, 4, Color.color(0.4, 0.4, 0.8, 0.5), 0.15f, -1, e.x, e.y, 0.98f, 2);
                        ef.add(flame0);
                        ef.add(flame1);

                        //friendly missile
                        id = rf.makePolygon(-1, 1.0f/ ConfigVariables.GAME_TICK_RATE, Color.LIGHTGREY, 3, 30, 2, e.rotation, e.x, e.y);
                    }else if(e instanceof HpBlock)
                    {
                        Integer flame0 = rf.makeParticleGenerator(0.01f, 0, 180, 17, 8, 4, 6, Color.color(0.9, 0.3, 0.3, 0.5), 0.15f, -1, e.x, e.y, 0.8f, 2);
                        Integer flame1 = rf.makeParticleGenerator(0.01f, 0, 180, 17, 8, 4, 6, Color.color(1, 0, 0, 0.5), 0.15f, -1, e.x, e.y, 0.8f, 2);
                        ef.add(flame0);
                        ef.add(flame1);

                        //asteroid
                        id = rf.makePolygon(-1, 1.0f/ ConfigVariables.GAME_TICK_RATE, Color.PINK, 4, 40, 0, e.rotation, e.x, e.y);

                    }

                    //add to hashmap
                    triplet = new Triplet<Long, Boolean, ArrayList<Integer>>(id, true, ef);
                    tickObject2RenderObject.put(e.ID, triplet);
                }else
                {
                    //update existence objects
                    rf.setPolygonCenterLocation(triplet.first, e.x, e.y);
                    rf.setPolygonRotation(triplet.first, e.rotation);
                    triplet.second = true;
                    boolean flameVisible = true;

                    //render ship health
                    if(e instanceof Ship)
                    {
                        Ship s = (Ship)e;
                        float health = s.hp / 100.0f;
                        float alpha = (health <= 0 ) ? 0.2f : 1f;

                        //if ship is died, then don't render flame
                        if(health <= 0)
                        {
                            flameVisible = false;
                        }

                        Color newColor = Color.color((health * Color.BLUE.getRed()) + ((1 - health) * Color.RED.getRed()),
                                                    (health * Color.BLUE.getGreen()) + ((1 - health) * Color.RED.getGreen()),
                                                    (health * Color.BLUE.getBlue()) + ((1 - health) * Color.RED.getBlue()), alpha);

                        rf.setPolygonColor(triplet.first, newColor);
                    }

                    //update particle effects
                    ArrayList<Integer> ef = triplet.third;
                    if(ef.size()>0)
                    {
                        for(Integer i : ef)
                        {
                            rf.setParticleGeneratorVisibleStatus(i, flameVisible);
                            rf.setParticleGeneratorLocation(i ,e.x, e.y);
                            rf.setParticleGeneratorRotation(i ,e.rotation + 180);
                        }
                    }

                }
            }

            //remove died objects
            Iterator<Triplet<Long, Boolean, ArrayList<Integer>>> objIterator = tickObject2RenderObject.values().iterator();
            while(objIterator.hasNext())
            {
                Triplet<Long, Boolean, ArrayList<Integer>> triplet = objIterator.next();

                //remove outdated objects
                if(!triplet.second)
                {
                    rf.setPolygonDestroy(triplet.first);
                    objIterator.remove();

                    //remove particle generators
                    for(Integer e : triplet.third)
                    {
                        rf.setParticleGeneratorDestroy(e);
                    }
                }
            }

            //particles
            for(ParticlePlayer particle : tp.particles)
            {
                rf.makeParticleGenerator(particle.generateInterval, particle.direction, particle.dRange, particle.velocity, particle.vRange, particle.radius, particle.rRange, Color.color(particle.r, particle.g, particle.b, particle.a), particle.particleLifeSpan, particle.generatorLifeSpan, particle.x, particle.y, particle.friction, particle.orderView);
            }

        }
    }

    @Override
    protected void disconnected() {
        WindowVariables.game.switchScene(WindowVariables.mainMenuScene);
        WindowMsgBox.InfoMessage("Connection Lost", "Disconnected from Server");
    }
}
