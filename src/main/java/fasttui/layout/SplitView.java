package fasttui.layout;

import fasttui.behaviour.EventDispatcher;
import fasttui.component.Container;

public class SplitView extends Container {

    private SplitHorizontal split;

    public SplitView() {
        super(0, 0, 0, 0);
    }

    public SplitView(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public void addSplit(SplitHorizontal splitHorizontal) {
        this.split = splitHorizontal;
        children.clear();
        if (split == null) return;

        split.getLeft().setX(0);
        split.getLeft().setY(0);
        split.getDivider().setX(0);
        split.getDivider().setY(0);
        split.getRight().setX(0);
        split.getRight().setY(0);

        add(split.getLeft());
        add(split.getDivider());
        add(split.getRight());
        layoutSplit();
    }

    public SplitHorizontal getSplit() {
        return split;
    }

    public boolean isDividerDragging() {
        return split != null && split.isDragging();
    }

    public void layoutSplit() {
        if (split == null) return;
        split.layout(x, y, width, height);
    }

    public void dispatchMouseMove(int mx, int my, boolean mouseDown) {
        if (split != null && split.isDragging()) {
            split.setDividerPosition(mx);
        }
        if (mouseDown || isDividerDragging()) {
            EventDispatcher.dispatchMouseDrag(this, mx, my);
        } else {
            EventDispatcher.dispatchMouseMove(this, mx, my);
        }
    }

    public void dispatchMouseClick(int mx, int my, boolean isPressed) {
        EventDispatcher.dispatchMouseClick(this, mx, my, isPressed);
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        layoutSplit();
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        layoutSplit();
    }

    @Override
    public void setX(int newX) {
        super.setX(newX);
        layoutSplit();
    }

    @Override
    public void setY(int newY) {
        super.setY(newY);
        layoutSplit();
    }
}
