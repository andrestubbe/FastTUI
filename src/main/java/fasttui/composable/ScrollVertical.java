package fasttui.composable;

import fastterminal.FastTerminalScene;
import fasttui.component.Component;

public class ScrollVertical extends Component {

    private int foregroundColor;
    private int backgroundColor;

    private int totalItems = 1;
    private int visibleItems = 1;
    private int scrollOffset = 0;

    public ScrollVertical(int x, int y, int height,
                          int foregroundColor, int backgroundColor) {
        super(x, y, 1, height);
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
    }

    public void update(int totalItems, int visibleItems, int scrollOffset) {
        this.totalItems = Math.max(1, totalItems);
        this.visibleItems = Math.max(1, visibleItems);
        int maxOffset = Math.max(0, totalItems - visibleItems);
        this.scrollOffset = Math.max(0, Math.min(scrollOffset, maxOffset));
    }

    @Override
    public void render(FastTerminalScene scene) {
        if (!visible || height <= 0) return;

        // Draw track
        for (int py = 0; py < height; py++) {
            scene.writeCell(x, y + py, ' ', backgroundColor, backgroundColor);
        }

        // Half-pixel resolution
        int totalHalf = height * 2;

        // Thumb size in half-pixels
        int thumbSizeHalf = Math.max(1, (visibleItems * totalHalf) / totalItems);

        int maxOffset = totalItems - visibleItems;

        // Thumb position in half-pixels
        int thumbPosHalf = (maxOffset == 0)
                ? 0
                : (scrollOffset * (totalHalf - thumbSizeHalf)) / maxOffset;

        int thumbEndHalf = thumbPosHalf + thumbSizeHalf;

        // Draw thumb using half-blocks
        for (int cellY = 0; cellY < height; cellY++) {

            int cellTop = cellY * 2;
            int cellBottom = cellTop + 1;

            boolean topFilled = (cellTop >= thumbPosHalf && cellTop < thumbEndHalf);
            boolean bottomFilled = (cellBottom >= thumbPosHalf && cellBottom < thumbEndHalf);

            char ch;
            if (topFilled && bottomFilled) ch = '█';
            else if (topFilled) ch = '▀';
            else if (bottomFilled) ch = '▄';
            else ch = ' ';

            scene.writeCell(x, y + cellY, ch, foregroundColor, backgroundColor);
        }
    }
}
