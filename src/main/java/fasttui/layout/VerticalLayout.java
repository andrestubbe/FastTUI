package fasttui.layout;

import fasttui.component.Component;
import java.util.List;

public class VerticalLayout implements Layout {

    private int spacing;

    public VerticalLayout(int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void layout(int parentX, int parentY, int parentWidth, int parentHeight, List<Component> children) {
        int currentX = parentX;
        int currentY = parentY;

        for (Component child : children) {
            child.setX(currentX);
            child.setY(currentY);
            currentY += child.getHeight() + spacing;
        }
    }

    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }
}
