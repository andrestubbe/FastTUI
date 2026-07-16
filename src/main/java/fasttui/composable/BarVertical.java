package fasttui.composable;

import fastterminal.FastTerminalScene;
import fasttui.component.Component;

public class BarVertical extends Component {

    private static final char[] BLOCKS = {' ', '▁', '▂', '▃', '▄', '▅', '▆', '▇', '█'};
    private static final int BLOCK_LENGTH = BLOCKS.length;
    private final int totalLevels;

    private int foregroundColor;
    private int backgroundColor;
    private int percent;

    public BarVertical(int x, int y, int height, int foregroundColor, int backgroundColor) {
        super(x, y, 1, height);
        this.backgroundColor = backgroundColor;
        this.foregroundColor = foregroundColor;
        this.totalLevels = height * BLOCK_LENGTH;
    }

    @Override
    public void render(FastTerminalScene scene) {
        if (!visible || width <= 0 || height <= 0) return;
        int levels = (percent * totalLevels) / 100;
        for (int py = 0; py < height; py++) {
            int cellLevel = levels - ((height - 1 - py) * BLOCK_LENGTH);
            if (cellLevel < 0) cellLevel = 0;
            if (cellLevel > 8) cellLevel = 8;
            scene.writeCell(x, y + py, BLOCKS[cellLevel], foregroundColor, backgroundColor);
        }
    }

    public void setPercent(int percent) {
        this.percent = Math.max(0, Math.min(100, percent));
    }

}
