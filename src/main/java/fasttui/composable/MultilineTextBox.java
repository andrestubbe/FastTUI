package fasttui.composable;

import fastterminal.FastTerminalScene;
import fasttui.behaviour.Behaviour;
import fasttui.behaviour.TextBoxBehaviour;
import fasttui.component.Control;
import java.util.ArrayList;
import java.util.List;

public class MultilineTextBox extends Control implements TextInput {

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

    public MultilineTextBox(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.backgroundColor = COLOR_DEFAULT_BG;
        this.foregroundColor = COLOR_DEFAULT_FG;
        this.addBehavior(new TextBoxBehaviour());
    }

    public static class LayoutResult {
        public List<String> lines = new ArrayList<>();
        public List<Integer> lineStarts = new ArrayList<>();
        public int caretRow = 0;
        public int caretCol = 0;
    }

    public LayoutResult doLayout() {
        LayoutResult result = new LayoutResult();
        String str = text.toString();
        int maxW = width;
        if (maxW <= 0) maxW = 1;

        if (str.isEmpty()) {
            result.lines.add("");
            result.lineStarts.add(0);
            result.caretRow = 0;
            result.caretCol = 0;
            return result;
        }

        int len = str.length();
        int lineStart = 0;
        int i = 0;

        while (i < len) {
            int lineEnd = lineStart + maxW;
            if (lineEnd > len) lineEnd = len;

            int newlineIdx = str.substring(lineStart, lineEnd).indexOf('\n');
            if (newlineIdx != -1) {
                lineEnd = lineStart + newlineIdx;
                result.lines.add(str.substring(lineStart, lineEnd));
                result.lineStarts.add(lineStart);
                lineStart = lineEnd + 1;
                i = lineStart;
                continue;
            }

            if (lineEnd < len) {
                int space = str.substring(lineStart, lineEnd).lastIndexOf(' ');
                if (space > 0) {
                    lineEnd = lineStart + space;
                }
            }

            result.lines.add(str.substring(lineStart, lineEnd));
            result.lineStarts.add(lineStart);

            if (lineEnd < len && str.charAt(lineEnd) == ' ') {
                lineStart = lineEnd + 1;
            } else {
                lineStart = lineEnd;
            }
            i = lineStart;
        }

        if (len > 0 && str.charAt(len - 1) == '\n') {
            result.lines.add("");
            result.lineStarts.add(len);
        }

        // Caret pos mapping
        for (int r = 0; r < result.lines.size(); r++) {
            int start = result.lineStarts.get(r);
            int end = start + result.lines.get(r).length();
            if (cursorPosition >= start && cursorPosition <= end) {
                result.caretRow = r;
                result.caretCol = cursorPosition - start;
                break;
            }
        }

        return result;
    }

    @Override
    public void render(FastTerminalScene scene) {
        if (!visible) return;
        int currentBg = focused ? COLOR_FOCUSED_BG : backgroundColor;

        LayoutResult layout = doLayout();

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

                if (c < lineText.length()) {
                    ch = masked ? '*' : lineText.charAt(c);
                }

                int cellBg = currentBg;
                int cellFg = foregroundColor;

                // Selection check
                if (lineIdx < layout.lines.size() && c < lineText.length() && hasSelection() && charIdx >= selMin && charIdx < selMax) {
                    cellBg = COLOR_SELECTION_BG;
                } else if (focused && lineIdx == layout.caretRow && c == layout.caretCol) {
                    boolean blinkOn = (System.currentTimeMillis() % 1000 < 500);
                    if (blinkOn) {
                        ch = '│';
                        cellFg = COLOR_CARET;
                    }
                }

                scene.writeCell(xPos, yPos, ch, cellFg, cellBg);
            }
        }
    }

    // Helper to get character index under mouse screen coordinate
    public int getCharIndexAtMouse(int mx, int my) {
        LayoutResult layout = doLayout();
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
        this.focused = focused;
    }

    public void setMasked(boolean masked) {
        this.masked = masked;
    }
}
