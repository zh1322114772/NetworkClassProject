package b451_Project.net;

import b451_Project.game.Entity;
import b451_Project.game.Ship;
import b451_Project.global.ConfigVariables;
import b451_Project.global.WindowMsgBox;
import b451_Project.global.WindowVariables;
import b451_Project.net.packets.*;
import b451_Project.render.RenderFactory;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.io.IOException;
import java.util.HashMap;

public class GameClient extends TCPClient{

    private boolean wPressed = false;
    private boolean sPressed = false;
    private boolean aPressed = false;
    private boolean dPressed = false;
    private boolean joinedGame = false;

    //to synchronize data part and render part
    private HashMap<Integer, Pair<Integer, Boolean>> tickObject2RenderObject;

    public GameClient(String hostAddress) throws IOException
    {
        super(hostAddress);
        tickObject2RenderObject = new HashMap<Integer, Pair<Integer, Boolean>>();
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
                Boolean b = tickObject2RenderObject.get(i).getValue();
                b = false;
            }

            //update render frame
            RenderFactory rf = WindowVariables.gameScene.getRenderFactory();
            for(Entity e : tp.entities)
            {
                Pair<Integer, Boolean> pair = tickObject2RenderObject.get(e.ID);
                if(pair == null)
                {
                    //new object
                    Integer id = 0;
                    if(e instanceof Ship)
                    {
                        id = rf.makePolygon(-1, 1.0f/ ConfigVariables.GAME_TICK_RATE, Color.BLUE, 3, 40, 0, e.rotation, e.x, e.y);
                    }

                    //add to hashmap
                    pair = new Pair<Integer, Boolean>(id, true);
                    tickObject2RenderObject.put(e.ID, pair);
                }else
                {
                    //update existence objects
                    rf.setPolygonCenterLocation(pair.getKey(), e.x, e.y);
                    rf.setPolygonRotation(pair.getKey(), e.rotation);
                    Boolean val = pair.getValue();
                    val = true;
                }
            }

            //remove died objects
            for(Integer i: tickObject2RenderObject.keySet())
            {
                Boolean b = tickObject2RenderObject.get(i).getValue();
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
