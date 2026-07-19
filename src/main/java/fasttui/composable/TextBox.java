package fasttui.composable;

import fastterminal.FastTerminalScene;
import fasttui.behaviour.Behaviour;
import fasttui.behaviour.TextBoxBehaviour;
import fasttui.component.Control;
import java.util.ArrayList;
import java.util.List;

public class TextBox extends Control implements TextInput {

    private StringBuilder text = new StringBuilder();
    private int cursorPosition = 0;
    private int selectionStart = -1;
    private boolean focused = false;
    private boolean masked = false;
    private boolean hovered = false;
    private final List<TextInput.StateChangeListener> stateChangeListeners = new ArrayList<>();
    private String placeholder = "";

    private int colorDefaultFg = 0xFFFFFF;
    private int colorDefaultBg = -2;
    private int colorHoverFg = 0xFFFFFF;
    private int colorHoverBg = 0x3F3F46;
    private int colorFocusedFg = 0xFFFFFF;
    private int colorFocusedBg = 0x27272A;
    private int colorSelectionFg = 0xFFFFFF;
    private int colorSelectionBg = 0x1D4ED8;
    private int colorCaretBg = 0xFF0000;
    private int colorCaretFg = 0xFFFFFF;
    private int colorPlaceholderFg = 0x6B7280;

    public TextBox(int x, int y, int width) {
        super(x, y, width, 1);
        this.backgroundColor = colorDefaultBg;
        this.foregroundColor = colorDefaultFg;
        this.addBehavior(new TextBoxBehaviour());
    }

    @Override
    public void render(FastTerminalScene scene) {
        if (!visible) return;

        int baseBg = backgroundColor;
        int baseFg = foregroundColor;
        if (focused) {
            baseBg = colorFocusedBg;
            baseFg = colorFocusedFg;
        } else if (hovered) {
            baseBg = colorHoverBg;
            baseFg = colorHoverFg;
        }

        final int scroll = this.getScrollOffset();
        final int selectionMin = this.getSelectionMin();
        final int selectionMax = this.getSelectionMax();
        final boolean hasSelection = this.hasSelection();

        for (int i = 0; i < width; i++) {
            int charIndex = i + scroll;
            char ch = ' ';
            int fg = baseFg;
            int bg = baseBg;

            if (text.length() == 0 && placeholder != null && charIndex < placeholder.length()) {
                ch = placeholder.charAt(charIndex);
                fg = colorPlaceholderFg;
            } else if (charIndex < text.length()) {
                ch = masked ? '*' : text.charAt(charIndex);
            }

            if (hasSelection && charIndex >= selectionMin && charIndex < selectionMax) {
                bg = colorSelectionBg;
                fg = colorSelectionFg;
            }

            boolean isCaret = focused && charIndex == cursorPosition && !hasSelection;
            if (isCaret) {
                boolean blinkOn = (System.currentTimeMillis() % 1000 < 500);
                if (blinkOn) {
                    bg = colorCaretBg;
                    fg = colorCaretFg;
                }
            }

            scene.writeCell(x + i, y, ch, fg, bg);
        }
    }

    @Override
    public boolean isHovered() {
        return hovered;
    }

    @Override
    public void setHovered(boolean hovered) {
        if (this.hovered != hovered) {
            this.hovered = hovered;
            for (TextInput.StateChangeListener listener : stateChangeListeners) {
                listener.onStateChanged(this);
            }
        }
    }

