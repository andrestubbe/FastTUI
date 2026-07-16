package fasttui.layout;

import fasttui.component.Container;
import fasttui.component.DividerVertical;

public class SplitHorizontal {

    private final Container left;
    private final Container right;
    private final DividerVertical divider;

    public SplitHorizontal(Container left, Container right) {
        this.left = left;
        this.right = right;
        this.divider = new DividerVertical(0, 0, 1, 100);
    }
}
