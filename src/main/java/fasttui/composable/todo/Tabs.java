package fasttui.composable.todo;

import fastterminal.FastTerminalScene;
import fasttui.component.Component;
import fasttui.component.Container;

import java.util.ArrayList;
import java.util.List;

public class Tabs extends Component {
    private List<String> tabTitles = new ArrayList<>();
    private List<Container> tabContainer = new ArrayList<>();
    private int selectedIndex = 0;
    private int activeTabBg = 0x666666;
    private int activeTabFg = 0xFFFFFF;
    private int inactiveTabBg = 0x222222;
    private int inactiveTabFg = 0xAAAAAA;

    public Tabs(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public void addTab(String title, Container container) {
        tabTitles.add(title);
        container.setX(this.x);
        container.setY(this.y + 1); // Below tab header
        container.setWidth(this.width);
        container.setHeight(this.height - 1);
        tabContainer.add(container);
    }

    @Override
    public void render(FastTerminalScene scene) {
        if (!visible) return;

        // Draw tab headers
        int currentX = x;
        int TAB_WIDTH = 20;
        for (int i = 0; i < tabTitles.size(); i++) {
            String title = tabTitles.get(i);
            int pad = TAB_WIDTH - title.length();
            int padLeft = pad / 2;
            int padRight = pad - padLeft;
            String paddedTitle = " ".repeat(padLeft) + title + " ".repeat(padRight);

            int bg = (i == selectedIndex) ? activeTabBg : inactiveTabBg;
            int fg = (i == selectedIndex) ? activeTabFg : inactiveTabFg;

            for (int c = 0; c < paddedTitle.length(); c++) {
                if (currentX + c < x + width) {
                    scene.writeCell(currentX + c, y, paddedTitle.charAt(c), fg, bg);
                }
            }
            currentX += TAB_WIDTH; // No gap
        }

        // Render selected panel
        if (selectedIndex >= 0 && selectedIndex < tabContainer.size()) {
            tabContainer.get(selectedIndex).render(scene);
        }
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        for (Container p : tabContainer) p.setX(x);
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        for (Container p : tabContainer) p.setY(y + 1);
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        for (Container p : tabContainer) p.setWidth(width);
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        for (Container p : tabContainer) p.setHeight(height - 1);
    }

    public void setActiveTabBackgroundColor(int color) {
        this.activeTabBg = color;
    }

    public void setActiveTabForeground(int color) {
        this.activeTabFg = color;
    }

    public void setInactiveTabBg(int color) {
        this.inactiveTabBg = color;
    }

    public void setInactiveTabFg(int color) {
        this.inactiveTabFg = color;
    }
}
