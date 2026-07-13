package fasttui.component;

import fastterminal.FastTerminalScene;

public class Box extends Container {

    protected BorderStyle borderStyle = BorderStyle.SINGLE;
    protected Integer borderColor = null;

    public Box(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.backgroundColor = -1; // Transparent by default
    }

    @Override
    public void render(FastTerminalScene scene) {
        if (!visible || width <= 0 || height <= 0) return;

        // Fill background
        if (backgroundColor != -1) {
            for (int r = 0; r < height; r++) {
                for (int c = 0; c < width; c++) {
                    scene.writeCell(x + c, y + r, ' ', -1, backgroundColor);
                }
            }
        }

        if (borderStyle != BorderStyle.NONE) {
            char horizontalTop = borderStyle.horizontalTop;
            char horizontalBottom = borderStyle.horizontalBottom;
            char verticalLeft = borderStyle.verticalLeft;
            char verticalRight = borderStyle.verticalRight;
            char topLeft = borderStyle.topLeft;
            char topRight = borderStyle.topRight;
            char bottomLeft = borderStyle.bottomLeft;
            char bottomRight = borderStyle.bottomRight;

            int borderBg = (borderStyle == BorderStyle.HALF_BLOCK) ? -1 : backgroundColor;
            int drawBorderColor = (borderColor != null) ? borderColor : foregroundColor;
            if (borderStyle == BorderStyle.HALF_BLOCK && backgroundColor != -1 && borderColor == null) {
                drawBorderColor = backgroundColor;
            }

            // Horizontal lines
            for (int i = 1; i < width - 1; i++) {
                scene.writeCell(x + i, y, horizontalTop, drawBorderColor, borderBg);
                scene.writeCell(x + i, y + height - 1, horizontalBottom, drawBorderColor, borderBg);
            }
            // Vertical lines
            for (int i = 1; i < height - 1; i++) {
                scene.writeCell(x, y + i, verticalLeft, drawBorderColor, borderBg);
                scene.writeCell(x + width - 1, y + i, verticalRight, drawBorderColor, borderBg);
            }
            // Corners
            scene.writeCell(x, y, topLeft, drawBorderColor, borderBg);
            scene.writeCell(x + width - 1, y, topRight, drawBorderColor, borderBg);
            scene.writeCell(x, y + height - 1, bottomLeft, drawBorderColor, borderBg);
            scene.writeCell(x + width - 1, y + height - 1, bottomRight, drawBorderColor, borderBg);
        }

        // Render children
        for (Component child : children) {
            child.render(scene);
        }
    }

    public void setBorderStyle(BorderStyle style) {
        this.borderStyle = style;
    }

    public BorderStyle getBorderStyle() {
        return borderStyle;
    }

    public void setBorderColor(int color) {
        this.borderColor = color;
    }
}
