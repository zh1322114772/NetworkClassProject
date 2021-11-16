package b451_Project.global;

import b451_Project.game.EntityFactory;
import b451_Project.net.GameClient;
import b451_Project.net.GameServer;

public class GameVariables {

    private GameVariables(){};

    public static GameServer server = null;

    public static GameClient client = null;

    public static EntityFactory entityFactory = null;

    public static volatile double currentScore = 0;

    public static volatile double highestScore = 0;
}
