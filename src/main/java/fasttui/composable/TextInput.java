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
    boolean isHovered();
    void setHovered(boolean hovered);

    int getColorDefaultFg();
    void setColorDefaultFg(int color);
    int getColorDefaultBg();
    void setColorDefaultBg(int color);
    int getColorHoverFg();
    void setColorHoverFg(int color);
    int getColorHoverBg();
    void setColorHoverBg(int color);
    int getColorFocusedFg();
    void setColorFocusedFg(int color);
    int getColorFocusedBg();
    void setColorFocusedBg(int color);
    int getColorSelectionFg();
    void setColorSelectionFg(int color);
    int getColorSelectionBg();
    void setColorSelectionBg(int color);
    int getColorCaretBg();
    void setColorCaretBg(int color);
    int getColorCaretFg();
    void setColorCaretFg(int color);

    String getPlaceholder();
    void setPlaceholder(String placeholder);
    int getColorPlaceholderFg();
    void setColorPlaceholderFg(int color);

    int getX();
    int getY();

    interface StateChangeListener {
        void onStateChanged(TextInput source);
    }
    void addStateChangeListener(StateChangeListener listener);
}
