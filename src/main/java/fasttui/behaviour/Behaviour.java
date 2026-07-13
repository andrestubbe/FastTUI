package fasttui.behaviour;

import fasttui.component.Component;

public interface Behaviour {
    default void onMousePressed(Component target, int mx, int my) {}
    default void onMouseReleased(Component target, int mx, int my) {}
    default void onMouseMoved(Component target, int mx, int my) {}
    default void onMouseDragged(Component target, int mx, int my) {}
    default void onMouseEnter(Component target) {}
    default void onMouseExit(Component target) {}
    default void onKeyPressed(Component target, int vKey, char keyChar) {}
    default void onKeyReleased(Component target, int vKey, char keyChar) {}
}
