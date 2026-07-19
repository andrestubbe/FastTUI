package fasttui.util;

import fastansi.CellConsumer;
import fastterminal.FastTerminalScene;

/**
 * @class Gradient
 * @brief High-performance, zero-allocation 24-bit True Color color interpolation engine.
 * 
 * Provides static linear color interpolation (lerp) operations across cell consumer layouts,
 * supporting horizontal, vertical, and diagonal gradient space spans.
 */
public final class Gradient {

    private Gradient() {
        // Prevent instantiation
    }

    /**
     * @brief Linearly interpolates (lerp) between two 24-bit RGB colors by a fraction t [0.0, 1.0].
     * 
     * @param colorStart Starting packed RGB color.
     * @param colorEnd Ending packed RGB color.
     * @param t Interpolation fraction factor bounded within [0.0, 1.0].
     * @return Packed 24-bit RGB interpolated value.
     */
    public static int interpolate(int colorStart, int colorEnd, double t) {
        t = Math.max(0.0, Math.min(1.0, t));
        
        int rS = (colorStart >> 16) & 0xFF;
        int gS = (colorStart >> 8) & 0xFF;
        int bS = colorStart & 0xFF;

        int rE = (colorEnd >> 16) & 0xFF;
        int gE = (colorEnd >> 8) & 0xFF;
        int bE = colorEnd & 0xFF;

        int r = (int) (rS + (rE - rS) * t);
        int g = (int) (gS + (gE - gS) * t);
        int b = (int) (bS + (bE - bS) * t);

        return (r << 16) | (g << 8) | b;
    }

    /**
     * @brief Applies a horizontal linear gradient to background cells.
     */
    public static void applyHorizontalBg(CellConsumer scene, int startX, int startY, int width, int height, int colorStart, int colorEnd) {
        for (int r = 0; r < height; r++) {
            int row = startY + r;
            for (int c = 0; c < width; c++) {
                int col = startX + c;
                double t = width > 1 ? (double) c / (width - 1) : 0.0;
                scene.writeCell(col, row, -2, -2, interpolate(colorStart, colorEnd, t));
            }
        }
        if (scene instanceof FastTerminalScene) {
            ((FastTerminalScene) scene).setDirty(true);
        }
    }

    /**
     * @brief Applies a vertical linear gradient to background cells.
     */
    public static void applyVerticalBg(CellConsumer scene, int startX, int startY, int width, int height, int colorStart, int colorEnd) {
        for (int r = 0; r < height; r++) {
            int row = startY + r;
            double t = height > 1 ? (double) r / (height - 1) : 0.0;
            int color = interpolate(colorStart, colorEnd, t);
            for (int c = 0; c < width; c++) {
                int col = startX + c;
                scene.writeCell(col, row, -2, -2, color);
            }
        }
        if (scene instanceof FastTerminalScene) {
            ((FastTerminalScene) scene).setDirty(true);
        }
    }

    /**
     * @brief Applies a diagonal linear gradient to background cells.
     */
    public static void applyDiagonalBg(CellConsumer scene, int startX, int startY, int width, int height, int colorStart, int colorEnd) {
        int maxDist = (width - 1) + (height - 1);
        for (int r = 0; r < height; r++) {
            int row = startY + r;
            for (int c = 0; c < width; c++) {
                int col = startX + c;
                double t = maxDist > 0 ? (double) (c + r) / maxDist : 0.0;
                scene.writeCell(col, row, -2, -2, interpolate(colorStart, colorEnd, t));
            }
        }
        if (scene instanceof FastTerminalScene) {
            ((FastTerminalScene) scene).setDirty(true);
        }
    }

    /**
     * @brief Applies a horizontal linear gradient to foreground cells.
     */
    public static void applyHorizontalFg(CellConsumer scene, int startX, int startY, int width, int height, int colorStart, int colorEnd) {
        for (int r = 0; r < height; r++) {
            int row = startY + r;
            for (int c = 0; c < width; c++) {
                int col = startX + c;
                double t = width > 1 ? (double) c / (width - 1) : 0.0;
                scene.writeCell(col, row, -2, interpolate(colorStart, colorEnd, t), -2);
            }
        }
        if (scene instanceof FastTerminalScene) {
            ((FastTerminalScene) scene).setDirty(true);
        }
    }

    /**
     * @brief Applies a vertical linear gradient to foreground cells.
     */
    public static void applyVerticalFg(CellConsumer scene, int startX, int startY, int width, int height, int colorStart, int colorEnd) {
        for (int r = 0; r < height; r++) {
            int row = startY + r;
            double t = height > 1 ? (double) r / (height - 1) : 0.0;
            int color = interpolate(colorStart, colorEnd, t);
            for (int c = 0; c < width; c++) {
                int col = startX + c;
                scene.writeCell(col, row, -2, color, -2);
            }
        }
        if (scene instanceof FastTerminalScene) {
            ((FastTerminalScene) scene).setDirty(true);
        }
    }

    /**
     * @brief Applies a diagonal linear gradient to foreground cells.
     */
    public static void applyDiagonalFg(CellConsumer scene, int startX, int startY, int width, int height, int colorStart, int colorEnd) {
        int maxDist = (width - 1) + (height - 1);
        for (int r = 0; r < height; r++) {
            int row = startY + r;
            for (int c = 0; c < width; c++) {
                int col = startX + c;
                double t = maxDist > 0 ? (double) (c + r) / maxDist : 0.0;
                scene.writeCell(col, row, -2, interpolate(colorStart, colorEnd, t), -2);
            }
        }
        if (scene instanceof FastTerminalScene) {
            ((FastTerminalScene) scene).setDirty(true);
        }
    }

    /**
     * @brief Applies a radial linear gradient to background cells.
     */
    public static void applyRadialBg(CellConsumer scene, int startX, int startY, int width, int height, int colorStart, int colorEnd) {
        double cx = width / 2.0;
        double cy = height / 2.0;
        double maxDist = Math.sqrt(cx * cx + cy * cy);
        
        for (int r = 0; r < height; r++) {
            int row = startY + r;
            for (int c = 0; c < width; c++) {
                int col = startX + c;
                double dist = Math.sqrt((c - cx) * (c - cx) + (r - cy) * (r - cy));
                double t = maxDist > 0 ? dist / maxDist : 0.0;
                scene.writeCell(col, row, -2, -2, interpolate(colorStart, colorEnd, t));
            }
        }
        if (scene instanceof FastTerminalScene) {
            ((FastTerminalScene) scene).setDirty(true);
        }
    }

    /**
     * @brief Applies a conic linear gradient to background cells.
     */
    public static void applyConicBg(CellConsumer scene, int startX, int startY, int width, int height, int colorStart, int colorEnd) {
        double cx = width / 2.0;
        double cy = height / 2.0;
        
        for (int r = 0; r < height; r++) {
            int row = startY + r;
            for (int c = 0; c < width; c++) {
                int col = startX + c;
                double angle = Math.atan2(r - cy, c - cx);
                double t = (angle + Math.PI) / (2 * Math.PI);
                scene.writeCell(col, row, -2, -2, interpolate(colorStart, colorEnd, t));
            }
        }
        if (scene instanceof FastTerminalScene) {
            ((FastTerminalScene) scene).setDirty(true);
        }
    }
}
