package fasttui.component;

import fastemojis.FastEmojis;
import fastterminal.FastTerminalScene;
import fasttui.composable.Button;

/**
 * A beautiful solid-colored borderless container panel with a clean 3D drop-shadow.
 * Supports premium border styles (rounded, single, double) and top-aligned panel titles.
 * Inherits child container capabilities from Container.
 */
public class Window extends Container {

    public enum BorderStyle {
        NONE,
        SINGLE,
        ROUNDED,
        DOUBLE
    }

    private boolean hasShadow = true;
    private BorderStyle borderStyle = BorderStyle.NONE;
    private String title = null;
    private int borderFg = 0x64748B;

    private boolean hasHeaderBar = false;
    private int headerBg = 0xFFFFFF;
    private int headerFg = 0x18181B;
    private boolean showWindowButtons = true;
    private double bodyAlpha = 1.0; 

    private int shadowBg = 0x000000; 
    private int shadowFg = 0x000000; 
    private double shadowAlpha = 0.25; 
    private boolean hasResizeButton = true; 
    private Button resizeButton;            
    private Button closeButton;             
    private Button minimizeButton;          

    private boolean isMinimized = false;
    private int restoredHeight = -1; 

    private boolean beosStyle = false;

    public Window(int x, int y, int width, int height, int bgColor) {
        super(x, y, width, height);
        this.backgroundColor = bgColor;

        this.resizeButton = new Button(width - 1, height - 1, 1, 1, "◢", null);
        this.resizeButton.setBackgroundNormal(bgColor);
        this.resizeButton.setBackgroundHover(0xE2E8F0);
        this.resizeButton.setBackgroundPressed(0x94A3B8);
        this.resizeButton.setForegroundNormal(0x94A3B8);
        this.resizeButton.setForegroundHover(0x0F172A);
        this.resizeButton.setForegroundPressed(0x0F172A);
        this.resizeButton.setVisible(hasResizeButton);
        add(this.resizeButton);

        this.closeButton = new Button(width - 3, 0, 3, 1, " ✕ ", () -> System.exit(0));
        this.closeButton.setBackgroundNormal(0xEF4444);
        this.closeButton.setForegroundNormal(0xFFFFFF);
        this.closeButton.setBackgroundHover(0xDC2626);
        this.closeButton.setForegroundHover(0xFFFFFF);
        this.closeButton.setBackgroundPressed(0x991B1B);
        this.closeButton.setForegroundPressed(0xFFFFFF);
        this.closeButton.setVisible(showWindowButtons && hasHeaderBar);
        add(this.closeButton);

        this.minimizeButton = new Button(width - 6, 0, 3, 1, " _ ", () -> toggleMinimize());
        this.minimizeButton.setBackgroundNormal(0xD9C676);
        this.minimizeButton.setForegroundNormal(0x000000);
        this.minimizeButton.setBackgroundHover(0xC9B55F);
        this.minimizeButton.setForegroundHover(0x000000);
        this.minimizeButton.setBackgroundPressed(0xB09F48);
        this.minimizeButton.setForegroundPressed(0x000000);
        this.minimizeButton.setVisible(showWindowButtons && hasHeaderBar);
        add(this.minimizeButton);
    }

    public void setHasResizeButton(boolean hasResizeButton) {
        this.hasResizeButton = hasResizeButton;
        if (this.resizeButton != null) {
            this.resizeButton.setVisible(hasResizeButton);
        }
    }

    public void toggleMinimize() {
        if (isMinimized) {
            isMinimized = false;
            this.visible = true;
            if (restoredHeight > 1) {
                super.setHeight(restoredHeight);
                for (Component child : children) {
                    if (child != resizeButton && child != closeButton && child != minimizeButton) {
                        child.setVisible(true);
                        int relY = child.getY() - this.y;
                        if (relY == 1) {
                            child.setHeight(restoredHeight - 1);
                        }
                    }
                }
                if (resizeButton != null) {
                    resizeButton.setY(this.y + restoredHeight - 1);
                    resizeButton.setVisible(hasResizeButton);
                }
            }
        } else {
            isMinimized = true;
            restoredHeight = this.height;
            this.visible = false;
        }
    }

