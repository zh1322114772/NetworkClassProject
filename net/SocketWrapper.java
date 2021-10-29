package b451_Project.net;

import b451_Project.net.packets.PacketBase;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This class wrap the Socket class
 * */
public class SocketWrapper {

    private Socket socket;
    private ObjectInputStream receiver;
    private ObjectOutputStream sender;
    private Thread receiveThread;
    private Queue<PacketBase> data;

    private volatile boolean threadFlag;

    public SocketWrapper(Socket s) throws IOException
    {
        socket = s;
        sender = new ObjectOutputStream(s.getOutputStream());
        receiver = new ObjectInputStream(s.getInputStream());
        data = new LinkedList<PacketBase>();
        threadFlag = true;

        //data receive thread
        receiveThread = new Thread(()->
        {
            while (threadFlag)
            {
                try
                {
                    Object p = receiver.readObject();
                    if(p instanceof PacketBase)
                    {
                        synchronized (this)
                        {
                            data.offer((PacketBase)p);
                        }
                    }
                }catch(Exception e)
                {
                    break;
                }
            }

            //close socket
            close();

        });
        receiveThread.start();
    }

    /**
     * return's true if received new data
     * */
    public boolean available()
    {
        synchronized (this)
        {
            return !data.isEmpty();
        }
    }

    /**
     * get received data from queue
     * */
    public PacketBase getData()
    {
        synchronized (this)
        {
            return data.poll();
        }
    }

    public void sendPacket(PacketBase p) throws IOException
    {
        sender.reset();
        sender.writeObject(p);
        sender.flush();
    }

    /**
     * close socket
     * */
    public void close()
    {
        synchronized (this)
        {
            while(!socket.isClosed())
            {
                try
                {
                    socket.close();
                    sender.close();
                    receiver.close();
                }catch(IOException e)
                {

                }
            }
        }
    }
}
