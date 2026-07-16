package fasttui.layout;

import fasttui.component.Component;

import java.util.List;

public class LinearLayout implements Layout {
    public enum Direction {HORIZONTAL, VERTICAL}

    public enum Alignment {LEFT, RIGHT, CENTER}

    private Direction direction;
    private int spacing;
    private Alignment alignment;

    public LinearLayout(Direction direction, Alignment alignment, int spacing) {
        this.direction = direction;
        this.alignment = alignment;
        this.spacing = spacing;
    }

    @Override
    public void layout(int parentX, int parentY, int parentWidth, int parentHeight, List<Component> children) {
        int currentX = parentX;
        int currentY = parentY;

        if (direction == Direction.HORIZONTAL) {

            int totalWidth = 0;
            for (Component child : children) {
                totalWidth += child.getWidth();
            }
            totalWidth += spacing * Math.max(0, children.size() - 1);

            switch (alignment) {
                case RIGHT:
                    // parentX ist die rechte Grenze → wir schieben nach links
                    currentX = parentX - totalWidth;
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

        } else {
            for (Component child : children) {
                child.setX(currentX);
                child.setY(currentY);
                currentY += child.getHeight() + spacing;
            }
        }
    }

    public void layout(int parentX, int parentY, int parentWidth, int parentHeight, List<Component> children, Alignment alignment) {
        this.alignment = alignment;
        this.layout(parentX, parentY, parentWidth, parentHeight, children);
    }


    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }
}
