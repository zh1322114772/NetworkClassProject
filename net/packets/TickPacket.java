package b451_Project.net.packets;

import b451_Project.game.Entity;
import b451_Project.game.ParticlePlayer;

import java.util.ArrayList;

/**
 * this packet is sent by server to clients to update entity locations
 * */
public class TickPacket implements PacketBase{
    public ArrayList<Entity> entities;
    public ArrayList<ParticlePlayer> particles;
}
