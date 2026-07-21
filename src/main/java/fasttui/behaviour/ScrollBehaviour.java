package fasttui.behaviour;

import fasttui.component.Component;
import fasttui.composable.ScrollVertical;

public class ScrollBehaviour implements Behaviour {

    private final ScrollVertical scroll;
    private boolean dragging = false;

    public ScrollBehaviour(ScrollVertical scroll) {
        this.scroll = scroll;
    }

    @Override
    public void onMouseMoved(Component target, int cellX, int cellY) {
        scroll.setHovered(true);
    }

    @Override
    public void onMousePressed(Component target, int cellX, int cellY) {
        dragging = true;
        scroll.setPressed(true);
        scroll.handleMouseClickOrDrag(cellY);
    }

    @Override
    public void onMouseReleased(Component target, int cellX, int cellY) {
        dragging = false;
        scroll.setPressed(false);
    }

    @Override
    public void onMouseDragged(Component target, int cellX, int cellY) {
        if (dragging) {
            scroll.setPressed(true);
            scroll.handleMouseClickOrDrag(cellY);
        }
    }
}
