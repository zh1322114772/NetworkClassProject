package b451_Project.net;
import b451_Project.global.ConfigVariables;
import b451_Project.net.packets.HelloPacket;
import b451_Project.net.packets.PacketBase;
import b451_Project.utils.Timer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This class wrap the ServerScoket
 * */

public abstract class TCPServer {

    //client socket list
    private ServerSocket server;
    private ArrayList<SocketWrapper> clientSockets = new ArrayList<SocketWrapper>();
    private ArrayList<Long> clientTimestamp = new ArrayList<Long>();

    private Thread connectionListenThread = new Thread();
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
                        //establish connection
                        clientSockets.add(new SocketWrapper(client));
                        clientTimestamp.add(System.currentTimeMillis());
                        handle = clientSockets.size() - 1;
                        clientConnected(handle);
                    }

                }catch(IOException e)
                {
                    System.out.println(e);
                    e.printStackTrace();
                    break;
                }

            }
            System.out.println("listen thread stopped");
        });
        connectionListenThread.start();

        //server tick loop thread
        loopThread.start((d)->
        {
            long currentMS = System.currentTimeMillis();
            synchronized (this)
            {
                //check new arrival packets from clients
                for(int i = 0; i < clientSockets.size(); i++)
                {
                    while(clientSockets.get(i).available())
                    {
                        //avoid hello packet
                        PacketBase p = clientSockets.get(i).getData();
                        if(!(p instanceof HelloPacket))
                        {
                            packetReceived(p, i);
                        }
                        //update client timestamp
                        clientTimestamp.set(i, System.currentTimeMillis());
                    }

                }

                //client timeout check
                for(int i = clientTimestamp.size() - 1; i>=0; i--)
                {
                    if((currentMS - clientTimestamp.get(i).longValue()) > ConfigVariables.CONNECTION_TIMEOUT)
                    {
                        clientDisconnected(i);
                        //force close socket
                        disconnect(i);
                    }

                }

                tick(d);
            }

        }, 1.0/ConfigVariables.GAME_TICK_RATE);

    }


    /**
     * stop server
     * */
    public void stop()
    {
        try
        {
            for(SocketWrapper s : clientSockets)
            {
                s.close();
            }
            threadFlag = false;
            server.close();
            loopThread.stop();
        }
        catch(Exception e)
        {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    @Override
    public void finalize()
    {
        stop();
    }

    /**
     * server tick callback event
     * @param d delta t
     * */
    protected abstract void tick(double d);

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
     * disconnect a client
     * @param handle client handle
     *
     * */
    public void disconnect(int handle)
    {
        synchronized (this)
        {
            if(handle < clientSockets.size())
            {
                clientSockets.get(handle).close();
                clientSockets.remove(handle);
                clientTimestamp.remove(handle);
            }
        }
    }


    /**
     * send a packet to a client
     * @param handle client handle
     * */
    public boolean sendPacket(PacketBase p, int handle)
    {
        synchronized (this)
        {
            if(handle < clientSockets.size())
            {
                try
                {
                    clientSockets.get(handle).sendPacket(p);
                    return true;
                }catch(IOException e)
                {
                    clientDisconnected(handle);
                    //force close socket
                    disconnect(handle);
                    System.out.println(e);
                    e.printStackTrace();
                }
            }
            return false;
        }
    }
}