    public int getIconWidth() {
        String label = getIconLabel();
        return label.length() + 2; 
    }

    private String getIconLabel() {
        return " ▢ " + (title != null ? title : "Window") + " ";
    }

    public void renderDesktopIcon(FastTerminalScene canvas, int iconX, int iconY) {
        if (!isMinimized) return;
        String label = getIconLabel();
        int iconW = label.length() + 2;
        int bg = 0xD9C676;
        for (int i = 0; i < iconW; i++) {
            int cx = iconX + i;
            if (cx >= 0 && cx < canvas.getWidth() && iconY >= 0 && iconY < canvas.getHeight()) {
                char ch = (i >= 1 && i - 1 < label.length()) ? label.charAt(i - 1) : ' ';
                canvas.writeCell(cx, iconY, ch, headerFg, bg);
            }
        }
    }

    public boolean isIconHit(int mx, int my, int iconX, int iconY) {
        if (!isMinimized) return false;
        return my == iconY && mx >= iconX && mx < iconX + getIconWidth();
    }

    public boolean isMinimized() {
        return isMinimized;
    }

    @Override
    public void render(FastTerminalScene scene) {
        if (!visible) return;

        if (hasShadow) {
            int sy = y + height;
            for (int sx = x; sx < x + width; sx++) {
                if (sx >= 0 && sx < scene.getWidth() && sy >= 0 && sy < scene.getHeight()) {
                    int idx = sy * scene.getWidth() + sx;
                    int cp = scene.getCodepointBuffer()[idx];
                    if (cp == '▄') {
                        scene.writeCellAlpha(sx, sy, cp, shadowFg, shadowBg, 0.0, shadowAlpha);
                    } else {
                        scene.writeCellAlpha(sx, sy, cp, shadowFg, shadowBg, shadowAlpha, shadowAlpha);
                    }
                }
            }
        }

        int finalBgColor = backgroundColor;
        if (hasHeaderBar && backgroundColor == headerBg) {
            finalBgColor = blendColor(backgroundColor, 0x000000, 0.08);
        }

        int gradLeft = 0xD9C676; 
        int gradRight = 0xD9C676; 

        for (int r = y; r < y + height; r++) {
            boolean isHeaderRow = (hasHeaderBar && r == y);
            int currentBg = isHeaderRow ? headerBg : finalBgColor;
            int currentFg = isHeaderRow ? headerFg : foregroundColor;
            for (int c = x; c < x + width; c++) {
                if (c >= 0 && c < scene.getWidth() && r >= 0 && r < scene.getHeight()) {
                    if (isHeaderRow && beosStyle) {
                        double t = (width > 1) ? (double) (c - x) / (width - 1) : 0.0;
                        int gradBg = blendColor(gradLeft, gradRight, t);
                        scene.writeCell(c, r, ' ', currentFg, gradBg);
                    } else if (isHeaderRow) {
                        scene.writeCell(c, r, ' ', currentFg, currentBg);
                    } else {
                        if (bodyAlpha >= 1.0) {
                            scene.writeCell(c, r, ' ', currentFg, currentBg);
                        } else {
                            scene.writeCellAlpha(c, r, ' ', currentFg, currentBg, 1.0, bodyAlpha);
                        }
                    }
                }
            }
        }

        if (borderStyle != BorderStyle.NONE && !isMinimized) {
            String horiz = FastEmojis.BOX_HORIZONTAL;
            String vert = FastEmojis.BOX_VERTICAL;
            String tl = FastEmojis.BOX_TOP_LEFT;
            String tr = FastEmojis.BOX_TOP_RIGHT;
            String bl = FastEmojis.BOX_BOTTOM_LEFT;
            String br = FastEmojis.BOX_BOTTOM_RIGHT;

            if (borderStyle == BorderStyle.ROUNDED) {
                tl = FastEmojis.BOX_ROUND_TOP_LEFT;
                tr = FastEmojis.BOX_ROUND_TOP_RIGHT;
                bl = FastEmojis.BOX_ROUND_BOTTOM_LEFT;
                br = FastEmojis.BOX_ROUND_BOTTOM_RIGHT;
            } else if (borderStyle == BorderStyle.DOUBLE) {
                horiz = FastEmojis.BOX_DOUBLE_HORIZONTAL;
                vert = FastEmojis.BOX_DOUBLE_VERTICAL;
                tl = FastEmojis.BOX_DOUBLE_TOP_LEFT;
                tr = FastEmojis.BOX_DOUBLE_TOP_RIGHT;
                bl = FastEmojis.BOX_DOUBLE_BOTTOM_LEFT;
                br = FastEmojis.BOX_DOUBLE_BOTTOM_RIGHT;
            }

            int hCp = horiz.codePointAt(0);
            int vCp = vert.codePointAt(0);
            int tlCp = tl.codePointAt(0);
            int trCp = tr.codePointAt(0);
            int blCp = bl.codePointAt(0);
            int brCp = br.codePointAt(0);

            if (!hasHeaderBar) {
                for (int c = x + 1; c < x + width - 1; c++) {
                    scene.writeCell(c, y, hCp, borderFg, backgroundColor);
                    scene.writeCell(c, y + height - 1, hCp, borderFg, backgroundColor);
                }
                for (int r = y + 1; r < y + height - 1; r++) {
                    scene.writeCell(x, r, vCp, borderFg, backgroundColor);
                    scene.writeCell(x + width - 1, r, vCp, borderFg, backgroundColor);
                }
                scene.writeCell(x, y, tlCp, borderFg, backgroundColor);
                scene.writeCell(x + width - 1, y, trCp, borderFg, backgroundColor);
                scene.writeCell(x, y + height - 1, blCp, borderFg, backgroundColor);
                scene.writeCell(x + width - 1, y + height - 1, brCp, borderFg, backgroundColor);
            } else {
                for (int c = x + 1; c < x + width - 1; c++) {
                    scene.writeCell(c, y + height - 1, hCp, borderFg, backgroundColor);
                }
                for (int r = y + 1; r < y + height - 1; r++) {
                    scene.writeCell(x, r, vCp, borderFg, backgroundColor);
                    scene.writeCell(x + width - 1, r, vCp, borderFg, backgroundColor);
                }
                scene.writeCell(x, y + height - 1, blCp, borderFg, backgroundColor);
                scene.writeCell(x + width - 1, y + height - 1, brCp, borderFg, backgroundColor);
            }
        }

        if (hasHeaderBar) {
            if (title != null && !title.isEmpty()) {
                int titleX = x + 2;
                for (int i = 0; i < title.length(); i++) {
                    int cx = titleX + i;
                    if (cx >= 0 && cx < scene.getWidth()) {
                        int titleBg;
                        if (beosStyle) {
                            double t = (width > 1) ? (double) (cx - x) / (width - 1) : 0.0;
                            titleBg = blendColor(gradLeft, gradRight, t);
                        } else {
                            titleBg = headerBg;
                        }
                        scene.writeCell(cx, y, title.charAt(i), headerFg, titleBg);
                    }
                }
            }
        } else {
            if (title != null && !title.isEmpty() && borderStyle != BorderStyle.NONE) {
                String formattedTitle = " " + title + " ";
                int titleX = x + (width - formattedTitle.length()) / 2;
                if (titleX > x) {
                    scene.writeString(titleX, y, formattedTitle, borderFg, backgroundColor);
                }
            }
        }

        if (resizeButton != null) {
            resizeButton.setBackgroundNormal(finalBgColor);
        }

        for (Component child : children) {
            if (child != resizeButton && child != closeButton && child != minimizeButton) {
                child.render(scene);
            }
        }
        if (minimizeButton != null) minimizeButton.render(scene);
        if (closeButton != null) closeButton.render(scene);
        if (resizeButton != null) resizeButton.render(scene);
    }

