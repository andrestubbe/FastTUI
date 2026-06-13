package fasttui.composable;
import fasttui.component.Component;
import fasttui.component.Panel;

import fastterminal.FastTerminalScene;

public class TextBox extends Component {
    private StringBuilder text = new StringBuilder();
    private boolean focused = false;
    private int cursorPosition = 0;
    private boolean masked = false;

    public TextBox(int x, int y, int width) {
        super(x, y, width, 1);
        this.bgColor = -1;
        this.fgColor = 0xFFFFFF;
    }

    @Override
    public void render(FastTerminalScene canvas) {
        if (!visible) return;
        int currentBg = focused ? 0x27272A : bgColor;
        
        // Calculate scroll offset to keep the cursor visible within the width
        int scrollOffset = 0;
        if (cursorPosition >= width) {
            scrollOffset = cursorPosition - width + 1;
        }
        
        for (int i = 0; i < width; i++) {
            int charIdx = i + scrollOffset;
            char ch = ' ';
            if (charIdx < text.length()) {
                ch = masked ? '*' : text.charAt(charIdx);
            }
            
            int cellBg = currentBg;
            if (focused && charIdx == cursorPosition) {
                cellBg = 0xAAAAAA; // Cursor block
            }
            canvas.writeCell(x + i, y, ch, fgColor, cellBg);
        }
    }

    @Override
    protected void onPress() {
        // Assume external focus manager will unfocus others
        focused = true;
    }

    public void setFocused(boolean focused) { this.focused = focused; }
    public boolean isFocused() { return focused; }
    
    public void setMasked(boolean masked) { this.masked = masked; }
    public boolean isMasked() { return masked; }
    
    public void handleKey(int vKey, char keyChar) {
        if (!focused) return;
        
        if (vKey == 0x08) { // Backspace
            if (text.length() > 0 && cursorPosition > 0) {
                text.deleteCharAt(cursorPosition - 1);
                cursorPosition--;
            }
        } else if (vKey == 0x25) { // Left
            if (cursorPosition > 0) cursorPosition--;
        } else if (vKey == 0x27) { // Right
            if (cursorPosition < text.length()) cursorPosition++;
        } else if (keyChar == 22 || keyChar == '\u0016') { // Ctrl+V
            try {
                java.awt.datatransfer.Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
                java.awt.datatransfer.Transferable contents = clipboard.getContents(null);
                if (contents != null && contents.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.stringFlavor)) {
                    String clip = (String) contents.getTransferData(java.awt.datatransfer.DataFlavor.stringFlavor);
                    if (clip != null && !clip.isEmpty()) {
                        clip = clip.replace("\r", "").replace("\n", "");
                        int spaceLeft = 256 - text.length();
                        if (spaceLeft > 0) {
                            String toInsert = clip.substring(0, Math.min(clip.length(), spaceLeft));
                            text.insert(cursorPosition, toInsert);
                            cursorPosition += toInsert.length();
                        }
                    }
                }
            } catch (Throwable ignored) {}
        } else if (Character.isDefined(keyChar) && keyChar >= 32 && keyChar < 127) {
            if (text.length() < 256) {
                text.insert(cursorPosition, keyChar);
                cursorPosition++;
            }
        }
    }

    public String getText() { return text.toString(); }

    public void setText(String s) {
        this.text.setLength(0);
        if (s != null) {
            this.text.append(s);
        }
        this.cursorPosition = this.text.length();
    }
}
