package fasttui.behaviour;

import fasttui.component.Component;
import fasttui.component.Container;
import fasttui.component.Control;
import fasttui.component.Interactive;

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

        if (root instanceof Interactive && ((Interactive) root).contains(mx, my)) {
            Interactive interactive = (Interactive) root;
            for (Behaviour b : interactive.getBehaviors()) {
                if (isPressed) b.onMousePressed(root, mx, my);
                else b.onMouseReleased(root, mx, my);
            }
            return !interactive.getBehaviors().isEmpty();
        }

        return false;
    }

    public static void dispatchMouseMove(Component root, int mx, int my) {
        Component hit = findComponentAt(root, mx, my);

        if (hit != hoveredComponent) {
            if (hoveredComponent instanceof Interactive) {
                Interactive hc = (Interactive) hoveredComponent;
                for (Behaviour b : hc.getBehaviors()) {
                    b.onMouseExit(hoveredComponent);
                }
            }
            hoveredComponent = hit;
            if (hoveredComponent instanceof Interactive) {
                Interactive hc = (Interactive) hoveredComponent;
                for (Behaviour b : hc.getBehaviors()) {
                    b.onMouseEnter(hoveredComponent);
                }
            }
        }

        if (hoveredComponent instanceof Interactive) {
            Interactive hc = (Interactive) hoveredComponent;
            for (Behaviour b : hc.getBehaviors()) {
                b.onMouseMoved(hoveredComponent, mx, my);
            }
        }
    }

    public static void dispatchMouseDrag(Component root, int mx, int my) {
        Component hit = findComponentAt(root, mx, my);
        if (hit instanceof Interactive) {
            Interactive hc = (Interactive) hit;
            for (Behaviour b : hc.getBehaviors()) {
                b.onMouseDragged(hit, mx, my);
            }
        }
    }

    private static boolean isWithinBounds(Component c, int mx, int my) {
        return mx >= c.getX() && mx < c.getX() + c.getWidth() &&
               my >= c.getY() && my < c.getY() + c.getHeight();
    }

    public static Component findComponentAt(Component root, int mx, int my) {
        if (!root.isVisible() || !isWithinBounds(root, mx, my)) return null;

        if (root instanceof Container) {
            Container container = (Container) root;
            for (int i = container.getChildren().size() - 1; i >= 0; i--) {
                Component hit = findComponentAt(container.getChildren().get(i), mx, my);
                if (hit != null) {
                    if (hit instanceof Interactive || !(root instanceof Interactive)) {
                        return hit;
                    }
                }
            }
        }

        return root;
    }
}

