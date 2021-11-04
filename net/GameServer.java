package b451_Project.net;

import b451_Project.game.Asteroid;
import b451_Project.game.EntityFactory;
import b451_Project.game.Ship;
import b451_Project.global.GameVariables;
import b451_Project.global.WindowVariables;
import b451_Project.net.packets.*;

import java.io.IOException;
import java.util.Random;

/**
 * Game server class of the game
 * */
public class GameServer extends TCPServer{

    //ship movement variables
    public static final float MAX_SHIP_SPEED = 60.0f;
    public static final float SHIP_ACCELERATION = 30.0f;
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
    private Random ranGen;
    private void startNewRound()
    {
        ef = new EntityFactory();
        GameVariables.entityFactory = ef;
        //set player ship locations
        playerShipEntities[0] = ef.makeShip((float)WindowVariables.WINDOW_WIDTH * 0.3333f, (float)WindowVariables.WINDOW_HEIGHT * 0.7f);
        playerShipEntities[1] = ef.makeShip((float)WindowVariables.WINDOW_WIDTH * 0.6666f, (float)WindowVariables.WINDOW_HEIGHT * 0.7f);
        tp = new TickPacket();
        tp.entities = ef.getEntityList();
        tp.particles = ef.particles;
    }

    public GameServer() throws IOException
    {
        ranGen = new Random();
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

        //check ship health
        if(p.hp > 0)
        {
            //add friction
            vx *= SHIP_FRICTION;
            vy *= SHIP_FRICTION;
            p.vx = vx;
            p.vy = vy;
        }else
        {
            p.vx = 0;
            p.vy = 0;
        }
    }

    private void gameLogicProcess(double d)
    {
        if(playerShipEntities[0].hp <= 0 && playerShipEntities[1].hp <= 0)
        {
            inGame = false;
        }

        //generate asteroid
        if(ranGen.nextFloat() > 0.9)
        {
            float shootingDirection = (float)(Math.random() * 1.5707963) + 0.78539815f;
            float shootingVelocity = (float)(Math.random() * 80) + 20;

            Asteroid a = ef.makeAsteroid(ranGen.nextFloat() * WindowVariables.WINDOW_WIDTH, -50f);
            a.vy = (float)Math.sin(shootingDirection) * shootingVelocity;
            a.vx = (float)Math.cos(shootingDirection) * shootingVelocity;
            a.rotation = shootingDirection + 3.1415926f;
        }

        //generate missile
        if(ranGen.nextFloat() > 0.97)
        {
            ef.makeMissile(ranGen.nextFloat() * WindowVariables.WINDOW_WIDTH, -50f);
        }

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
            gameLogicProcess(d);
        }

        //process logic
        ef.tick(d);

        //send packets
        if(playerHandle[0] != -1)
        {
            sendPacket(tp, playerHandle[0]);
        }

        if(playerHandle[1] != -1)
        {
            sendPacket(tp, playerHandle[1]);
        }

        ef.particles.clear();
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
            inGame = false;
            sendPacket(new MsgPacket("Game ended", "Player Disconnected from the game"), playerHandle[1]);
        }

        if(handle == playerHandle[1])
        {
            playerHandle[1] = -1;
            playerCount--;
            inGame = false;
            sendPacket(new MsgPacket("Game ended", "Player Disconnected from the game"), playerHandle[0]);
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
