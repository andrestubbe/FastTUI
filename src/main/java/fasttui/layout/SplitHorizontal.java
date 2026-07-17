package fasttui.layout;

import fasttui.behaviour.DividerBehaviour;
import fasttui.component.Container;
import fasttui.component.DividerVertical;

public class SplitHorizontal {

    public enum PaneMode {
        LEFT_ONLY,
        RIGHT_ONLY,
        SPLIT
    }

    private final Container left;
    private final Container right;
    private final DividerVertical divider;
    private final DividerBehaviour dividerBehaviour;

    private double ratio = 0.5;
    private int minPanelSize = 5;
    private int dividerWidth = 1;
    private PaneMode paneMode = PaneMode.SPLIT;

    private int parentX;
    private int parentY;
    private int parentWidth;
    private int parentHeight;

    public SplitHorizontal(Container left, Container right) {
        this.left = left;
        this.right = right;
        this.divider = new DividerVertical(0, 0, dividerWidth, 1);
        this.dividerBehaviour = new DividerBehaviour(this);
        this.divider.addBehavior(dividerBehaviour);
    }

    public Container getLeft() {
        return left;
    }

    public Container getRight() {
        return right;
    }

    public DividerVertical getDivider() {
        return divider;
    }

    public DividerBehaviour getDividerBehaviour() {
        return dividerBehaviour;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = Math.max(0.05, Math.min(0.95, ratio));
    }

    public int getMinPanelSize() {
        return minPanelSize;
    }

    public void setMinPanelSize(int minPanelSize) {
        this.minPanelSize = Math.max(1, minPanelSize);
    }

    public PaneMode getPaneMode() {
        return paneMode;
    }

    public void setPaneMode(PaneMode paneMode) {
        this.paneMode = paneMode;
        relayout();
    }

    public boolean isDragging() {
        return dividerBehaviour.isDragging();
    }

    public void layout(int parentX, int parentY, int parentWidth, int parentHeight) {
        this.parentX = parentX;
        this.parentY = parentY;
        this.parentWidth = parentWidth;
        this.parentHeight = parentHeight;
        relayout();
    }

    public void setDividerPosition(int absoluteX) {
        if (parentWidth <= 0 || paneMode != PaneMode.SPLIT) return;
        int leftW = Math.max(minPanelSize, Math.min(absoluteX - parentX, parentWidth - dividerWidth - minPanelSize));
        int track = parentWidth - dividerWidth;
        if (track > 0) ratio = (double) leftW / track;
        applySplitLayout(leftW);
    }

    private void relayout() {
        if (parentWidth <= 0) return;
        switch (paneMode) {
            case LEFT_ONLY -> applySinglePane(true);
            case RIGHT_ONLY -> applySinglePane(false);
            case SPLIT -> applySplitLayout((int) ((parentWidth - dividerWidth) * ratio));
        }
    }

    private void applySinglePane(boolean leftPane) {
        left.setVisible(leftPane);
        divider.setVisible(false);
        right.setVisible(!leftPane);

        if (leftPane) {
            left.setX(parentX);
            left.setY(parentY);
            left.setWidth(parentWidth);
            left.setHeight(parentHeight);

            divider.setWidth(0);
            right.setWidth(0);
        } else {
            right.setX(parentX);
            right.setY(parentY);
            right.setWidth(parentWidth);
            right.setHeight(parentHeight);

            left.setWidth(0);
            divider.setWidth(0);
        }
    }

    private void applySplitLayout(int leftW) {
        left.setVisible(true);
        divider.setVisible(true);
        right.setVisible(true);

        left.setX(parentX);
        left.setY(parentY);
        left.setWidth(leftW);
        left.setHeight(parentHeight);

        divider.setX(parentX + leftW);
        divider.setY(parentY);
        divider.setWidth(dividerWidth);
        divider.setHeight(parentHeight);

        right.setX(parentX + leftW + dividerWidth);
        right.setY(parentY);
        right.setWidth(parentWidth - leftW - dividerWidth);
        right.setHeight(parentHeight);
    }
}
