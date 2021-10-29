package b451_Project.net;

import b451_Project.game.EntityFactory;
import b451_Project.game.Ship;
import b451_Project.global.WindowVariables;
import b451_Project.net.packets.*;

import java.io.IOException;

/**
 * Game server class of the game
 * */
public class GameServer extends TCPServer{

    //ship movement variables
    public static final float MAX_SHIP_SPEED = 40.0f;
    public static final float SHIP_ACCELERATION = 20.0f;
    public static final float SHIP_FRICTION = 0.8f;

    //player info
    private int playerCount = 0;
    private int[] playerHandle = new int[2];
    private ClientTickPacket[] playerKeyStatus = new ClientTickPacket[2];

    //game logic process
    private boolean inGame = false;
    private EntityFactory ef;
    private Ship[] playerShipEntities = new Ship[2];
    private TickPacket tp;

    private void startNewRound()
    {
        ef = new EntityFactory();

        //set player ship locations
        playerShipEntities[0] = ef.makeShip((float)WindowVariables.WINDOW_WIDTH * 0.3333f, (float)WindowVariables.WINDOW_HEIGHT * 0.7f);
        playerShipEntities[1] = ef.makeShip((float)WindowVariables.WINDOW_WIDTH * 0.6666f, (float)WindowVariables.WINDOW_HEIGHT * 0.7f);
        tp = new TickPacket();
        tp.entities = ef.getEntityList();
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

        float vx = p.vx;
        float vy = p.vy;

        //apply velocity
        if(t.wKeyPressed)
        {
            vy -= SHIP_ACCELERATION;
        }

        if(t.sKeyPressed)
        {
            vy += SHIP_ACCELERATION;
        }

        if(t.aKeyPressed)
        {
            vx -= SHIP_ACCELERATION;
        }

        if(t.dKeyPressed)
        {
            vx += SHIP_ACCELERATION;
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

        //send packets
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
