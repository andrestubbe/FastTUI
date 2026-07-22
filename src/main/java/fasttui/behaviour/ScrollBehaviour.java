package fasttui.behaviour;

import fasttui.component.Component;
import fasttui.composable.ScrollVertical;

public class ScrollBehaviour implements Behaviour {

    private final ScrollVertical scroll;
    private boolean dragging = false;
    private boolean draggingThumb = false;
    private int grabOffsetCellY = 0;

    public ScrollBehaviour(ScrollVertical scroll) {
        this.scroll = scroll;
    }

    @Override
    public void onMouseEnter(Component target) {
        scroll.setHovered(true);
    }

    @Override
    public void onMouseExit(Component target) {
        scroll.setHovered(false);
        if (!dragging) {
            scroll.setPressed(false);
        }
    }

    @Override
    public void onMouseMoved(Component target, int cellX, int cellY) {
        scroll.setHovered(true);
    }

    @Override
    public void onMousePressed(Component target, int cellX, int cellY) {
        dragging = true;
        scroll.setPressed(true);

        if (scroll.isClickOnThumb(cellY)) {
            draggingThumb = true;
            grabOffsetCellY = cellY - scroll.getY() - scroll.getThumbTopCell();
        } else {
            draggingThumb = false;
            scroll.handleMouseClickOrDrag(cellY);
        }
    }

    @Override
    public void onMouseReleased(Component target, int cellX, int cellY) {
        dragging = false;
        draggingThumb = false;
        scroll.setPressed(false);
        if (!scroll.contains(cellX, cellY)) {
            scroll.setHovered(false);
        }
    }

    @Override
    public void onMouseDragged(Component target, int cellX, int cellY) {
        if (dragging) {
            scroll.setPressed(true);
            if (draggingThumb) {
                int targetThumbTopCell = (cellY - scroll.getY()) - grabOffsetCellY;
                scroll.handleThumbDrag(targetThumbTopCell);
            } else {
                scroll.handleMouseClickOrDrag(cellY);
            }
        }
    }
}
