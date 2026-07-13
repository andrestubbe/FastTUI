package fasttui.composable;

import fastterminal.FastTerminalScene;
import fasttui.behaviour.Behaviour;
import fasttui.component.Component;
import fasttui.component.Control;

public class TextBox extends Control {
    private StringBuilder text = new StringBuilder();
    private boolean focused = false;
    private int cursorPosition = 0;
    private boolean masked = false;

    public TextBox(int x, int y, int width) {
        super(x, y, width, 1);
        this.backgroundColor = -1;
        this.foregroundColor = 0xFFFFFF;

        this.addBehavior(new Behaviour() {
            @Override
            public void onMousePressed(Component target, int mx, int my) {
                focused = true;
            }

            @Override
            public void onKeyPressed(Component target, int vKey, char keyChar) {
                handleKey(vKey, keyChar);
            }
        });
    }

    @Override
    public void render(FastTerminalScene scene) {
        if (!visible) return;
        int currentBg = focused ? 0x27272A : backgroundColor;

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
                cellBg = 0xAAAAAA;
            }
            scene.writeCell(x + i, y, ch, foregroundColor, cellBg);
        }
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public boolean isFocused() {
        return focused;
    }

    public void setMasked(boolean masked) {
        this.masked = masked;
    }

    public boolean isMasked() {
        return masked;
    }

    public void handleKey(int vKey, char keyChar) {
        if (!focused) return;

        if (vKey == 0x08) {
            if (text.length() > 0 && cursorPosition > 0) {
                text.deleteCharAt(cursorPosition - 1);
                cursorPosition--;
            }
        } else if (vKey == 0x25) {
            if (cursorPosition > 0) cursorPosition--;
        } else if (vKey == 0x27) {
            if (cursorPosition < text.length()) cursorPosition++;
        } else if (keyChar == 22 || keyChar == '\u0016') {
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
            } catch (Throwable ignored) {
            }
        } else if (Character.isDefined(keyChar) && keyChar >= 32 && keyChar < 127) {
            if (text.length() < 256) {
                text.insert(cursorPosition, keyChar);
                cursorPosition++;
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
    }
}
