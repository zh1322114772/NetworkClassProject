package b451_Project.net.packets;

/**
 * this packet should be sent by client to indicate current client key pressed status
 * */
public class ClientTickPacket implements PacketBase {

    public boolean wKeyPressed;
    public boolean sKeyPressed;
    public boolean aKeyPressed;
    public boolean dKeyPressed;
}
