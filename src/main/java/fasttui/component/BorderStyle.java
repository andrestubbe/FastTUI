package fasttui.component;

public enum BorderStyle {
    SINGLE('─', '─', '│', '│', '┌', '┐', '└', '┘'),
    DOUBLE('═', '═', '║', '║', '╔', '╗', '╚', '╝'),
    ROUNDED('─', '─', '│', '│', '╭', '╮', '╰', '╯'),
    HALF_BLOCK('▄', '▀', '█', '█', '▄', '▄', '▀', '▀'),
    FULL_BLOCK('█', '█', '█', '█', '█', '█', '█', '█'),
    NONE(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ');

    public final char horizontalTop, horizontalBottom, verticalLeft, verticalRight, topLeft, topRight, bottomLeft, bottomRight;

    BorderStyle(char horizontalTop, char horizontalBottom, char verticalLeft, char verticalRight, char topLeft, char topRight, char bottomLeft, char bottomRight) {
        this.horizontalTop = horizontalTop;
        this.horizontalBottom = horizontalBottom;
        this.verticalLeft = verticalLeft;
        this.verticalRight = verticalRight;
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
    }
}
