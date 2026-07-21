package fasttui.composable;

import fastterminal.FastTerminalScene;
import fasttui.component.ColorSet;
import fasttui.component.Control;
import fasttui.behaviour.ScrollBehaviour;

public class ScrollVertical extends Control {

    public interface ScrollListener {
        void onScroll(int scrollOffset);
    }

    private ColorSet colorSetFg;
    private ColorSet colorSetBg;

    private int foregroundColor;
    private int backgroundColor;

    private boolean isHovered = false;
    private boolean isPressed = false;

    private int totalItems = 1;
    private int visibleItems = 1;
    private int scrollOffset = 0;
    private ScrollListener listener = null;

    public ScrollVertical(int x, int y, int height,
                          int foregroundColor, int backgroundColor) {
        super(x, y, 1, height);
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        this.addBehavior(new ScrollBehaviour(this));
    }

    public ScrollVertical(int x, int y, int height,
                          ColorSet colorSetFg, ColorSet colorSetBg) {
        super(x, y, 1, height);
        this.colorSetFg = colorSetFg;
        this.colorSetBg = colorSetBg;
        this.foregroundColor = colorSetFg != null ? colorSetFg.normal : -1;
        this.backgroundColor = colorSetBg != null ? colorSetBg.normal : -1;
        this.addBehavior(new ScrollBehaviour(this));
    }

    public void setHovered(boolean hovered) {
        this.isHovered = hovered;
    }

    public void setPressed(boolean pressed) {
        this.isPressed = pressed;
    }

    public void setScrollListener(ScrollListener listener) {
        this.listener = listener;
    }

    public void handleMouseClickOrDrag(int cellY) {
        int relativeY = cellY - this.y;
        if (height > 0 && totalItems > visibleItems) {
            double ratio = (double) relativeY / height;
            ratio = Math.max(0.0, Math.min(1.0, ratio));
            int maxOffset = totalItems - visibleItems;
            int targetOffset = (int) (ratio * maxOffset);
            if (listener != null) {
                listener.onScroll(targetOffset);
            }
        }
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

        int activeFg = foregroundColor;
        int activeBg = backgroundColor;

        if (colorSetFg != null) {
            if (isPressed) activeFg = colorSetFg.pressed;
            else if (isHovered) activeFg = colorSetFg.hover;
            else activeFg = colorSetFg.normal;
        }

        if (colorSetBg != null) {
            if (isPressed) activeBg = colorSetBg.pressed;
            else if (isHovered) activeBg = colorSetBg.hover;
            else activeBg = colorSetBg.normal;
        }

        // Draw track
        for (int py = 0; py < height; py++) {
            scene.writeCell(x, y + py, ' ', activeBg, activeBg);
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

            scene.writeCell(x, y + cellY, ch, activeFg, activeBg);
        }
    }
}
