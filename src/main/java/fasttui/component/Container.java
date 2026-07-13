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
            child.setX(this.x + child.getX());
            child.setY(this.y + child.getY());
            children.add(child);
        }
    }

    @Override
    public void render(FastTerminalScene scene) {
        if (!visible) return;
        if (backgroundColor != -1) {
            for (int r = 0; r < height; r++) {
                for (int c = 0; c < width; c++) {
                    scene.writeCell(x + c, y + r, ' ', -1, backgroundColor);
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
