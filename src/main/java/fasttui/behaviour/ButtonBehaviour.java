package fasttui.behaviour;

import fasttui.component.Component;
import fasttui.component.Interactive;

public class ButtonBehaviour implements Behaviour {

    private ButtonState buttonState = ButtonState.NORMAL;
    private ButtonListener buttonListener;
    private final Runnable action;

    public ButtonBehaviour(final Runnable action) {
        this.action = action;
    }

    @Override
    public void onMousePressed(final Component component, final int mx, final int my) {
        this.updateState(ButtonState.PRESSED);
    }

    @Override
    public void onMouseReleased(final Component component, final int mx, final int my) {
        boolean inside = component instanceof Interactive && ((Interactive) component).contains(mx, my);
        if (this.buttonState == ButtonState.PRESSED && inside && this.action != null) {
            this.action.run();
        }
        this.updateState(inside ? ButtonState.HOVERED : ButtonState.NORMAL);
    }

    @Override
    public void onMouseEnter(final Component component) {
        if (this.buttonState != ButtonState.PRESSED) {
            this.updateState(ButtonState.HOVERED);
        }
    }

    @Override
    public void onMouseExit(final Component component) {
        this.updateState(ButtonState.NORMAL);
    }

    public ButtonState getState() {
        return buttonState;
    }

    public void setListener(final ButtonListener buttonListener) {
        this.buttonListener = buttonListener;
    }

    private void updateState(ButtonState newButtonState) {
        if (this.buttonState != newButtonState) {
            this.buttonState = newButtonState;
            if (buttonListener != null) {
                buttonListener.onStateChanged(newButtonState);
            }
        }
    }
}
