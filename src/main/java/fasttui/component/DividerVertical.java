package fasttui.component;

import fastterminal.FastTerminalScene;

public class DividerVertical extends Control {

    private int color = 0x555555;

    public DividerVertical(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public void render(FastTerminalScene scene) {
        if (!visible) return;
        for (int r = 0; r < height; r++) {
            scene.writeCell(x, y + r, '│', color, backgroundColor);
        }
    }
}
