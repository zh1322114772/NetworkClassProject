package b451_Project.net;

import b451_Project.global.WindowMsgBox;
import b451_Project.global.WindowVariables;
import b451_Project.net.packets.*;

import java.io.IOException;

public class GameClient extends TCPClient{

    private boolean wPressed = false;
    private boolean sPressed = false;
    private boolean aPressed = false;
    private boolean dPressed = false;
    private boolean joinedGame = false;
    public GameClient(String hostAddress) throws IOException
    {
        super(hostAddress);
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
            WindowVariables.game.switchScene(WindowVariables.GameScene);
        }

        if(p instanceof MsgPacket)
        {
            MsgPacket msg = (MsgPacket)p;
            WindowMsgBox.InfoMessage(msg.header, msg.text);
        }

        if(p instanceof TickPacket)
        {
            TickPacket tp = (TickPacket)p;
            //test purpose
            System.out.println(tp.entities.get(0).x + " " + tp.entities.get(0).y);
        }
    }

    @Override
    protected void disconnected() {
        WindowVariables.game.switchScene(WindowVariables.MainMenuScene);
        WindowMsgBox.InfoMessage("Connection Lost", "Disconnected from Server");
    }
}
