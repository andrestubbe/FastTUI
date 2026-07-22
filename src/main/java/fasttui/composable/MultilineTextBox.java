package fasttui.composable;

import fastterminal.FastTerminalScene;
import fasttui.behaviour.Behaviour;
import fasttui.behaviour.TextBoxBehaviour;
import fasttui.component.Control;
import fasttui.layout.MultilineLayoutEngine;
import java.util.ArrayList;
import java.util.List;

public class MultilineTextBox extends Control implements TextInput {

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
    private int colorPlaceholderHoverFg = -1;
    private int colorPlaceholderFocusedFg = -1;
    private int colorPlaceholderBg = -1;
    private int colorPlaceholderHoverBg = -1;
    private int colorPlaceholderFocusedBg = -1;

    public MultilineTextBox(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.backgroundColor = colorDefaultBg;
        this.foregroundColor = colorDefaultFg;
        this.addBehavior(new TextBoxBehaviour());
    }

    public MultilineLayoutEngine.LayoutResult doLayout() {
        return MultilineLayoutEngine.layout(text.toString(), width, cursorPosition);
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

        MultilineLayoutEngine.LayoutResult layout = doLayout();

        int scrollOffset = 0;
        if (layout.caretRow >= height) {
            scrollOffset = layout.caretRow - height + 1;
        }

        int selMin = getSelectionMin();
        int selMax = getSelectionMax();

        for (int r = 0; r < height; r++) {
            int lineIdx = r + scrollOffset;
            int yPos = y + r;

            String lineText = "";
            int lineStartIdx = 0;
            if (lineIdx < layout.lines.size()) {
                lineText = layout.lines.get(lineIdx);
                lineStartIdx = layout.lineStarts.get(lineIdx);
            }

            for (int c = 0; c < width; c++) {
                int xPos = x + c;
                char ch = ' ';
                int charIdx = lineStartIdx + c;

                if (text.length() == 0 && placeholder != null && charIdx < placeholder.length()) {
                    ch = placeholder.charAt(charIdx);
                } else if (c < lineText.length()) {
                    ch = masked ? '*' : lineText.charAt(c);
                }

                int cellBg = baseBg;
                int cellFg = baseFg;

                if (text.length() == 0 && placeholder != null && charIdx < placeholder.length()) {
                    if (focused) {
                        cellFg = (colorPlaceholderFocusedFg != -1) ? colorPlaceholderFocusedFg : colorPlaceholderFg;
                        if (colorPlaceholderFocusedBg != -1) cellBg = colorPlaceholderFocusedBg;
                    } else if (hovered) {
                        cellFg = (colorPlaceholderHoverFg != -1) ? colorPlaceholderHoverFg : colorPlaceholderFg;
                        if (colorPlaceholderHoverBg != -1) cellBg = colorPlaceholderHoverBg;
                    } else {
                        cellFg = colorPlaceholderFg;
                        if (colorPlaceholderBg != -1) cellBg = colorPlaceholderBg;
                    }
                }

                // Selection check
                if (lineIdx < layout.lines.size() && c < lineText.length() && hasSelection() && charIdx >= selMin && charIdx < selMax) {
                    cellBg = colorSelectionBg;
                    cellFg = colorSelectionFg;
                } else if (focused && lineIdx == layout.caretRow && c == layout.caretCol) {
                    boolean blinkOn = (System.currentTimeMillis() % 1000 < 500);
                    if (blinkOn) {
                        cellBg = colorCaretBg;
                        cellFg = colorCaretFg;
                    }
                }

                scene.writeCell(xPos, yPos, ch, cellFg, cellBg);
            }
        }
    }

