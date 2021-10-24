package b451_Project.net;

import b451_Project.net.packets.PacketBase;
import java.io.IOException;

public class GameClient extends TCPClient{

    public GameClient(String hostAddress) throws IOException
    {
        super(hostAddress);
    }

    @Override
    protected void tick(double d) {

    }

    @Override
    protected void packetReceived(PacketBase p) {

    }

    @Override
    protected void disconnected() {

    }
}
