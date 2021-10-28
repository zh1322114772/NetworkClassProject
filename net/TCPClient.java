package b451_Project.net;

import b451_Project.global.ConfigVariables;
import b451_Project.net.packets.HelloPacket;
import b451_Project.net.packets.PacketBase;
import b451_Project.utils.Timer;

import java.io.*;
import java.net.Socket;

public abstract class TCPClient {
    private SocketWrapper client;
    long timeStamp = 0;
    private Timer loopThread;

    TCPClient(String hostAddress) throws IOException
    {
        client = new SocketWrapper(new Socket(hostAddress, ConfigVariables.SERVER_PORT));
        loopThread = new Timer(false);

        //client tick loop thread
        loopThread.start((d)->
        {
            long halfTimeOut = ConfigVariables.CONNECTION_TIMEOUT / 2;
            synchronized (this)
            {
                //check if new arrival packets from server
                while(client.available())
                {
                    packetReceived(client.getData());
                }

                //tick
                tick(d);

                //check if it's necessary to send the hello packet
                if(System.currentTimeMillis() - timeStamp > halfTimeOut)
                {
                    sendPacket(new HelloPacket());
                }
            }

        }, 1.0/ConfigVariables.GAME_TICK_RATE);

    }

    /**
     * stop connection
     * */
    public void stop()
    {
        loopThread.stop();
        client.close();
    }

    @Override
    public void finalize()
    {
        stop();
    }

    /**
     * send a new packet
     * @param p packet
     * */
    public void sendPacket(PacketBase p)
    {
        synchronized (this)
        {
            try
            {
                client.sendPacket(p);
                timeStamp = System.currentTimeMillis();
            }catch(IOException e)
            {
                System.out.println(e);
                e.printStackTrace();
                //unable to communicate with server
                disconnected();
                stop();
            }
        }

    }

    /**
     * client tick callback
     * @param d delta d
     * */
    protected abstract void tick(double d);

    /**
     * packet received call back event
     * @param p received packet
     * */
    protected abstract void packetReceived(PacketBase p);

    /**
     * callback when client is disconnected from server
     */
    protected abstract void disconnected();
}
