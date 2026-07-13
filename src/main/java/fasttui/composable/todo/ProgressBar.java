package fasttui.composable.todo;

import fastterminal.FastTerminalScene;
import fasttui.component.Component;

public class ProgressBar extends Component {
    private int filledColor;
    private int emptyColor;
    private int percent = 0;

    public ProgressBar(int x, int y, int width, int filledColor, int emptyColor) {
        super(x, y, width, 1);
        this.filledColor = filledColor;
        this.emptyColor = emptyColor;
    }

    public void setPercent(int percent) {
        this.percent = Math.max(0, Math.min(100, percent));
    }

    public int getPercent() {
        return this.percent;
    }

    @Override
    public void render(FastTerminalScene scene) {
        if (!visible || width <= 0 || height <= 0) return;

        int filled = (width * percent) / 100;
        String filledBar = repeat('█', filled);
        String emptyBar = repeat('░', width - filled);

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
}
