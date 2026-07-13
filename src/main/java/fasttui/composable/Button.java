package fasttui.composable;

import fastterminal.FastTerminalScene;
import fasttui.behaviour.ButtonBehaviour;
import fasttui.component.BorderStyle;
import fasttui.component.Box;
import fasttui.component.Control;
import fasttui.component.Text;

public class Button extends Control {

    public enum Alignment {LEFT, CENTER, RIGHT}

    private String text;
    private final ButtonBehaviour buttonBehaviour;
    private int backgroundNormal = -1;
    private int backgroundHover = 0xCCCCCC;
    private int backgroundActive = 0x767676;
    private int foregroundNormal = 0xCCCCCC;
    private int foregroundHover = 0x0C0C0C;
    private int foregroundActive = 0x0C0C0C;
    private Alignment alignment = Alignment.CENTER;
    private final Box box;
    private final Text label;

    public Button(int x, int y, int width, int height, String text, Runnable action) {
        super(x, y, width, height);
        this.text = text;
        this.buttonBehaviour = new ButtonBehaviour(action);
        this.addBehavior(buttonBehaviour);

        // 1. Compose background box
        this.box = new Box(0, 0, width, height);
        this.box.setBorderStyle(BorderStyle.NONE);
        this.add(box);

        // 2. Compose text label
        this.label = new Text(0, 0, width, height);
        this.label.setText(text);
        this.add(label);

        updateChildLayout();
    }

    private void updateChildLayout() {
        boolean hasBorder = (box.getBorderStyle() != BorderStyle.NONE);
        int textW = text.length();
        int textX = 0;
        int maxTextW = hasBorder ? width - 2 : width;
        if (maxTextW < 0) maxTextW = 0;

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

    @Override
    public void render(FastTerminalScene scene) {
        if (!visible) return;

        int currentBg = backgroundNormal;
        int currentFg = foregroundNormal;

        if (buttonBehaviour.isPressed()) {
            currentBg = backgroundActive;
            currentFg = foregroundActive;
        } else if (buttonBehaviour.isHovered()) {
            currentBg = backgroundHover;
            currentFg = foregroundHover;
        }

        box.setBackgroundColor(currentBg);

        label.setBackgroundColor(currentBg);
        label.setForegroundColor(currentFg);

        updateChildLayout();
        super.render(scene);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    public void setBackgroundNormal(int backgroundNormal) {
        this.backgroundNormal = backgroundNormal;
    }

    public void setBackgroundHover(int backgroundHover) {
        this.backgroundHover = backgroundHover;
    }

    public void setBackgroundActive(int backgroundActive) {
        this.backgroundActive = backgroundActive;
    }

    public void setForegroundNormal(int foregroundNormal) {
        this.foregroundNormal = foregroundNormal;
    }

    public void setForegroundHover(int foregroundHover) {
        this.foregroundHover = foregroundHover;
    }

    public void setForegroundActive(int foregroundActive) {
        this.foregroundActive = foregroundActive;
    }

    public void setBorderStyle(BorderStyle style) {
        if (box != null) {
            box.setBorderStyle(style);
            updateChildLayout();
        }
    }
}
