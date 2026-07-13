package fasttui.behaviour;

import fasttui.component.Component;
import fasttui.component.Container;
import fasttui.component.Control;

public class EventDispatcher {
    private static Component hoveredComponent = null;

    public static boolean dispatchMouseClick(Component root, int mx, int my, boolean isPressed) {
        if (!root.isVisible()) return false;

        if (root instanceof Container) {
            Container container = (Container) root;
            for (int i = container.getChildren().size() - 1; i >= 0; i--) {
                if (dispatchMouseClick(container.getChildren().get(i), mx, my, isPressed)) {
                    return true;
                }
            }
        }

        if (root instanceof Control && ((Control) root).contains(mx, my)) {
            Control control = (Control) root;
            for (Behaviour b : control.getBehaviors()) {
                if (isPressed) b.onMousePressed(control, mx, my);
                else b.onMouseReleased(control, mx, my);
            }
            return !control.getBehaviors().isEmpty();
        }

        return false;
    }

    public static void dispatchMouseMove(Component root, int mx, int my) {
        Component hit = findComponentAt(root, mx, my);

        if (hit != hoveredComponent) {
            if (hoveredComponent instanceof Control) {
                Control hc = (Control) hoveredComponent;
                for (Behaviour b : hc.getBehaviors()) {
                    b.onMouseExit(hc);
                }
            }
            hoveredComponent = hit;
            if (hoveredComponent instanceof Control) {
                Control hc = (Control) hoveredComponent;
                for (Behaviour b : hc.getBehaviors()) {
                    b.onMouseEnter(hc);
                }
            }
        }

        if (hoveredComponent instanceof Control) {
            Control hc = (Control) hoveredComponent;
            for (Behaviour b : hc.getBehaviors()) {
                b.onMouseMoved(hc, mx, my);
            }
        }
    }

    public static void dispatchMouseDrag(Component root, int mx, int my) {
        Component hit = findComponentAt(root, mx, my);
        if (hit instanceof Control) {
            Control hc = (Control) hit;
            for (Behaviour b : hc.getBehaviors()) {
                b.onMouseDragged(hc, mx, my);
            }
        }
    }

    private static boolean isWithinBounds(Component c, int mx, int my) {
        return mx >= c.getX() && mx < c.getX() + c.getWidth() &&
               my >= c.getY() && my < c.getY() + c.getHeight();
    }

    private static Component findComponentAt(Component root, int mx, int my) {
        if (!root.isVisible() || !isWithinBounds(root, mx, my)) return null;

        if (root instanceof Container) {
            Container container = (Container) root;
            for (int i = container.getChildren().size() - 1; i >= 0; i--) {
                Component hit = findComponentAt(container.getChildren().get(i), mx, my);
                if (hit != null) {
                    if (hit instanceof Control || !(root instanceof Control)) {
                        return hit;
                    }
                }
            }
        }

        return root;
    }
}
