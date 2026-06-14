package fasttui.composable;
import fasttui.component.Component;
import fasttui.component.Panel;

import fastterminal.FastTerminalScene;
import java.util.List;
import java.util.function.Consumer;

/**
 * A beautiful, fully interactive dropdown selector that expands/collapses dynamically
 * and triggers action callbacks upon option selection.
 */
public class Dropdown extends Component {
    private List<String> items;
    private int selectedIndex = 0;
    private boolean expanded = false;
    private Consumer<Integer> onSelect;

    private int normalBg = -1; // Default terminal background
    private int hoverBg = 0x27272A;  // Charcoal zinc (interaction)
    private int itemHoverBg = 0x0EA5E9; // Glowing Sky Blue for item selection
    private int hoveredItemIndex = -1;
    private boolean focused = false;

    private int maxVisibleItems = 6;
    private int scrollOffset = 0;

    public Dropdown(int x, int y, int width, List<String> items, Consumer<Integer> onSelect) {
        super(x, y, width, 1);
        this.items = items;
        this.onSelect = onSelect;
    }

    public boolean isFocused() { return focused; }
    public void setFocused(boolean focused) { this.focused = focused; }

    public int getNormalBg() { return normalBg; }
    public void setNormalBg(int normalBg) { this.normalBg = normalBg; }

    public int getHoverBg() { return hoverBg; }
    public void setHoverBg(int hoverBg) { this.hoverBg = hoverBg; }

    public int getItemHoverBg() { return itemHoverBg; }
    public void setItemHoverBg(int itemHoverBg) { this.itemHoverBg = itemHoverBg; }

    public void ensureVisible(int index) {
        if (items == null || index < 0 || index >= items.size()) return;
        int visibleCount = Math.min(items.size(), maxVisibleItems);
        if (index < scrollOffset) {
            scrollOffset = index;
        } else if (index >= scrollOffset + visibleCount) {
            scrollOffset = index - visibleCount + 1;
        }
    }

    public void handleMouseWheel(int delta) {
        if (!expanded || items == null) return;
        int visibleCount = Math.min(items.size(), maxVisibleItems);
        int maxOffset = Math.max(0, items.size() - visibleCount);
        int newOffset = scrollOffset - delta;
        if (newOffset < 0) newOffset = 0;
        if (newOffset > maxOffset) newOffset = maxOffset;
        scrollOffset = newOffset;
    }

    @Override
    public boolean contains(int cellX, int cellY) {
        if (expanded && items != null) {
            int visibleCount = Math.min(items.size(), maxVisibleItems);
            return cellX >= x && cellX < x + width && cellY >= y && cellY < y + 1 + visibleCount;
        }
        return cellX >= x && cellX < x + width && cellY >= y && cellY < y + 1;
    }

    @Override
    public void handleMouseMove(int cellX, int cellY) {
        super.handleMouseMove(cellX, cellY);

        if (expanded && items != null && contains(cellX, cellY)) {
            int relativeRow = cellY - y;
            int visibleCount = Math.min(items.size(), maxVisibleItems);
            if (relativeRow >= 1 && relativeRow <= visibleCount) {
                hoveredItemIndex = scrollOffset + (relativeRow - 1);
            } else {
                hoveredItemIndex = -1;
            }
        } else {
            hoveredItemIndex = -1;
        }
    }

