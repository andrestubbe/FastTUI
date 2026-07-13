package fasttui.behaviour;

import fasttui.component.Component;
import fasttui.component.Control;

public class ButtonBehaviour implements Behaviour {
    private final Runnable action;
    private boolean isPressed = false;
    private boolean isHovered = false;

    public ButtonBehaviour(Runnable action) {
        this.action = action;
    }

    @Override
    public void onMousePressed(Component target, int mx, int my) {
        isPressed = true;
    }

    @Override
    public void onMouseReleased(Component target, int mx, int my) {
        if (isPressed && target instanceof Control && ((Control) target).contains(mx, my)) {
            if (action != null) {
                action.run();
            }
        }
        isPressed = false;
    }

    @Override
    public void onMouseEnter(Component target) {
        isHovered = true;
    }

    @Override
    public void onMouseExit(Component target) {
        isHovered = false;
        isPressed = false;
    }

    public boolean isPressed() {
        return isPressed;
    }

    public boolean isHovered() {
        return isHovered;
    }
}
