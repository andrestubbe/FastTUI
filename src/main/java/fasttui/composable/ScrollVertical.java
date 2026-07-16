package fasttui.composable;

import fastterminal.FastTerminalScene;
import fasttui.component.Component;

public class ScrollVertical extends Component {

    private int foregroundColor;
    private int backgroundColor;

    private int totalItems;
    private int visibleItems;
    private int scrollOffset;

    public ScrollVertical(int x, int y, int height,
                          int foregroundColor, int backgroundColor) {
        super(x, y, 1, height);
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
    }

    public void update(int totalItems, int visibleItems, int scrollOffset) {
        this.totalItems = Math.max(1, totalItems);
        this.visibleItems = Math.max(1, visibleItems);
        this.scrollOffset = Math.max(0, Math.min(scrollOffset, totalItems - visibleItems));
    }

    @Override
    public void render(FastTerminalScene scene) {
        if (!visible || height <= 0) return;

        // Draw track
        for (int py = 0; py < height; py++) {
            scene.writeCell(x, y + py, ' ', backgroundColor, backgroundColor);
        }

        // Compute thumb size
        int thumbSize = Math.max(1, (visibleItems * height) / totalItems);

        // Compute thumb position
        int maxOffset = totalItems - visibleItems;
        int thumbPos = (maxOffset == 0)
                ? 0
                : (scrollOffset * (height - thumbSize)) / maxOffset;

        // Draw thumb
        for (int py = 0; py < thumbSize; py++) {
            scene.writeCell(x, y + thumbPos + py, '█', foregroundColor, backgroundColor);
        }
    }
}
