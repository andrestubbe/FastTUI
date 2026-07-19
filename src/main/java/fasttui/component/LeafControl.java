package fasttui.component;

import fasttui.behaviour.Behaviour;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class LeafControl extends Component implements Interactive {

    protected List<Behaviour> behaviors = null;

    public LeafControl(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public void addBehavior(Behaviour behavior) {
        if (behavior != null) {
            if (this.behaviors == null) {
                this.behaviors = new ArrayList<>();
            }
            this.behaviors.add(behavior);
        }
    }

    public void removeBehavior(Behaviour behavior) {
        if (behavior != null && this.behaviors != null) {
            this.behaviors.remove(behavior);
            if (this.behaviors.isEmpty()) {
                this.behaviors = null;
            }
        }
    }

    @Override
    public List<Behaviour> getBehaviors() {
        return behaviors == null ? Collections.emptyList() : behaviors;
    }

    @Override
    public boolean contains(int cellX, int cellY) {
        return cellX >= x && cellX < x + width && cellY >= y && cellY < y + height;
    }
}
