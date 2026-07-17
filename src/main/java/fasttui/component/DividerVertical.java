package fasttui.component;

import fastterminal.FastTerminalScene;
import fasttui.behaviour.DividerBehaviour;

public class DividerVertical extends Control {

    private int colorNormal = 0x666666;
    private int colorHovered = 0xAAAAAA;
    private int colorPressed = 0xFFFFFF;
    private char handleChar = '|';

    public DividerVertical(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public void setColorNormal(int color) {
        this.colorNormal = color;
    }

    public void setColorHovered(int color) {
        this.colorHovered = color;
    }

    public void setColorPressed(int color) {
        this.colorPressed = color;
    }

    public void setHandleChar(char handleChar) {
        this.handleChar = handleChar;
    }

    @Override
    public void render(FastTerminalScene scene) {
        if (!visible) return;

        int color = colorNormal;
        if (behaviors != null) {
            for (Object behavior : behaviors) {
                if (behavior instanceof DividerBehaviour) {
                    DividerBehaviour.State state = ((DividerBehaviour) behavior).getState();
                    switch (state) {
                        case HOVERED -> color = colorHovered;
                        case PRESSED -> color = colorPressed;
                        case NORMAL -> color = colorNormal;
                    }
                    break;
                }
            }
        }

        scene.writeCell(x, y + height / 2, handleChar, color, -1);
    }
}
