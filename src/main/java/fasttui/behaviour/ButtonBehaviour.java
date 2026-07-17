package fasttui.behaviour;

import fasttui.component.Component;
import fasttui.component.Control;

public class ButtonBehaviour implements Behaviour {

    public enum State {
        NORMAL,
        HOVERED,
        PRESSED
    }

    public interface Listener {
        void onStateChanged(State newState);
    }

    private final Runnable action;
    private State state = State.NORMAL;

    private Listener listener;

    public ButtonBehaviour(Runnable action) {
        this.action = action;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private void updateState(State newState) {
        if (this.state != newState) {
            this.state = newState;
            if (listener != null) {
                listener.onStateChanged(newState);
            }
        }
    }

    @Override
    public void onMousePressed(Component component, int mx, int my) {
        updateState(State.PRESSED);
    }

    @Override
    public void onMouseReleased(Component component, int mx, int my) {
        boolean inside = component instanceof Control &&
                ((Control) component).contains(mx, my);

        if (state == State.PRESSED && inside && action != null) {
            action.run();
        }

        updateState(State.NORMAL);
    }

    @Override
    public void onMouseEnter(Component component) {
        if (state != State.PRESSED) {
            updateState(State.HOVERED);
        }
    }

    @Override
    public void onMouseExit(Component component) {
        updateState(State.NORMAL);
    }

    public State getState() {
        return state;
    }
}
