package b451_Project.net;

import b451_Project.net.packets.PacketBase;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public abstract class TCPClient {
    private Socket client;
    private OutputStream sender;
    private InputStream receiver;


    TCPClient(String hostAddress)
    {

    }

    protected abstract void sendPacket(PacketBase p);

    protected abstract void packetReceived(PacketBase p);
}
