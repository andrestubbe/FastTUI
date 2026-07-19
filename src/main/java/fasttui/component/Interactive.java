package fasttui.component;

import fasttui.behaviour.Behaviour;
import java.util.List;

public interface Interactive {
    List<Behaviour> getBehaviors();
    boolean contains(int cellX, int cellY);
}