    @Override
    public int getColorDefaultFg() { return colorDefaultFg; }
    @Override
    public void setColorDefaultFg(int color) { this.colorDefaultFg = color; }
    @Override
    public int getColorDefaultBg() { return colorDefaultBg; }
    @Override
    public void setColorDefaultBg(int color) { this.colorDefaultBg = color; }
    @Override
    public int getColorHoverFg() { return colorHoverFg; }
    @Override
    public void setColorHoverFg(int color) { this.colorHoverFg = color; }
    @Override
    public int getColorHoverBg() { return colorHoverBg; }
    @Override
    public void setColorHoverBg(int color) { this.colorHoverBg = color; }
    @Override
    public int getColorFocusedFg() { return colorFocusedFg; }
    @Override
    public void setColorFocusedFg(int color) { this.colorFocusedFg = color; }
    @Override
    public int getColorFocusedBg() { return colorFocusedBg; }
    @Override
    public void setColorFocusedBg(int color) { this.colorFocusedBg = color; }
    @Override
    public int getColorSelectionFg() { return colorSelectionFg; }
    @Override
    public void setColorSelectionFg(int color) { this.colorSelectionFg = color; }
    @Override
    public int getColorSelectionBg() { return colorSelectionBg; }
    @Override
    public void setColorSelectionBg(int color) { this.colorSelectionBg = color; }
    @Override
    public int getColorCaretBg() { return colorCaretBg; }
    @Override
    public void setColorCaretBg(int color) { this.colorCaretBg = color; }
    @Override
    public int getColorCaretFg() { return colorCaretFg; }
    @Override
    public void setColorCaretFg(int color) { this.colorCaretFg = color; }

    @Override
    public String getPlaceholder() { return placeholder; }
    @Override
    public void setPlaceholder(String placeholder) { this.placeholder = placeholder != null ? placeholder : ""; }
    @Override
    public int getColorPlaceholderFg() { return colorPlaceholderFg; }
    @Override
    public void setColorPlaceholderFg(int color) { this.colorPlaceholderFg = color; }

    @Override
    public void addStateChangeListener(StateChangeListener listener) {
        if (listener != null) {
            this.stateChangeListeners.add(listener);
        }
    }

    public void clearSelection() {
        selectionStart = -1;
    }

    public void deleteSelection() {
        if (hasSelection()) {
            int min = getSelectionMin();
            int max = getSelectionMax();
            text.delete(min, max);
            cursorPosition = min;
            clearSelection();
        }
    }

    public boolean hasSelection() {
        return selectionStart != -1 && selectionStart != cursorPosition;
    }

    public int getSelectionMin() {
        if (!hasSelection()) return -1;
        return Math.min(selectionStart, cursorPosition);
    }

    public int getSelectionMax() {
        if (!hasSelection()) return -1;
        return Math.max(selectionStart, cursorPosition);
    }

    public int getScrollOffset() {
        if (cursorPosition >= width) {
            return cursorPosition - width + 1;
        }
        return 0;
    }

    public void handleKey(int vKey, char keyChar) {
        handleKey(vKey, keyChar, true);
    }

    public void handleKey(int vKey, char keyChar, boolean isPressed) {
        if (behaviors != null) {
            for (Behaviour b : behaviors) {
                if (b instanceof TextBoxBehaviour) {
                    ((TextBoxBehaviour) b).handleKey(this, vKey, keyChar, isPressed);
                }
            }
        }
    }

    public String getText() {
        return text.toString();
    }

    public void setText(String s) {
        this.text.setLength(0);
        if (s != null) {
            this.text.append(s);
        }
        this.cursorPosition = this.text.length();
        clearSelection();
    }

    @Override
    public StringBuilder getTextBuffer() {
        return text;
    }

    @Override
    public int getCursorPosition() {
        return cursorPosition;
    }

    @Override
    public void setCursorPosition(int pos) {
        this.cursorPosition = pos;
    }

    @Override
    public int getSelectionStart() {
        return selectionStart;
    }

    @Override
    public void setSelectionStart(int selectionStart) {
        this.selectionStart = selectionStart;
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    @Override
    public void setFocused(boolean focused) {
        if (this.focused != focused) {
            this.focused = focused;
            for (TextInput.StateChangeListener listener : stateChangeListeners) {
                listener.onStateChanged(this);
            }
        }
    }

    public boolean isMasked() {
        return masked;
    }

    public void setMasked(boolean masked) {
        this.masked = masked;
    }
}
