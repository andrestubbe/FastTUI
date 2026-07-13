package fasttui.layout;

import fasttui.component.Component;

import java.util.List;

public interface Layout {
    void layout(int parentX, int parentY, int parentWidth, int parentHeight, List<Component> children);
}
