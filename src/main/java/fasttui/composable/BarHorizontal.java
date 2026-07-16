package fasttui.composable;

import fastterminal.FastTerminalScene;
import fasttui.component.Component;

public class BarHorizontal extends Component {
    private char filledChar = '█';
    private char emptyChar = '░';
    private int filledColor;
    private int emptyColor;
    private int percent = 0;

    public BarHorizontal(int x, int y, int width, int filledColor, int emptyColor) {
        super(x, y, width, 1);
        this.filledColor = filledColor;
        this.emptyColor = emptyColor;
    }

    @Override
    public void render(FastTerminalScene scene) {
        if (!visible || width <= 0 || height <= 0) return;

        int filled = (width * percent) / 100;
        String filledBar = repeat(filledChar, filled);
        String emptyBar = repeat(emptyChar, width - filled);

        scene.writeString(x, y, filledBar, filledColor, -1);
        scene.writeString(x + filled, y, emptyBar, emptyColor, -1);
    }

    private String repeat(char ch, int count) {
        if (count <= 0) return "";
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            sb.append(ch);
        }
        return sb.toString();
    }

    public void setFilledChar(char filledChar) {
        this.filledChar = filledChar;
    }

    public void setEmptyChar(char emptyChar) {
        this.emptyChar = emptyChar;
    }

    public void setFilledColor(int filledColor) {
        this.filledColor = filledColor;
    }

    public void setEmptyColor(int emptyColor) {
        this.emptyColor = emptyColor;
    }

    public void setPercent(int percent) {
        this.percent = Math.max(0, Math.min(100, percent));
    }
}
