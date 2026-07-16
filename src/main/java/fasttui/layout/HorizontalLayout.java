package fasttui.layout;

import fasttui.component.Component;
import java.util.List;

public class HorizontalLayout implements Layout {

    public enum Alignment {LEFT, RIGHT, CENTER}

    private int spacing;
    private Alignment alignment;

    public HorizontalLayout(Alignment alignment, int spacing) {
        this.alignment = alignment;
        this.spacing = spacing;
    }

    @Override
    public void layout(int parentX, int parentY, int parentWidth, int parentHeight, List<Component> children) {
        int currentX = parentX;
        int currentY = parentY;

        int totalWidth = 0;
        for (Component child : children) {
            totalWidth += child.getWidth();
        }
        totalWidth += spacing * Math.max(0, children.size() - 1);

        switch (alignment) {
            case RIGHT:
                currentX = parentX + parentWidth - totalWidth;
                break;

            case CENTER:
                currentX = parentX + (parentWidth - totalWidth) / 2;
                break;

            case LEFT:
            default:
                currentX = parentX;
        }

        for (Component child : children) {
            child.setX(currentX);
            child.setY(currentY);
            currentX += child.getWidth() + spacing;
        }
    }

    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }
}
