package b451_Project.global;
import b451_Project.ComputerNetworkProgrammingProject;
import b451_Project.scenes.GameScene;
import b451_Project.scenes.MainMenuScene;
import b451_Project.scenes.SceneBase;

public class WindowProperties {

    //avoid new instance
    private WindowProperties(){}

    public static SceneBase MainMenuScene = new MainMenuScene();

    public static SceneBase GameScene = new GameScene();

    public static ComputerNetworkProgrammingProject game = null;

    public static final int WINDOW_WIDTH = 1280;

    public static final int WINDOW_HEIGHT = 720;

    public static final int WINDOW_FRAME_RATE = 60;

}
