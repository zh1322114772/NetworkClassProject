package b451_Project.net;
import b451_Project.global.ConfigVariables;
import b451_Project.net.packets.PacketBase;
import b451_Project.utils.Timer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;


/**
 * This class wrap the ServerScoket
 * */

public abstract class TCPServer {

    //client socket list
    private ServerSocket server;
    private ArrayList<Socket> clientSockets = new ArrayList<Socket>();
    private ArrayList<ObjectOutputStream> socketPacketSender = new ArrayList<ObjectOutputStream>();
    private ArrayList<ObjectInputStream> socketPacketReceiver = new ArrayList<ObjectInputStream>();
    private ArrayList<Long> clientTimestamp = new ArrayList<Long>();

    private Thread connectionListenThread = new Thread();
    private Timer dataReceiveThread = new Timer(false);
    private Timer loopThread = new Timer(false);

    private volatile boolean threadFlag = true;

    public TCPServer() throws IOException
    {

        server = new ServerSocket(ConfigVariables.SERVER_PORT);

        //connection establishment thread
        connectionListenThread = new Thread(()->
        {
            //listen new connection
            while(threadFlag)
            {
                try
                {
                    Socket client = server.accept();
                    int handle;
                    synchronized (this)
                    {
                        //establish encrypted connection
                        clientSockets.add(client);
                        socketPacketSender.add(new ObjectOutputStream(client.getOutputStream()));
                        socketPacketReceiver.add(new ObjectInputStream(client.getInputStream()));
                        clientTimestamp.add(System.currentTimeMillis());
                        handle = clientSockets.size() - 1;
                        clientConnected(handle);
                    }

                }catch(IOException e)
                {

                }

            }
        });
        connectionListenThread.start();

        //heartbeat packet check thread
        loopThread.start((d)->
        {
            long currentMS = System.currentTimeMillis();
            synchronized (this)
            {
                //check timeout
                for(int i = clientTimestamp.size() - 1; i>=0; i--)
                {
                    if((currentMS - clientTimestamp.get(i).longValue()) > ConfigVariables.CONNECTION_TIMEOUT)
                    {
                        clientDisconnected(i);

                        //force close socket
                        while(!clientSockets.get(i).isClosed())
                        {
                            try
                            {
                                clientSockets.get(i).close();
                            }catch (Exception e)
                            {

                            }
                        }

                    }

                    //remove closed client from list
                    clientSockets.remove(i);
                    clientTimestamp.remove(i);
                    socketPacketReceiver.remove(i);
                    socketPacketSender.remove(i);
                }

            }
        }, 1.0/ConfigVariables.GAME_TICK_RATE);

        //receive data from client
        dataReceiveThread.start((d)->
        {
            synchronized (this) {
                for (int i = 0; i < clientSockets.size(); i++) {
                    try {
                        Object o = socketPacketReceiver.get(i).readObject();
                        if (o instanceof PacketBase) {
                            //update client timestamp
                            clientTimestamp.set(i, System.currentTimeMillis());
                            packetReceived((PacketBase)o, i);
                        }
                    } catch (Exception e) {

                    }
                }
            }
        }, 1.0/ConfigVariables.GAME_TICK_RATE);

    }


    /**
     * stop server
     * */
    public void stop() throws IOException
    {
        threadFlag = false;
        loopThread.stop();
        dataReceiveThread.stop();
    }

    /**
     * client connection established callback
     * @param handle client handle
     * */
    protected abstract void clientConnected(int handle);

    /**
     * client disconnected callback
     * @param handle client handle
     * */
    protected abstract void clientDisconnected(int handle);

    /**
     * new packet received from a client
     * @param p packet
     * @param handle client handle
     * */
    protected abstract void packetReceived(PacketBase p, int handle);

    /**
     * send a packet to a client
     * @param handle client handle
     * */
    public boolean sendPacket(PacketBase p, int handle)
    {
        synchronized (this)
        {
            if(handle < socketPacketSender.size())
            {
                try
                {
                    socketPacketSender.get(handle).writeObject(p);
                    return true;
                }catch(IOException e)
                {

                }

            }
            return false;
        }
    }

}
