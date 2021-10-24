package b451_Project.net;

import b451_Project.net.packets.JoinPacket;
import b451_Project.net.packets.MsgPacket;
import b451_Project.net.packets.PacketBase;
import java.io.IOException;

/**
 * Game server class of the game
 * */
public class GameServer extends TCPServer{

    //player info
    private int playerCount = 0;
    private int[] playerHandle = new int[2];
    private String[] playerName = new String[2];


    private void startNewRound()
    {

    }

    public GameServer() throws IOException
    {

    }

    @Override
    protected void tick(double d)
    {

    }

    @Override
    protected void clientConnected(int handle) {

    }

    @Override
    protected void clientDisconnected(int handle) {

    }

    @Override
    protected void packetReceived(PacketBase p, int handle) {

        //player join packet
        if(p instanceof JoinPacket)
        {
            //room full
            if(playerCount >= 2)
            {
                sendPacket((PacketBase) (new MsgPacket("Unable to join the match", "maximum number of players reached")), handle);
                return;
            }

            //avoid same player join twice
            if(playerCount > 0)
            {
                if(playerHandle[0] == handle) return;
            }

            //join game
            playerHandle[playerCount] = handle;
            playerCount++;

        }
    }
}
