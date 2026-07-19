package fasttui.behaviour;

import fastkeyboard.Keys;
import fasttui.component.Component;
import fasttui.composable.TextBox;
import fasttui.composable.MultilineTextBox;
import fasttui.composable.TextInput;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;

public class TextBoxBehaviour implements Behaviour {

    private boolean shiftPressed = false;
    private boolean ctrlPressed = false;

    private int getCharIndexAt(TextInput target, int mx, int my) {
        if (target instanceof MultilineTextBox) {
            return ((MultilineTextBox) target).getCharIndexAtMouse(mx, my);
        } else if (target instanceof TextBox) {
            TextBox box = (TextBox) target;
            int relX = mx - box.getX();
            int scrollOffset = box.getScrollOffset();
            int charIdx = relX + scrollOffset;
            return Math.max(0, Math.min(charIdx, box.getTextBuffer().length()));
        }
        return 0;
    }

    @Override
    public void onMousePressed(Component target, int mx, int my) {
        if (target instanceof TextInput) {
            TextInput box = (TextInput) target;
            box.setFocused(true);
            int charIdx = getCharIndexAt(box, mx, my);
            box.setCursorPosition(charIdx);
            box.clearSelection();
            box.setSelectionStart(charIdx);
        }
    }

    @Override
    public void onMouseDragged(Component target, int mx, int my) {
        if (target instanceof TextInput) {
            TextInput box = (TextInput) target;
            int charIdx = getCharIndexAt(box, mx, my);
            box.setCursorPosition(charIdx);
        }
    }

    @Override
    public void onMouseReleased(Component target, int mx, int my) {
        if (target instanceof TextInput) {
            TextInput box = (TextInput) target;
            if (!box.hasSelection()) {
                box.clearSelection();
            }
        }
    }

    @Override
    public void onKeyPressed(Component target, int vKey, char keyChar) {
        if (target instanceof TextInput) {
            handleKey((TextInput) target, vKey, keyChar, true);
        }
    }

    @Override
    public void onKeyReleased(Component target, int vKey, char keyChar) {
        if (target instanceof TextInput) {
            handleKey((TextInput) target, vKey, keyChar, false);
        }
    }

    private void updateModifierKeys(int vKey, boolean isPressed) {
        if (vKey == Keys.SHIFT || vKey == Keys.LSHIFT || vKey == Keys.RSHIFT) {
            shiftPressed = isPressed;
        } else if (vKey == Keys.CTRL || vKey == Keys.CONTROL) {
            ctrlPressed = isPressed;
        }
    }

    private boolean isSeparator(char c) {
        return c == ' ' || c == '\\' || c == '/';
    }

    public void handleKey(TextInput box, int vKey, char keyChar, boolean isPressed) {
        // Modifier keys first
        if (vKey == Keys.SHIFT || vKey == Keys.LSHIFT || vKey == Keys.RSHIFT ||
            vKey == Keys.CTRL || vKey == Keys.CONTROL) {
            updateModifierKeys(vKey, isPressed);
            return;
        }

        // Only handle key presses, not releases
        if (!isPressed || !box.isFocused()) return;

        // Multiline vertical navigation
        if (handleUpDownArrows(box, vKey)) return;

        // High-level dispatch
        if (handleSelectionShortcuts(box, vKey, keyChar)) return;
        if (handleDeletion(box, vKey)) return;
        if (handleCursorMovement(box, vKey)) return;
        if (handleClipboard(box, vKey, keyChar)) return;
        handleCharacterInput(box, keyChar);
    }

    // -----------------------------
    // 0) Vertical Navigation (Up/Down) for Multiline
    // -----------------------------
    private boolean handleUpDownArrows(TextInput box, int vKey) {
        if (vKey != Keys.UP && vKey != Keys.DOWN) return false;

        if (box instanceof MultilineTextBox) {
            MultilineTextBox mbox = (MultilineTextBox) box;
            MultilineTextBox.LayoutResult layout = mbox.doLayout();
            int cursor = mbox.getCursorPosition();
            boolean up = vKey == Keys.UP;

            if (up && layout.caretRow > 0) {
                if (shiftPressed && mbox.getSelectionStart() == -1) {
                    mbox.setSelectionStart(cursor);
                } else if (!shiftPressed) {
                    mbox.clearSelection();
                }
                int newRow = layout.caretRow - 1;
                int newCol = Math.min(layout.caretCol, layout.lines.get(newRow).length());
                int newPos = layout.lineStarts.get(newRow) + newCol;
                mbox.setCursorPosition(newPos);
            } else if (!up && layout.caretRow < layout.lines.size() - 1) {
                if (shiftPressed && mbox.getSelectionStart() == -1) {
                    mbox.setSelectionStart(cursor);
                } else if (!shiftPressed) {
                    mbox.clearSelection();
                }
                int newRow = layout.caretRow + 1;
                int newCol = Math.min(layout.caretCol, layout.lines.get(newRow).length());
                int newPos = layout.lineStarts.get(newRow) + newCol;
                mbox.setCursorPosition(newPos);
            }
            return true;
        }
        return false;
    }

    // -----------------------------
    // 1) Selection shortcuts
    // -----------------------------
    private boolean handleSelectionShortcuts(TextInput box, int vKey, char keyChar) {
        boolean ctrlA = ctrlPressed && (vKey == Keys.A || keyChar == 'a' || keyChar == 'A');
        boolean rawCtrlA = keyChar == '\u0001';

        if (ctrlA || rawCtrlA) {
            box.setSelectionStart(0);
            box.setCursorPosition(box.getTextBuffer().length());
            return true;
        }
        return false;
    }