    @Override
    public void render(FastTerminalScene canvas) {
        if (!visible || items == null || items.isEmpty()) return;

        // 1. Render main collapsed header button
        int headerBg = (isHovered || focused) ? hoverBg : normalBg;
        for (int c = x; c < x + width; c++) {
            if (c >= 0 && c < canvas.getWidth() && y >= 0 && y < canvas.getHeight()) {
                canvas.writeCell(c, y, ' ', fgColor, headerBg);
            }
        }

        String headerText = items.get(selectedIndex);
        int maxTextLen = width - 2;
        if (headerText.length() > maxTextLen) {
            headerText = headerText.substring(0, maxTextLen - 2) + "..";
        }
        int textX = x;
        for (int i = 0; i < headerText.length(); i++) {
            int cx = textX + i;
            if (cx >= x && cx < x + width - 1 && cx < canvas.getWidth() && y >= 0 && y < canvas.getHeight()) {
                canvas.writeCell(cx, y, headerText.charAt(i), fgColor, headerBg);
            }
        }
        int arrowX = x + width - 1;
        if (arrowX >= x && arrowX < x + width && arrowX < canvas.getWidth() && y >= 0 && y < canvas.getHeight()) {
            canvas.writeCell(arrowX, y, '↓', fgColor, headerBg);
        }

        // 2. Render expanded dropdown options
        if (expanded) {
            int visibleCount = Math.min(items.size(), maxVisibleItems);
            boolean showScrollbar = items.size() > maxVisibleItems;

            for (int i = 0; i < visibleCount; i++) {
                int itemIdx = scrollOffset + i;
                int iy = y + 1 + i;
                if (iy < 0 || iy >= canvas.getHeight()) continue;

                int actualHoverIdx = (hoveredItemIndex == -1) ? selectedIndex : hoveredItemIndex;
                boolean isCurrentHover = (itemIdx == actualHoverIdx);

                int bg = isCurrentHover ? itemHoverBg : 0x18181B; // Obsidian backdrop
                int fg = isCurrentHover ? 0x000000 : 0xD4D4D8; // Black text on hovered sky-blue

                int contentWidth = showScrollbar ? width - 1 : width;

                for (int c = x; c < x + contentWidth; c++) {
                    if (c >= 0 && c < canvas.getWidth()) {
                        canvas.writeCell(c, iy, ' ', fg, bg);
                    }
                }

                String itemText = items.get(itemIdx);
                if (itemText.length() > contentWidth - 2) {
                    itemText = itemText.substring(0, contentWidth - 2);
                }
                for (int c = 0; c < itemText.length(); c++) {
                    int cx = x + 1 + c;
                    if (cx >= x && cx < x + contentWidth && cx < canvas.getWidth()) {
                        canvas.writeCell(cx, iy, itemText.charAt(c), fg, bg);
                    }
                }

                // Render scrollbar cell in the last column
                if (showScrollbar) {
                    int scrollbarX = x + width - 1;
                    if (scrollbarX >= 0 && scrollbarX < canvas.getWidth()) {
                        int thumbHeight = Math.max(1, (visibleCount * visibleCount) / items.size());
                        int scrollRange = items.size() - visibleCount;
                        int trackRange = visibleCount - thumbHeight;
                        int thumbStart = (scrollRange == 0) ? 0 : (scrollOffset * trackRange) / scrollRange;

                        boolean isThumb = (i >= thumbStart && i < thumbStart + thumbHeight);
                        int sBg = isThumb ? 0x52525B : 0x27272A; // light gray thumb, dark gray track
                        int sFg = isThumb ? 0xD4D4D8 : 0x3F3F46;
                        char sChar = isThumb ? '█' : '░';
                        canvas.writeCell(scrollbarX, iy, sChar, sFg, sBg);
                    }
                }
            }
        }
    }

    @Override
    public boolean handleMouseClick(int cellX, int cellY, boolean isPressed) {
        if (!isPressed) return false;

        if (contains(cellX, cellY)) {
            if (expanded && items != null) {
                int relativeRow = cellY - y;
                int visibleCount = Math.min(items.size(), maxVisibleItems);
                if (relativeRow == 0) {
                    expanded = false;
                } else if (relativeRow >= 1 && relativeRow <= visibleCount) {
                    selectedIndex = scrollOffset + (relativeRow - 1);
                    expanded = false;
                    if (onSelect != null) {
                        onSelect.accept(selectedIndex);
                    }
                }
            } else {
                expanded = true;
                ensureVisible(selectedIndex);
            }
            return true;
        } else {
            expanded = false;
        }
        return false;
    }

    public int getSelectedIndex() { return selectedIndex; }
    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        ensureVisible(selectedIndex);
    }
    public boolean isExpanded() { return expanded; }
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
        if (expanded) {
            ensureVisible(selectedIndex);
        }
    }

    public void setOptions(List<String> items) {
        this.items = items;
        this.scrollOffset = 0;
        if (this.selectedIndex >= items.size()) {
            this.selectedIndex = 0;
        }
        if (this.hoveredItemIndex >= items.size()) {
            this.hoveredItemIndex = -1;
        }
        ensureVisible(this.selectedIndex);
    }

    public int getHoveredItemIndex() {
        return hoveredItemIndex;
    }

    public void setHoveredItemIndex(int hoveredItemIndex) {
        this.hoveredItemIndex = hoveredItemIndex;
        ensureVisible(hoveredItemIndex);
    }
}
