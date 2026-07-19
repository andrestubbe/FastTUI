package fasttui.composable;

import fastterminal.FastTerminalScene;
import fasttui.behaviour.Behaviour;
import fasttui.behaviour.TextBoxBehaviour;
import fasttui.component.Control;

public class TextBox extends Control implements TextInput {

    public static final int COLOR_DEFAULT_FG = 0xFFFFFF;
    public static final int COLOR_DEFAULT_BG = -2;
    public static final int COLOR_FOCUSED_BG = 0x27272A;
    public static final int COLOR_SELECTION_BG = 0x1D4ED8;
    public static final int COLOR_CARET = 0xFF0000;

    private StringBuilder text = new StringBuilder();
    private int cursorPosition = 0;
    private int selectionStart = -1;
    private boolean focused = false;
    private boolean masked = false;

    public TextBox(int x, int y, int width) {
        super(x, y, width, 1);
        this.backgroundColor = COLOR_DEFAULT_BG;
        this.foregroundColor = COLOR_DEFAULT_FG;
        this.addBehavior(new TextBoxBehaviour());
    }

    @Override
    public void render(FastTerminalScene scene) {
        if (!visible) return;

        // Base background depends on focus state
        final int baseBg = focused ? COLOR_FOCUSED_BG : backgroundColor;

        // Horizontal scroll offset so the cursor stays visible
        final int scroll = this.getScrollOffset();

        // Selection boundaries (if any)
        final int selectionMin = this.getSelectionMin();
        final int selectionMax = this.getSelectionMax();
        final boolean hasSelection = this.hasSelection();

        for (int i = 0; i < width; i++) {

            // --- 1) Determine character to display ---
            int charIndex = i + scroll;

            // Default to space if outside text range
            char ch = (charIndex < text.length())
                    ? (masked ? '*' : text.charAt(charIndex))
                    : ' ';

            // --- 2) Determine foreground/background colors ---
            int fg = foregroundColor;
            int bg = baseBg;

            // Highlight selected text range
            if (hasSelection && charIndex >= selectionMin && charIndex < selectionMax) {
                bg = COLOR_SELECTION_BG;
            }

            // Caret rendering (only when focused and no selection)
            boolean isCaret = focused && charIndex == cursorPosition && !hasSelection;
            if (isCaret) {
                // Simple blinking effect: visible for 500ms, invisible for 500ms
                boolean blinkOn = (System.currentTimeMillis() % 1000 < 500);
                if (blinkOn) {
                    ch = '│';      // Unicode thin vertical bar
                    fg = COLOR_CARET;
                }
            }

            // --- 3) Write final cell to the terminal scene ---
            scene.writeCell(x + i, y, ch, fg, bg);
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

    public boolean isFocused() {
        return focused;
    }

    public boolean isMasked() {
        return masked;
    }

    public boolean hasSelection() {
        return selectionStart != -1 && selectionStart != cursorPosition;
    }

    public int getScrollOffset() {
        if (cursorPosition >= width) {
            return cursorPosition - width + 1;
        }
        return 0;
    }

    public int getSelectionMin() {
        if (!hasSelection()) return -1;
        return Math.min(selectionStart, cursorPosition);
    }

    public int getSelectionMax() {
        if (!hasSelection()) return -1;
        return Math.max(selectionStart, cursorPosition);
    }

    public StringBuilder getTextBuffer() {
        return text;
    }

    public int getCursorPosition() {
        return cursorPosition;
    }

    public int getSelectionStart() {
        return selectionStart;
    }

    public String getText() {
        return text.toString();
    }

    public void setCursorPosition(int pos) {
        this.cursorPosition = pos;
    }

    public void setSelectionStart(int selectionStart) {
        this.selectionStart = selectionStart;
    }

    public void setText(String s) {
        this.text.setLength(0);
        if (s != null) {
            this.text.append(s);
        }
        this.cursorPosition = this.text.length();
        clearSelection();
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public void setMasked(boolean masked) {
        this.masked = masked;
    }
}
