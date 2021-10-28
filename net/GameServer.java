package b451_Project.net;

import b451_Project.game.EntityFactory;
import b451_Project.game.Ship;
import b451_Project.net.packets.*;

import java.io.IOException;

/**
 * Game server class of the game
 * */
public class GameServer extends TCPServer{

    //ship movement variables
    public static final double MAX_SHIP_SPEED = 5.0;
    public static final double SHIP_ACCELERATION = 1.5;
    public static final double SHIP_FRICTION = 0.9;

    //player info
    private int playerCount = 0;
    private int[] playerHandle = new int[2];
    private ClientTickPacket[] playerKeyStatus = new ClientTickPacket[2];

    //game logic process
    private boolean inGame = false;
    private EntityFactory ef;
    private Ship[] playerShipEntities = new Ship[2];

    private void startNewRound()
    {
        ef = new EntityFactory();

        //set player ship locations
        playerShipEntities[0] = ef.makeShip(200, 240);
        playerShipEntities[1] = ef.makeShip(200, 480);
    }

    public GameServer() throws IOException
    {
        playerHandle[0] = -1;
        playerHandle[1] = -1;
        startNewRound();
    }

    private void setShipVelocity(ClientTickPacket t, Ship p)
    {
        if(!(t != null && p != null)) return;

        double vx = p.vx;
        double vy = p.vy;

        //apply velocity
        if(t.wKeyPressed)
        {
            vx += SHIP_ACCELERATION;
        }

        if(t.sKeyPressed)
        {
            vx -= SHIP_ACCELERATION;
        }

        if(t.aKeyPressed)
        {
            vy -= SHIP_ACCELERATION;
        }

        if(t.dKeyPressed)
        {
            vy += SHIP_ACCELERATION;
        }

        //check max speed
        if(Math.abs(vx) > MAX_SHIP_SPEED)
        {
            vx = (Math.abs(vx)/vx) * MAX_SHIP_SPEED;
        }

        if(Math.abs(vy) > MAX_SHIP_SPEED)
        {
            vy = (Math.abs(vy)/vy) * MAX_SHIP_SPEED;
        }

        //add friction
        vx *= SHIP_FRICTION;
        vy *= SHIP_FRICTION;
        p.vx = vx;
        p.vy = vy;
    }

    @Override
    protected void tick(double d)
    {
        if(ef == null) return;

        //got 2 players, ready to start the game
        if(playerCount == 2 && inGame == false)
        {
            inGame = true;
            startNewRound();
        }

        //update player ship location
        setShipVelocity(playerKeyStatus[0], playerShipEntities[0]);
        setShipVelocity(playerKeyStatus[1], playerShipEntities[1]);

        if(inGame)
        {

        }



        //process logic
        ef.tick();

        //send packets to clients
        TickPacket tp = new TickPacket();
        tp.entities = ef.getEntityList();

        System.out.println(tp.entities.get(0).x + " " + tp.entities.get(0).y);
        if(playerHandle[0] != -1)
        {
            sendPacket(tp, playerHandle[0]);
        }

        if(playerHandle[1] != -1)
        {
            sendPacket(tp, playerHandle[1]);
        }
    }

    @Override
    protected void clientConnected(int handle) {
        System.out.println("Client Connected to the Server, Handle: " + handle);
    }

    @Override
    protected void clientDisconnected(int handle) {
        System.out.println("Client Disconnected from the Server, Handle: " + handle);
        if(handle == playerHandle[0])
        {
            playerHandle[0] = -1;
            playerCount--;
        }

        if(handle == playerHandle[1])
        {
            playerHandle[1] = -1;
            playerCount--;
        }

    }

    @Override
    protected void packetReceived(PacketBase p, int handle) {

        //player join packet
        if(p instanceof JoinPacket)
        {
            //room full
            if(playerCount >= 2)
            {
                sendPacket(new MsgPacket("Unable to join the match", "maximum number of players reached"), handle);
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
            sendPacket(new JoinPacket(), handle);
            System.out.println("Client Joined the game, Handle: " + handle +" as Player: " + playerCount);
        }

        //only receive packets from joined player
        if(handle == playerHandle[0] || handle == playerHandle[1])
        {
            //client key status
            if(p instanceof ClientTickPacket)
            {
                if(handle == playerHandle[0])
                {
                    playerKeyStatus[0] = (ClientTickPacket)p;
                }else if(handle == playerHandle[1])
                {
                    playerKeyStatus[1] = (ClientTickPacket)p;
                }
            }


        }

    }
}