    private int blendColor(int color1, int color2, double ratio) {
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int r = (int) (r1 * (1.0 - ratio) + r2 * ratio);
        int g = (int) (g1 * (1.0 - ratio) + g2 * ratio);
        int b = (int) (b1 * (1.0 - ratio) + b2 * ratio);

        return (r << 16) | (g << 8) | b;
    }

    public boolean isCloseClick(int mx, int my) {
        if (!hasHeaderBar || !showWindowButtons) return false;
        return my == y && mx >= x + width - 3 && mx < x + width;
    }

    public boolean isMinimizeClick(int mx, int my) {
        if (!hasHeaderBar || !showWindowButtons) return false;
        return my == y && mx >= x + width - 6 && mx < x + width - 3;
    }

    public boolean isHasShadow() {
        return hasShadow;
    }

    public void setHasShadow(boolean hasShadow) {
        this.hasShadow = hasShadow;
    }

    public BorderStyle getBorderStyle() {
        return borderStyle;
    }

    public void setBorderStyle(BorderStyle borderStyle) {
        this.borderStyle = borderStyle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getBorderFg() {
        return borderFg;
    }

    public void setBorderFg(int borderFg) {
        this.borderFg = borderFg;
    }

    public boolean isResizeClick(int mx, int my) {
        if (!hasResizeButton) return false;
        return mx == x + width - 1 && my == y + height - 1;
    }

    @Override
    public void setWidth(int newWidth) {
        super.setWidth(newWidth);
        if (resizeButton != null) {
            resizeButton.setX(this.x + newWidth - 1);
        }
        if (closeButton != null) {
            closeButton.setX(this.x + newWidth - 3);
        }
        if (minimizeButton != null) {
            minimizeButton.setX(this.x + newWidth - 6);
        }
        for (Component child : children) {
            if (child != resizeButton && child != closeButton && child != minimizeButton) {
                int relX = child.getX() - this.x;
                if (relX == 0) {
                    child.setWidth(newWidth);
                } else {
                    child.setWidth(newWidth - 2);
                }
            }
        }
    }

    @Override
    public void setHeight(int newHeight) {
        super.setHeight(newHeight);
        if (resizeButton != null) {
            resizeButton.setY(this.y + newHeight - 1);
        }
        for (Component child : children) {
            if (child != resizeButton && child != closeButton && child != minimizeButton) {
                int relY = child.getY() - this.y;
                if (relY == 1) {
                    child.setHeight(newHeight - 1);
                } else {
                    child.setHeight(newHeight - 2);
                }
            }
        }
    }

    public boolean isHasHeaderBar() {
        return hasHeaderBar;
    }

    public void setHasHeaderBar(boolean hasHeaderBar) {
        this.hasHeaderBar = hasHeaderBar;
        if (closeButton != null) {
            closeButton.setVisible(showWindowButtons && hasHeaderBar);
        }
        if (minimizeButton != null) {
            minimizeButton.setVisible(showWindowButtons && hasHeaderBar);
        }
    }

    public int getHeaderBg() {
        return headerBg;
    }

    public void setHeaderBg(int headerBg) {
        this.headerBg = headerBg;
    }

    public int getHeaderFg() {
        return headerFg;
    }

    public void setHeaderFg(int headerFg) {
        this.headerFg = headerFg;
    }

    public boolean isShowWindowButtons() {
        return showWindowButtons;
    }

    public void setShowWindowButtons(boolean showWindowButtons) {
        this.showWindowButtons = showWindowButtons;
        if (closeButton != null) {
            closeButton.setVisible(showWindowButtons && hasHeaderBar);
        }
        if (minimizeButton != null) {
            minimizeButton.setVisible(showWindowButtons && hasHeaderBar);
        }
    }

    public boolean isBeosStyle() {
        return beosStyle;
    }

    public void setBeosStyle(boolean beosStyle) {
        this.beosStyle = beosStyle;
    }

    public double getBodyAlpha() {
        return bodyAlpha;
    }

    public void setBodyAlpha(double bodyAlpha) {
        this.bodyAlpha = bodyAlpha;
    }

    public double getShadowAlpha() {
        return shadowAlpha;
    }

    public void setShadowAlpha(double shadowAlpha) {
        this.shadowAlpha = shadowAlpha;
    }

    public int getShadowBg() {
        return shadowBg;
    }

    public void setShadowBg(int shadowBg) {
        this.shadowBg = shadowBg;
    }

    public int getShadowFg() {
        return shadowFg;
    }

    public void setShadowFg(int shadowFg) {
        this.shadowFg = shadowFg;
    }
}
