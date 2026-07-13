package fasttui.composable.todo;

import fasttui.component.BorderStyle;
import fasttui.component.Container;
import fasttui.composable.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Dropdown extends Container {
    private List<String> items;
    private int selectedIndex = 0;
    private boolean expanded = false;
    private Consumer<Integer> onSelect;

    private Button headerBtn;
    private final List<Button> optionBtns = new ArrayList<>();

    private int headerBgNormal = -1;
    private int headerFgNormal = 0xCCCCCC;
    private int headerBgHover = 0xCCCCCC;
    private int headerFgHover = 0x0C0C0C;

    private int optionBgNormal = 0x0C0C0C;
    private int optionFgNormal = 0xCCCCCC;
    private int optionBgHover = 0xCCCCCC;
    private int optionFgHover = 0x0C0C0C;

    private int maxVisibleItems = 6;
    private int scrollOffset = 0;
    private int headerHeight = 1;
    private BorderStyle headerBorderStyle = BorderStyle.NONE;

    public Dropdown(int x, int y, int width, List<String> items, Consumer<Integer> onSelect) {
        this(x, y, width, 1, items, onSelect);
    }

    public Dropdown(int x, int y, int width, int headerHeight, List<String> items, Consumer<Integer> onSelect) {
        super(x, y, width, headerHeight);
        this.headerHeight = headerHeight;
        this.headerBorderStyle = BorderStyle.NONE;
        this.items = items;
        this.onSelect = onSelect;
        rebuildComponents();
    }

    public void setHeaderColors(int bgNormal, int fgNormal, int bgHover, int fgHover) {
        this.headerBgNormal = bgNormal;
        this.headerFgNormal = fgNormal;
        this.headerBgHover = bgHover;
        this.headerFgHover = fgHover;
        rebuildComponents();
    }

    public void setOptionColors(int bgNormal, int fgNormal, int bgHover, int fgHover) {
        this.optionBgNormal = bgNormal;
        this.optionFgNormal = fgNormal;
        this.optionBgHover = bgHover;
        this.optionFgHover = fgHover;
        rebuildComponents();
    }

    public void setBorderStyle(BorderStyle style) {
        this.headerBorderStyle = style;
        rebuildComponents();
    }

    private void rebuildComponents() {
        // Clear children
        this.children.clear();
        this.optionBtns.clear();

        // 1. Create header button with arrow indicator
        String arrow = expanded ? " ↑" : " ↓";
        String headerText = (items != null && !items.isEmpty()) ? items.get(selectedIndex) : "";

        boolean hasBorder = (headerBorderStyle != BorderStyle.NONE);
        int targetWidth = hasBorder ? width - 2 : width;
        int remaining = targetWidth - headerText.length() - 2;
        if (remaining > 0) {
            headerText = headerText + " ".repeat(remaining);
        }
        headerText += arrow;

        headerBtn = new Button(0, 0, width, headerHeight, headerText, () -> {
            toggleExpanded();
        });
        headerBtn.setBorderStyle(headerBorderStyle);
        headerBtn.setBackgroundNormal(headerBgNormal);
        headerBtn.setForegroundNormal(headerFgNormal);
        headerBtn.setBackgroundHover(headerBgHover);
        headerBtn.setForegroundHover(headerFgHover);
        this.add(headerBtn);

        // 2. Create option buttons if expanded
        if (expanded && items != null) {
            int visibleCount = Math.min(items.size(), maxVisibleItems);
            this.setHeight(headerHeight + visibleCount);

            for (int i = 0; i < visibleCount; i++) {
                int itemIdx = scrollOffset + i;
                String itemText = items.get(itemIdx);

                final int finalIdx = itemIdx;
                Button optBtn = new Button(0, headerHeight + i, width, 1, " " + itemText, () -> {
                    selectItem(finalIdx);
                });

                optBtn.setBackgroundNormal(optionBgNormal);
                optBtn.setForegroundNormal(optionFgNormal);
                optBtn.setBackgroundHover(optionBgHover);
                optBtn.setForegroundHover(optionFgHover);
                optBtn.setAlignment(Button.Alignment.LEFT);

                this.add(optBtn);
                this.optionBtns.add(optBtn);
            }
        } else {
            this.setHeight(headerHeight);
        }
    }

    private void toggleExpanded() {
        this.expanded = !expanded;
        rebuildComponents();
    }

    private void selectItem(int index) {
        this.selectedIndex = index;
        this.expanded = false;
        rebuildComponents();
        if (onSelect != null) {
            onSelect.accept(index);
        }
    }

    public void handleMouseWheel(int delta) {
        if (!expanded || items == null) return;
        int visibleCount = Math.min(items.size(), maxVisibleItems);
        int maxOffset = Math.max(0, items.size() - visibleCount);
        int newOffset = scrollOffset - delta;
        if (newOffset < 0) newOffset = 0;
        if (newOffset > maxOffset) newOffset = maxOffset;
        if (newOffset != scrollOffset) {
            scrollOffset = newOffset;
            rebuildComponents();
        }
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        rebuildComponents();
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
        rebuildComponents();
    }

    public void setOptions(List<String> items) {
        this.items = items;
        this.scrollOffset = 0;
        if (this.selectedIndex >= items.size()) {
            this.selectedIndex = 0;
        }
        rebuildComponents();
    }

    public void handleKey(int vKey, char keyChar) {
        if (!expanded || items == null || items.isEmpty()) return;

        if (vKey == 0x26) { // Up
            if (selectedIndex > 0) {
                selectItem(selectedIndex - 1);
            }
        } else if (vKey == 0x28) { // Down
            if (selectedIndex < items.size() - 1) {
                selectItem(selectedIndex + 1);
            }
        }
    }
}
