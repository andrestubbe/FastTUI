package fasttui.component;

import fastterminal.FastTerminalScene;

public abstract class Component {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected int backgroundColor = -1; // 0x0C0C0C
    protected int foregroundColor = 0xCCCCCC;
    protected boolean visible = true;

    public Component(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void render(FastTerminalScene scene);

    public boolean isVisible() {
        return visible;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getForegroundColor() {
        return foregroundColor;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setForegroundColor(int foregroundColor) {
        this.foregroundColor = foregroundColor;
    }
}
