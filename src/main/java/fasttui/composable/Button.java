package fasttui.composable;

import fastterminal.FastTerminalScene;
import fasttui.behaviour.ButtonBehaviour;
import fasttui.behaviour.ButtonBehaviour.State;
import fasttui.component.BorderStyle;
import fasttui.component.Box;
import fasttui.component.Control;
import fasttui.component.TextArea;

public class Button extends Control {

    public enum Alignment {LEFT, CENTER, RIGHT}

    private String text;
    private final ButtonBehaviour behaviour;

    private int backgroundNormal = -1;
    private int backgroundHover = 0xCCCCCC;
    private int backgroundPressed = 0x767676;

    private int foregroundNormal = 0xCCCCCC;
    private int foregroundHover = 0x0C0C0C;
    private int foregroundPressed = 0x0C0C0C;

    private Alignment alignment = Alignment.CENTER;

    private final Box box;
    private final TextArea label;

    public Button(int x, int y, int width, int height, String text, Runnable action) {
        super(x, y, width, height);
        this.text = text;

        this.behaviour = new ButtonBehaviour(action);
        this.addBehavior(this.behaviour);

        this.box = new Box(0, 0, width, height);
        this.box.setBorderStyle(BorderStyle.NONE);
        this.add(box);

        this.label = new TextArea(0, 0, width, height);
        this.label.setText(text);
        this.add(label);

        updateChildLayout();
    }

    @Override
    public void render(FastTerminalScene scene) {
        if (!visible) return;

        State state = behaviour.getState();

        int bg;
        int fg;

        switch (state) {
            case PRESSED:
                bg = backgroundPressed;
                fg = foregroundPressed;
                break;
            case HOVERED:
                bg = backgroundHover;
                fg = foregroundHover;
                break;
            case NORMAL:
            default:
                bg = backgroundNormal;
                fg = foregroundNormal;
                break;
        }

        box.setBackgroundColor(bg);
        label.setBackgroundColor(bg);
        label.setForegroundColor(fg);

        updateChildLayout();
        super.render(scene);
    }

    private void updateChildLayout() {
        boolean hasBorder = (box.getBorderStyle() != BorderStyle.NONE);
        int textW = text.length();
        int maxTextW = hasBorder ? width - 2 : width;
        if (maxTextW < 0) maxTextW = 0;

        int textX = 0;
        if (alignment == Alignment.CENTER) {
            textX = (width - textW) / 2;
        } else if (alignment == Alignment.RIGHT) {
            textX = width - textW;
        }

        if (hasBorder) {
            if (textX < 1) textX = 1;
        } else {
            if (textX < 0) textX = 0;
        }

        int textY = height / 2;

        label.setX(this.x + textX);
        label.setY(this.y + textY);
        label.setWidth(Math.min(textW, maxTextW));
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    public void setBackgroundNormal(int c) {
        backgroundNormal = c;
    }

    public void setBackgroundHover(int c) {
        backgroundHover = c;
    }

    public void setBackgroundPressed(int c) {
        backgroundPressed = c;
    }

    public void setForegroundNormal(int c) {
        foregroundNormal = c;
    }

    public void setForegroundHover(int c) {
        foregroundHover = c;
    }

    public void setForegroundPressed(int c) {
        foregroundPressed = c;
    }

    public void setBorderStyle(BorderStyle style) {
        box.setBorderStyle(style);
        updateChildLayout();
    }
}