    // Helper to get character index under mouse screen coordinate
    public int getCharIndexAtMouse(int mx, int my) {
        MultilineLayoutEngine.LayoutResult layout = doLayout();
        int caretRow = layout.caretRow;
        int scrollOffset = 0;
        if (caretRow >= height) {
            scrollOffset = caretRow - height + 1;
        }

        int lineIdx = (my - y) + scrollOffset;
        int relX = mx - x;

        if (lineIdx < 0) lineIdx = 0;
        if (lineIdx >= layout.lines.size()) lineIdx = layout.lines.size() - 1;

        String lineText = layout.lines.get(lineIdx);
        int lineStartIdx = layout.lineStarts.get(lineIdx);

        int col = Math.max(0, Math.min(relX, lineText.length()));
        return lineStartIdx + col;
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

    public void setForegroundSet(fasttui.component.ColorSet colorSet) {
        if (colorSet != null) {
            this.colorDefaultFg = colorSet.normal;
            this.colorHoverFg = colorSet.hover;
            this.colorFocusedFg = colorSet.focus;
            this.foregroundColor = colorSet.normal;
        }
    }

    public void setBackgroundSet(fasttui.component.ColorSet colorSet) {
        if (colorSet != null) {
            this.colorDefaultBg = colorSet.normal;
            this.colorHoverBg = colorSet.hover;
            this.colorFocusedBg = colorSet.focus;
            this.backgroundColor = colorSet.normal;
        }
    }
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

    public int getColorPlaceholderHoverFg() { return colorPlaceholderHoverFg; }
    public void setColorPlaceholderHoverFg(int color) { this.colorPlaceholderHoverFg = color; }

    public int getColorPlaceholderFocusedFg() { return colorPlaceholderFocusedFg; }
    public void setColorPlaceholderFocusedFg(int color) { this.colorPlaceholderFocusedFg = color; }

    public int getColorPlaceholderBg() { return colorPlaceholderBg; }
    public void setColorPlaceholderBg(int color) { this.colorPlaceholderBg = color; }

    public int getColorPlaceholderHoverBg() { return colorPlaceholderHoverBg; }
    public void setColorPlaceholderHoverBg(int color) { this.colorPlaceholderHoverBg = color; }

    public int getColorPlaceholderFocusedBg() { return colorPlaceholderFocusedBg; }
    public void setColorPlaceholderFocusedBg(int color) { this.colorPlaceholderFocusedBg = color; }

    public void setPlaceholderForegroundSet(fasttui.component.ColorSet colorSet) {
        if (colorSet != null) {
            this.colorPlaceholderFg = colorSet.normal;
            this.colorPlaceholderHoverFg = colorSet.hover;
            this.colorPlaceholderFocusedFg = colorSet.focus;
        }
    }

    public void setPlaceholderBackgroundSet(fasttui.component.ColorSet colorSet) {
        if (colorSet != null) {
            this.colorPlaceholderBg = colorSet.normal;
            this.colorPlaceholderHoverBg = colorSet.hover;
            this.colorPlaceholderFocusedBg = colorSet.focus;
        }
    }

    @Override
    public void addStateChangeListener(StateChangeListener listener) {
        if (listener != null) {
            this.stateChangeListeners.add(listener);
        }
    }

    @Override
    public boolean hasSelection() {
        return selectionStart != -1 && selectionStart != cursorPosition;
    }

    @Override
    public int getSelectionMin() {
        if (!hasSelection()) return -1;
        return Math.min(selectionStart, cursorPosition);
    }

    @Override
    public int getSelectionMax() {
        if (!hasSelection()) return -1;
        return Math.max(selectionStart, cursorPosition);
    }

    @Override
    public void clearSelection() {
        selectionStart = -1;
    }

    @Override
    public void deleteSelection() {
        if (hasSelection()) {
            int min = getSelectionMin();
            int max = getSelectionMax();
            text.delete(min, max);
            cursorPosition = min;
            clearSelection();
        }
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

    @Override
    public boolean isFocused() {
        return focused;
    }

    public boolean isMasked() {
        return masked;
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
    public void setFocused(boolean focused) {
        if (this.focused != focused) {
            this.focused = focused;
            for (TextInput.StateChangeListener listener : stateChangeListeners) {
                listener.onStateChanged(this);
            }
        }
    }

    public void setMasked(boolean masked) {
        this.masked = masked;
    }
}