    // -----------------------------
    // 2) Deletion (Backspace/Delete)
    // -----------------------------
    private boolean handleDeletion(TextInput box, int vKey) {
        StringBuilder text = box.getTextBuffer();
        int cursor = box.getCursorPosition();

        if (vKey == Keys.BACKSPACE || vKey == Keys.BACK) {
            if (box.hasSelection()) {
                box.deleteSelection();
            } else if (cursor > 0) {
                text.deleteCharAt(cursor - 1);
                box.setCursorPosition(cursor - 1);
            }
            return true;
        }

        if (vKey == Keys.DELETE) {
            if (box.hasSelection()) {
                box.deleteSelection();
            } else if (cursor < text.length()) {
                text.deleteCharAt(cursor);
            }
            return true;
        }

        return false;
    }

    // -----------------------------
    // 3) Cursor movement (Left/Right)
    // -----------------------------
    private boolean handleCursorMovement(TextInput box, int vKey) {
        if (vKey != Keys.LEFT && vKey != Keys.RIGHT) return false;

        StringBuilder text = box.getTextBuffer();
        int cursor = box.getCursorPosition();
        boolean left = vKey == Keys.LEFT;

        if (ctrlPressed) {
            handleCtrlWordJump(box, text, cursor, left);
        } else {
            handleSimpleArrow(box, cursor, left);
        }

        return true;
    }

    private void handleCtrlWordJump(TextInput box, StringBuilder text, int cursor, boolean left) {
        int pos = cursor;
        int len = text.length();

        if (shiftPressed && box.getSelectionStart() == -1) {
            box.setSelectionStart(cursor);
        } else if (!shiftPressed) {
            box.clearSelection();
        }

        if (left) {
            while (pos > 0 && isSeparator(text.charAt(pos - 1))) pos--;
            while (pos > 0 && !isSeparator(text.charAt(pos - 1))) pos--;
        } else {
            while (pos < len && !isSeparator(text.charAt(pos))) pos++;
            while (pos < len && isSeparator(text.charAt(pos))) pos++;
        }

        box.setCursorPosition(pos);
    }

    private void handleSimpleArrow(TextInput box, int cursor, boolean left) {
        int newPos = left ? cursor - 1 : cursor + 1;

        if (shiftPressed) {
            if (box.getSelectionStart() == -1) {
                box.setSelectionStart(cursor);
            }
        } else {
            box.clearSelection();
        }

        if (newPos >= 0 && newPos <= box.getTextBuffer().length()) {
            box.setCursorPosition(newPos);
        }
    }

    // -----------------------------
    // 4) Clipboard (Copy, Cut, Paste)
    // -----------------------------
    private boolean handleClipboard(TextInput box, int vKey, char keyChar) {
        boolean ctrlC = ctrlPressed && (vKey == Keys.C || keyChar == 'c' || keyChar == 'C') || keyChar == '\u0003';
        boolean ctrlX = ctrlPressed && (vKey == Keys.X || keyChar == 'x' || keyChar == 'X') || keyChar == '\u0018';
        boolean ctrlV = ctrlPressed && (vKey == Keys.V || keyChar == 'v' || keyChar == 'V') || keyChar == 22 || keyChar == '\u0016';

        StringBuilder text = box.getTextBuffer();

        if (ctrlC) {
            if (box.hasSelection()) {
                String selected = text.substring(box.getSelectionMin(), box.getSelectionMax());
                setClipboardText(selected);
            }
            return true;
        }

        if (ctrlX) {
            if (box.hasSelection()) {
                String selected = text.substring(box.getSelectionMin(), box.getSelectionMax());
                setClipboardText(selected);
                box.deleteSelection();
            }
            return true;
        }

        if (ctrlV) {
            String clip = getClipboardText();
            if (clip != null && !clip.isEmpty()) {
                insertText(box, clip);
            }
            return true;
        }

        return false;
    }

    private void setClipboardText(String data) {
        try {
            Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
            java.awt.datatransfer.StringSelection selection = new java.awt.datatransfer.StringSelection(data);
            clipboard.setContents(selection, selection);
        } catch (Throwable ignored) {}
    }

    private String getClipboardText() {
        try {
            Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable contents = clipboard.getContents(null);
            if (contents != null && contents.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.stringFlavor)) {
                String clip = (String) contents.getTransferData(java.awt.datatransfer.DataFlavor.stringFlavor);
                if (clip != null) {
                    return clip.replace("\r", "");
                }
            }
        } catch (Throwable ignored) {}
        return null;
    }

    private void insertText(TextInput box, String clip) {
        StringBuilder text = box.getTextBuffer();
        int cursor = box.getCursorPosition();

        if (box.hasSelection()) {
            box.deleteSelection();
            cursor = box.getCursorPosition();
        }

        int spaceLeft = 256 - text.length();
        if (spaceLeft <= 0) return;

        String toInsert = clip.substring(0, Math.min(clip.length(), spaceLeft));
        text.insert(cursor, toInsert);
        box.setCursorPosition(cursor + toInsert.length());
    }

    // -----------------------------
    // 5) Character input
    // -----------------------------
    private void handleCharacterInput(TextInput box, char keyChar) {
        if (!Character.isDefined(keyChar) || (keyChar < 32 && keyChar != '\n') || keyChar >= 127) return;

        StringBuilder text = box.getTextBuffer();
        int cursor = box.getCursorPosition();

        if (box.hasSelection()) {
            box.deleteSelection();
            cursor = box.getCursorPosition();
        }

        if (text.length() < 256) {
            text.insert(cursor, keyChar);
            box.setCursorPosition(cursor + 1);
        }
    }
}
