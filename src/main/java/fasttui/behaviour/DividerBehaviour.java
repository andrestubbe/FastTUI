package fasttui.behaviour;

import fasttui.component.Component;
import fasttui.layout.SplitHorizontal;

public class DividerBehaviour implements Behaviour {

    public enum State {
        NORMAL,
        HOVERED,
        PRESSED
    }

    private final SplitHorizontal split;
    private boolean dragging;
    private State state = State.NORMAL;

    public DividerBehaviour(SplitHorizontal split) {
        this.split = split;
    }

    public boolean isDragging() {
        return dragging;
    }

    public State getState() {
        return state;
    }

    @Override
    public void onMousePressed(Component target, int cellX, int cellY) {
        dragging = true;
        state = State.PRESSED;
        split.setDividerPosition(cellX);
    }

    @Override
    public void onMouseReleased(Component target, int cellX, int cellY) {
        dragging = false;
        state = State.NORMAL;
    }

    @Override
    public void onMouseDragged(Component target, int cellX, int cellY) {
        if (dragging) split.setDividerPosition(cellX);
    }

    @Override
    public void onMouseEnter(Component target) {
        if (!dragging) state = State.HOVERED;
    }

    @Override
    public void onMouseExit(Component target) {
        if (!dragging) state = State.NORMAL;
    }
}
