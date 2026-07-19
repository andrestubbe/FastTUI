package fasttui.composable;

public interface TextInput {
    StringBuilder getTextBuffer();
    int getCursorPosition();
    void setCursorPosition(int pos);
    int getSelectionStart();
    void setSelectionStart(int start);
    boolean hasSelection();
    void deleteSelection();
    void clearSelection();
    int getSelectionMin();
    int getSelectionMax();
    boolean isFocused();
    void setFocused(boolean focused);
    int getX();
    int getY();
}
