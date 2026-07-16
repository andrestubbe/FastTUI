package fasttui.behaviour;

import fasttui.component.Component;
import fasttui.component.Control;

public class ButtonBehaviour implements Behaviour {

    public enum State {
        NORMAL,
        HOVERED,
        PRESSED
    }

    private final Runnable action;
    private State state = State.NORMAL;

    public ButtonBehaviour(Runnable action) {
        this.action = action;
    }

    @Override
    public void onMousePressed(Component target, int mx, int my) {
        state = State.PRESSED;
    }

    @Override
    public void onMouseReleased(Component target, int mx, int my) {
        if (state == State.PRESSED && target instanceof Control && ((Control) target).contains(mx, my)) {
            if (action != null) action.run();
        }
        state = State.NORMAL;
    }

    @Override
    public void onMouseEnter(Component target) {
        if (state != State.PRESSED) state = State.HOVERED;
    }

    @Override
    public void onMouseExit(Component target) {
        state = State.NORMAL;
    }

    public State getState() {
        return state;
    }
}
