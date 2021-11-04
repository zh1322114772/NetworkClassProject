package b451_Project.game;
import java.util.ArrayList;

/**
 * define AI entity in the game
 * */
public interface AIEntity {

    /**
     * tick call back
     * @param e entity list
     * @oaram delta T
     * */
    void tick(ArrayList<Entity> e, double d);

}
