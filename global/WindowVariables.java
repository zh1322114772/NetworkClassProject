package b451_Project.global;
import b451_Project.ComputerNetworkProgrammingProject;
import b451_Project.scenes.GameScene;
import b451_Project.scenes.MainMenuScene;

public class WindowVariables {

    //avoid new instance
    private WindowVariables(){}

    public static MainMenuScene mainMenuScene = new MainMenuScene();

    public static GameScene gameScene = new GameScene();

    public static ComputerNetworkProgrammingProject game = null;

    public static final int WINDOW_WIDTH = 1366;

    public static final int WINDOW_HEIGHT = 768;

    public static final int WINDOW_FRAME_RATE = 60;

}
