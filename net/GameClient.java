package b451_Project.net;

import b451_Project.game.Entity;
import b451_Project.game.Ship;
import b451_Project.global.ConfigVariables;
import b451_Project.global.WindowMsgBox;
import b451_Project.global.WindowVariables;
import b451_Project.net.packets.*;
import b451_Project.render.ParticleGenerator;
import b451_Project.render.RenderFactory;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GameClient extends TCPClient{

    private boolean wPressed = false;
    private boolean sPressed = false;
    private boolean aPressed = false;
    private boolean dPressed = false;
    private boolean joinedGame = false;

    //to synchronize data part and render part
    private HashMap<Integer, Pair<Integer, Pair<Boolean, ArrayList<Integer>>>> tickObject2RenderObject;



    public GameClient(String hostAddress) throws IOException
    {
        super(hostAddress);
        //map from Object ID -> (Polygon ID, Validate, Particle Generator ID)
        tickObject2RenderObject = new HashMap<Integer, Pair<Integer, Pair<Boolean, ArrayList<Integer>>>>();
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

            //update game content

            //set verify flag to false
            for(Integer i: tickObject2RenderObject.keySet())
            {
                Boolean b = tickObject2RenderObject.get(i).getValue().getKey();
                b = false;
            }

            //update render frame
            RenderFactory rf = WindowVariables.gameScene.getRenderFactory();
            for(Entity e : tp.entities)
            {
                Pair<Integer, Pair<Boolean, ArrayList<Integer>>> pair = tickObject2RenderObject.get(e.ID);
                if(pair == null)
                {
                    //new object
                    Integer id = 0;
                    ArrayList<Integer> ef = new ArrayList<Integer>();
                    if(e instanceof Ship)
                    {
                        //particle effects
                        Integer flame0 = rf.makeParticleGenerator(0.012f, 90, 10, 12, 4, Color.RED, 0.5f, -1, e.x, e.y, 1f, 1);
                        Integer flame1 = rf.makeParticleGenerator(0.012f, 90, 7, 12, 4, Color.YELLOW, 0.5f, -1, e.x, e.y, 1f, 1);
                        ef.add(flame0);
                        ef.add(flame1);

                        //ship
                        id = rf.makePolygon(-1, 1.0f/ ConfigVariables.GAME_TICK_RATE, Color.BLUE, 3, 40, 0.5f, e.rotation, e.x, e.y);
                    }

                    //add to hashmap
                    pair = new Pair<Integer, Pair<Boolean, ArrayList<Integer>>>(id, new Pair<Boolean, ArrayList<Integer>>(true, ef));
                    tickObject2RenderObject.put(e.ID, pair);
                }else
                {
                    //update existence objects
                    rf.setPolygonCenterLocation(pair.getKey(), e.x, e.y);
                    rf.setPolygonRotation(pair.getKey(), e.rotation);
                    Pair<Boolean, ArrayList<Integer>> data = pair.getValue();
                    Boolean val = data.getKey();
                    val = true;

                    //update particle effects
                    ArrayList<Integer> ef = data.getValue();
                    if(ef.size()>0)
                    {
                        for(Integer i : ef)
                        {
                            rf.setParticleGeneratorLocation(i ,e.x, e.y);
                        }
                    }

                }
            }

            //remove died objects
            for(Integer i: tickObject2RenderObject.keySet())
            {
                Boolean b = tickObject2RenderObject.get(i).getValue().getKey();
                if(!b)
                {
                    tickObject2RenderObject.remove(i);
                }
            }

        }
    }

    @Override
    protected void disconnected() {
        WindowVariables.game.switchScene(WindowVariables.mainMenuScene);
        WindowMsgBox.InfoMessage("Connection Lost", "Disconnected from Server");
    }
}
