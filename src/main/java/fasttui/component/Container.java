package fasttui.component;

import fastterminal.FastTerminalScene;

import java.util.ArrayList;
import java.util.List;

public class Container extends Component {

    protected final List<Component> children = new ArrayList<>();

    public Container(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public void add(Component child) {
        if (child != null) {
            child.setParent(this);
            children.add(child);
        }
    }

    public void addAll(Component[] comps) {
        for (Component child : comps) {
            if (child != null) {
                child.setParent(this);
                children.add(child);
            }
        }
    }

    public boolean removeChild(Component child) {
        if (child == null) return false;
        if (children.remove(child)) {
            child.setParent(null);
            return true;
        }
        return false;
    }

    public void removeChildren(Component[] comps) {
        if (comps == null || comps.length == 0) return;
        for (Component c : comps) {
            if (children.remove(c)) {
                c.setParent(null);
            }
        }
    }

    public void removeAll() {
        for (Component child : children) {
            child.setParent(null);
        }
        children.clear();
    }

    @Override
    public void render(FastTerminalScene scene) {
        if (!visible) return;
        int absX = getAbsoluteX();
        int absY = getAbsoluteY();
        if (backgroundColor != -1) {
            for (int r = 0; r < height; r++) {
                for (int c = 0; c < width; c++) {
                    scene.writeCell(absX + c, absY + r, ' ', -1, backgroundColor);
                }
            }
        }
        for (Component child : children) {
            child.render(scene);
        }
    }

    public List<Component> getChildren() {
        return children;
    }

    @Override
    public void setX(int newX) {
        int dx = newX - this.x;
        super.setX(newX);
        for (Component child : children) {
            child.setX(child.getX() + dx);
        }
    }

    @Override
    public void setY(int newY) {
        int dy = newY - this.y;
        super.setY(newY);
        for (Component child : children) {
            child.setY(child.getY() + dy);
        }
    }
}
