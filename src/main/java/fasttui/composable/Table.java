package fasttui.composable;

import fastterminal.FastTerminalScene;
import fasttui.component.BorderStyle;
import fasttui.component.Component;
import fasttui.component.Container;

public class Table extends Container {

    private final int[] columnWidths;
    private final Component[][] cellComponents;
    private BorderStyle borderStyle = BorderStyle.SINGLE;
    private int borderColor = 0x767676; // Campbell Gray for dividers

    public Table(int x, int y, int width, int height, int rows, int cols, int[] columnWidths) {
        super(x, y, width, height);
        this.columnWidths = columnWidths;
        this.cellComponents = new Component[rows][cols];
        rebuildGrid();
    }

    public void rebuildGrid() {
        this.children.clear();

        int rows = cellComponents.length;
        int cols = cellComponents[0].length;
        int currentY = 0;

        for (int r = 0; r < rows; r++) {
            if (currentY >= height) break;

            int currentX = 0;
            for (int c = 0; c < cols; c++) {
                int w = columnWidths[c];
                Component cellComp = cellComponents[r][c];

                if (cellComp != null) {
                    // Position component relative to Table
                    cellComp.setX(currentX);
                    cellComp.setY(currentY);
                    cellComp.setWidth(w);
                    cellComp.setHeight(1);
                    this.add(cellComp);
                }

                currentX += w;
                if (borderStyle != BorderStyle.NONE && c < cols - 1) {
                    currentX++; // Account for column separator
                }
            }
            currentY++;
        }
    }

    @Override
    public void render(FastTerminalScene scene) {
        if (!visible) return;

        // Draw general background if set
        if (backgroundColor != -1) {
            for (int r = 0; r < height; r++) {
                for (int c = 0; c < width; c++) {
                    scene.writeCell(x + c, y + r, ' ', -1, backgroundColor);
                }
            }
        }

        // Render cell child components
        super.render(scene);

        // Draw vertical grid separators (dividers)
        if (borderStyle != BorderStyle.NONE) {
            char separator = borderStyle.verticalLeft;
            int totalRows = Math.min(height, cellComponents.length);

            for (int r = 0; r < totalRows; r++) {
                int currentX = x;
                for (int col = 0; col < columnWidths.length - 1; col++) {
                    currentX += columnWidths[col];
                    if (currentX < x + width) {
                        scene.writeCell(currentX, y + r, separator, borderColor, backgroundColor);
                        currentX++;
                    }
                }
            }
        }
    }

    public void setCell(int row, int col, Component comp) {
        if (row >= 0 && row < cellComponents.length && col >= 0 && col < cellComponents[row].length) {
            cellComponents[row][col] = comp;
            rebuildGrid();
        }
    }

    public Component getCell(int row, int col) {
        if (row >= 0 && row < cellComponents.length && col >= 0 && col < cellComponents[row].length) {
            return cellComponents[row][col];
        }
        return null;
    }

    public void setBorderStyle(BorderStyle borderStyle) {
        this.borderStyle = borderStyle;
        rebuildGrid();
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }
}
