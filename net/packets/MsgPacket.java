package b451_Project.net.packets;

/**
 * Display message box on client side
 * */
public class MsgPacket implements PacketBase{
    public String header = "";
    public String text = "";
    public MsgPacket(String h, String t)
    {
        header = h;
        text = t;
    }
}
