package fasttui.composable;

import fastterminal.FastTerminalScene;
import fasttui.behaviour.ButtonBehaviour;
import fasttui.behaviour.ButtonListener;
import fasttui.behaviour.ButtonState;
import fasttui.component.ColorSet;
import fasttui.component.LeafControl;

public class Button extends LeafControl implements ButtonListener {

    private final ColorSet backgroundColorSet;
    private final ColorSet foregroundColorSet;
    private final ButtonBehaviour behaviour;
    private String text;
    private String paddedText;

    public Button(
            final int x, final int y,
            final int width, final int height,
            final String text,
            final ColorSet backgroundColorSet,
            final ColorSet foregroundColorSet,
            final Runnable action
    ) {
        super(x, y, width, height);
        this.behaviour = new ButtonBehaviour(action);
        this.behaviour.setListener(this);
        this.addBehavior(this.behaviour);
        this.setText(text);
        this.backgroundColorSet = backgroundColorSet;
        this.foregroundColorSet = foregroundColorSet;
        this.setButtonState(ButtonState.NORMAL);
    }

    public Button(
            final int x, final int y,
            final String text,
            final int paddingX,
            final ColorSet backgroundColorSet,
            final ColorSet foregroundColorSet,
            final Runnable action
    ) {
        super(x, y, (text != null ? text.length() : 0) + paddingX * 2, 1);
        this.behaviour = new ButtonBehaviour(action);
        this.behaviour.setListener(this);
        this.addBehavior(this.behaviour);
        this.setText(text);
        this.backgroundColorSet = backgroundColorSet;
        this.foregroundColorSet = foregroundColorSet;
        this.setButtonState(ButtonState.NORMAL);
    }

    private int style = fastterminal.FastStyle.NONE;

    public void setStyle(int style) {
        this.style = style;
    }

    @Override
    public void render(final FastTerminalScene scene) {
        if (!this.visible || this.width <= 0) return;
        scene.writeString(x, y, this.paddedText, this.foregroundColor, this.backgroundColor, this.style);
    }

    private String getPaddedText(String text, final int width) {
        if (text == null) text = "";
        if (text.length() >= width) {
            return text.substring(0, width);
        }
        final int totalPadding = width - text.length();
        final int leftPadding = totalPadding / 2;
        final int rightPadding = totalPadding - leftPadding;
        return " ".repeat(leftPadding) + text + " ".repeat(rightPadding);
    }

    @Override
    public void onStateChanged(final ButtonState buttonState) {
        this.setButtonState(buttonState);
    }

    public void setButtonState(final ButtonState buttonState) {
        switch (buttonState) {
            case FOCUSSED:
                this.foregroundColor = this.foregroundColorSet.focus;
                this.backgroundColor = this.backgroundColorSet.focus;
                break;
            case PRESSED:
                this.foregroundColor = this.foregroundColorSet.press;
                this.backgroundColor = this.backgroundColorSet.press;
                break;
            case HOVERED:
                this.foregroundColor = this.foregroundColorSet.hover;
                this.backgroundColor = this.backgroundColorSet.hover;
                break;
            case NORMAL:
            default:
                this.foregroundColor = this.foregroundColorSet.normal;
                this.backgroundColor = this.backgroundColorSet.normal;
                break;
        }
    }

    public void setText(final String text) {
        this.text = text;
        this.paddedText = this.getPaddedText(text, width);
    }

    @Override
    public void setWidth(final int width) {
        super.setWidth(width);
        this.paddedText = this.getPaddedText(this.text, width);
    }

}
