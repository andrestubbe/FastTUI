package fasttui.composable;

import fastterminal.FastTerminalScene;
import fasttui.component.Component;

public class ScrollHorizontal extends Component {

    private int foregroundColor;
    private int backgroundColor;

    private int totalItems = 1;
    private int visibleItems = 1;
    private int scrollOffset = 0;

    public ScrollHorizontal(int x, int y, int width, int fg, int bg) {
        super(x, y, width, 1);
        this.foregroundColor = fg;
        this.backgroundColor = bg;
    }

    public void update(int totalItems, int visibleItems, int scrollOffset) {
        this.totalItems = Math.max(1, totalItems);
        this.visibleItems = Math.max(1, visibleItems);

        int maxOffset = Math.max(0, totalItems - visibleItems);
        this.scrollOffset = Math.max(0, Math.min(scrollOffset, maxOffset));
    }

    @Override
    public void render(FastTerminalScene scene) {
        if (!visible || width <= 0) return;

        // Draw track
        for (int px = 0; px < width; px++) {
            scene.writeCell(x + px, y, ' ', backgroundColor, backgroundColor);
        }

        // Thumb size in cells (simple proportional)
        int thumbSize = Math.max(1, (visibleItems * width) / totalItems);

        int maxOffset = totalItems - visibleItems;

        // Thumb position in cells
        int thumbPos = (maxOffset == 0)
                ? 0
                : (scrollOffset * (width - thumbSize)) / maxOffset;

        // Draw thumb
        for (int px = 0; px < thumbSize; px++) {
            scene.writeCell(x + thumbPos + px, y, '▄', foregroundColor, backgroundColor);
        }
    }
}
