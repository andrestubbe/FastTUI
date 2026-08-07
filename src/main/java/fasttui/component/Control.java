package fasttui.component;

import fasttui.behaviour.Behaviour;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Control extends Container implements Interactive {

    protected List<Behaviour> behaviors = null;

    public Control(int x, int y, int width, int height) {
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

    public List<Behaviour> getBehaviors() {
        return behaviors == null ? Collections.emptyList() : behaviors;
    }

    public boolean contains(int cellX, int cellY) {
        int absX = getAbsoluteX();
        int absY = getAbsoluteY();
        return cellX >= absX && cellX < absX + width && cellY >= absY && cellY < absY + height;
    }
}
