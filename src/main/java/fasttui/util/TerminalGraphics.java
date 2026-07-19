package fasttui.util;

import fastterminal.FastTerminalScene;

/**
 * High-performance Text User Interface (TUI) styling and post-processing filters.
 * Part of the FastTUI library. Provides utility methods to apply drop shadows, 
 * translucent overlays, and visual effects over terminal buffers.
 */
public final class TerminalGraphics {

    private TerminalGraphics() {
        // Prevent instantiation
    }

    /**
     * Helper to check if a coordinate falls in the corners of a bounding box.
     * Disabled to keep shadows and overlays as clean, sharp rectangles.
     */
    private static boolean isCornerCell(int c, int r, int w, int h) {
        return false;
    }

    /**
     * Applies a semi-translucent solid color overlay (dimmer/tint) over a specific region of the scene.
     * Useful for dimming backgrounds, modal backdrops, or applying color filters.
     */
    public static void applyOverlay(FastTerminalScene scene, int x, int y, int w, int h, int color, double opacity) {
        applyFlatShadow(scene, x, y, w, h, color, opacity);
    }

    /**
     * Draws a semi-translucent rectangle and then blurs its edges into the surrounding background.
     * This creates a natural, realistic convolution-based drop shadow/glow on all sides.
     * (Non-stenciled version: blurs the entire area, including the background directly under the box).
     */
    public static void applyBlurredShadow(FastTerminalScene scene, int x, int y, int w, int h, int overlayColor, double opacity, double radiusX, double radiusY) {
        if (opacity <= 0.0) return;

        int sceneW = scene.getWidth();
        int sceneH = scene.getHeight();
        int[] bg = scene.getBgBuffer();

        int intRadiusX = (int) Math.ceil(radiusX);
        int intRadiusY = (int) Math.ceil(radiusY);

        int paddedW = w + 2 * intRadiusX;
        int paddedH = h + 2 * intRadiusY;

        double[] rawOpacities = new double[paddedW * paddedH];
        for (int r = 0; r < paddedH; r++) {
            boolean inY = (r >= intRadiusY && r < intRadiusY + h);
            for (int c = 0; c < paddedW; c++) {
                boolean inX = (c >= intRadiusX && c < intRadiusX + w);
                if (inY && inX && !isCornerCell(c - intRadiusX, r - intRadiusY, w, h)) {
                    rawOpacities[r * paddedW + c] = opacity;
                } else {
                    rawOpacities[r * paddedW + c] = 0.0;
                }
            }
        }

        double[] blurredOpacities = new double[paddedW * paddedH];
        for (int r = 0; r < paddedH; r++) {
            for (int c = 0; c < paddedW; c++) {
                double sumWeight = 0.0;
                double sumOpacity = 0.0;

                for (int dr = -intRadiusY; dr <= intRadiusY; dr++) {
                    int neighborY = r + dr;
                    if (neighborY < 0 || neighborY >= paddedH) continue;

                    double distY = Math.abs(dr);
                    double weightY;
                    if (distY <= radiusY) {
                        weightY = 1.0;
                    } else if (distY < radiusY + 1.0) {
                        weightY = (radiusY + 1.0) - distY;
                    } else {
                        weightY = 0.0;
                    }

                    for (int dc = -intRadiusX; dc <= intRadiusX; dc++) {
                        int neighborX = c + dc;
                        if (neighborX < 0 || neighborX >= paddedW) continue;

                        double distX = Math.abs(dc);
                        double weightX;
                        if (distX <= radiusX) {
                            weightX = 1.0;
                        } else if (distX < radiusX + 1.0) {
                            weightX = (radiusX + 1.0) - distX;
                        } else {
                            weightX = 0.0;
                        }

                        double weight = weightX * weightY;
                        if (weight > 0.0) {
                            sumOpacity += rawOpacities[neighborY * paddedW + neighborX] * weight;
                            sumWeight += weight;
                        }
                    }
                }

                blurredOpacities[r * paddedW + c] = (sumWeight > 0.0) ? (sumOpacity / sumWeight) : 0.0;
            }
        }

        int overlayR = (overlayColor >> 16) & 0xFF;
        int overlayG = (overlayColor >> 8) & 0xFF;
        int overlayB = overlayColor & 0xFF;

        for (int r = 0; r < paddedH; r++) {
            int row = y - intRadiusY + r;
            if (row < 0 || row >= sceneH) continue;

            for (int c = 0; c < paddedW; c++) {
                int col = x - intRadiusX + c;
                if (col < 0 || col >= sceneW) continue;

                double alpha = blurredOpacities[r * paddedW + c];
                if (alpha <= 0.005) continue;

                int idx = row * sceneW + col;
                int originalBg = bg[idx];
                if (originalBg == -1 || originalBg == -2) {
                    originalBg = 0x0C0C0C;
                }

                int rVal = (originalBg >> 16) & 0xFF;
                int gVal = (originalBg >> 8) & 0xFF;
                int bVal = originalBg & 0xFF;

                double factor = 1.0 - alpha;
                rVal = (int) (rVal * factor + overlayR * alpha);
                gVal = (int) (gVal * factor + overlayG * alpha);
                bVal = (int) (bVal * factor + overlayB * alpha);

                int blendedBg = (rVal << 16) | (gVal << 8) | bVal;
                scene.writeCell(col, row, -2, -2, blendedBg);
            }
        }
    }

    /**
     * Draws a drop shadow/glow around a window area where the window rectangle itself is completely
     * punched out (stenciled) using an offset.
     */
    public static void applyStenciledBlurredShadow(FastTerminalScene scene, int windowX, int windowY, int windowW, int windowH, int offsetX, int offsetY, int overlayColor, double opacity, double radiusX, double radiusY) {
        if (opacity <= 0.0) return;

        int sceneW = scene.getWidth();
        int sceneH = scene.getHeight();
        int[] bg = scene.getBgBuffer();

        int shadowX = windowX + offsetX;
        int shadowY = windowY + offsetY;

        int intRadiusX = (int) Math.ceil(radiusX);
        int intRadiusY = (int) Math.ceil(radiusY);

        int paddedW = windowW + 2 * intRadiusX;
        int paddedH = windowH + 2 * intRadiusY;

        double[] rawOpacities = new double[paddedW * paddedH];
        for (int r = 0; r < paddedH; r++) {
            boolean inY = (r >= intRadiusY && r < intRadiusY + windowH);
            for (int c = 0; c < paddedW; c++) {
                boolean inX = (c >= intRadiusX && c < intRadiusX + windowW);
                if (inY && inX && !isCornerCell(c - intRadiusX, r - intRadiusY, windowW, windowH)) {
                    rawOpacities[r * paddedW + c] = opacity;
                } else {
                    rawOpacities[r * paddedW + c] = 0.0;
                }
            }
        }

        double[] blurredOpacities = new double[paddedW * paddedH];
        for (int r = 0; r < paddedH; r++) {
            for (int c = 0; c < paddedW; c++) {
                double sumWeight = 0.0;
                double sumOpacity = 0.0;

                for (int dr = -intRadiusY; dr <= intRadiusY; dr++) {
                    int neighborY = r + dr;
                    if (neighborY < 0 || neighborY >= paddedH) continue;

                    double distY = Math.abs(dr);
                    double weightY;
                    if (distY <= radiusY) {
                        weightY = 1.0;
                    } else if (distY < radiusY + 1.0) {
                        weightY = (radiusY + 1.0) - distY;
                    } else {
                        weightY = 0.0;
                    }

                    for (int dc = -intRadiusX; dc <= intRadiusX; dc++) {
                        int neighborX = c + dc;
                        if (neighborX < 0 || neighborX >= paddedW) continue;

                        double distX = Math.abs(dc);
                        double weightX;
                        if (distX <= radiusX) {
                            weightX = 1.0;
                        } else if (distX < radiusX + 1.0) {
                            weightX = (radiusX + 1.0) - distX;
                        } else {
                            weightX = 0.0;
                        }

                        double weight = weightX * weightY;
                        if (weight > 0.0) {
                            sumOpacity += rawOpacities[neighborY * paddedW + neighborX] * weight;
                            sumWeight += weight;
                        }
                    }
                }

                blurredOpacities[r * paddedW + c] = (sumWeight > 0.0) ? (sumOpacity / sumWeight) : 0.0;
            }
        }

        int overlayR = (overlayColor >> 16) & 0xFF;
        int overlayG = (overlayColor >> 8) & 0xFF;
        int overlayB = overlayColor & 0xFF;

        for (int r = 0; r < paddedH; r++) {
            int row = shadowY - intRadiusY + r;
            if (row < 0 || row >= sceneH) continue;

            for (int c = 0; c < paddedW; c++) {
                int col = shadowX - intRadiusX + c;
                if (col < 0 || col >= sceneW) continue;

                if (col >= windowX && col < windowX + windowW && row >= windowY && row < windowY + windowH) {
                    continue;
                }

                double alpha = blurredOpacities[r * paddedW + c];
                if (alpha <= 0.005) continue;

                int idx = row * sceneW + col;
                int originalBg = bg[idx];
                if (originalBg == -1 || originalBg == -2) {
                    originalBg = 0x0C0C0C;
                }

                int rVal = (originalBg >> 16) & 0xFF;
                int gVal = (originalBg >> 8) & 0xFF;
                int bVal = originalBg & 0xFF;

                double factor = 1.0 - alpha;
                rVal = (int) (rVal * factor + overlayR * alpha);
                gVal = (int) (gVal * factor + overlayG * alpha);
                bVal = (int) (bVal * factor + overlayB * alpha);

                int blendedBg = (rVal << 16) | (gVal << 8) | bVal;
                scene.writeCell(col, row, -2, -2, blendedBg);
            }
        }
    }

    /**
     * Applies a soft 3D drop shadow with a linear gradient falloff towards the edges.
     */
    public static void applyDropShadow(FastTerminalScene scene, int x, int y, int w, int h, double minOpacity, double maxOpacity) {
        int sceneW = scene.getWidth();
        int sceneH = scene.getHeight();
        int[] bg = scene.getBgBuffer();

        for (int r = 0; r < h; r++) {
            int row = y + r;
            if (row < 0 || row >= sceneH) continue;

            double fadeY = Math.max(0.0, Math.min(1.0, (double) (h - 1 - r) / 2.0));

            for (int c = 0; c < w; c++) {
                int col = x + c;
                if (col < 0 || col >= sceneW) continue;

                if (isCornerCell(c, r, w, h)) continue;

                int distX = Math.min(c, w - 1 - c);
                double fadeX = Math.max(0.0, Math.min(1.0, (double) distX / 3.0));

                double cellOpacity = minOpacity + (maxOpacity - minOpacity) * fadeX * fadeY;
                double factor = 1.0 - Math.max(0.0, Math.min(1.0, cellOpacity));

                int idx = row * sceneW + col;
                int originalBg = bg[idx];

                if (originalBg == -1 || originalBg == -2) {
                    originalBg = 0x0C0C0C;
                }

                int red = (originalBg >> 16) & 0xFF;
                int green = (originalBg >> 8) & 0xFF;
                int blue = originalBg & 0xFF;

                red = (int) (red * factor);
                green = (int) (green * factor);
                blue = (int) (blue * factor);

                int darkenedBg = (red << 16) | (green << 8) | blue;
                scene.writeCell(col, row, -2, -2, darkenedBg);
            }
        }
    }

    /**
     * Applies a flat (solid opacity) drop shadow over cells in a scene region.
     */
    public static void applyFlatShadow(FastTerminalScene scene, int x, int y, int w, int h, int shadowBg, double shadowAlpha) {
        int sceneW = scene.getWidth();
        int sceneH = scene.getHeight();
        int[] bg = scene.getBgBuffer();

        double factor = 1.0 - Math.max(0.0, Math.min(1.0, shadowAlpha));

        int shadowR = (shadowBg >> 16) & 0xFF;
        int shadowG = (shadowBg >> 8) & 0xFF;
        int shadowB = shadowBg & 0xFF;

        for (int r = 0; r < h; r++) {
            int row = y + r;
            if (row < 0 || row >= sceneH) continue;

            for (int c = 0; c < w; c++) {
                int col = x + c;
                if (col < 0 || col >= sceneW) continue;

                if (isCornerCell(c, r, w, h)) continue;

                int idx = row * sceneW + col;
                int originalBg = bg[idx];

                if (originalBg == -1 || originalBg == -2) {
                    originalBg = 0x0C0C0C;
                }

                int rVal = (originalBg >> 16) & 0xFF;
                int gVal = (originalBg >> 8) & 0xFF;
                int bVal = originalBg & 0xFF;

                rVal = (int) (rVal * factor + shadowR * shadowAlpha);
                gVal = (int) (gVal * factor + shadowG * shadowAlpha);
                bVal = (int) (bVal * factor + shadowB * shadowAlpha);

                int blendedBg = (rVal << 16) | (gVal << 8) | bVal;
                scene.writeCell(col, row, -2, -2, blendedBg);
            }
        }
    }

    /**
     * Applies a true spatial Box Blur to the background colors of the scene within a given region (default 100% blend strength).
     */
    public static void applyBackgroundBlur(FastTerminalScene scene, int x, int y, int w, int h, double radiusX, double radiusY) {
        applyBackgroundBlur(scene, x, y, w, h, 1.0, radiusX, radiusY);
    }

    /**
     * Applies a true spatial Box Blur to the background colors of the scene within a given region, 
     * blending the blurred result with the original background colors using a blending alpha factor.
     *
     * @param scene   Target scene.
     * @param x       Starting column coordinate.
     * @param y       Starting row coordinate.
     * @param w       Region width.
     * @param h       Region height.
     * @param alpha   Blur blend mix strength (0.0 = no blur, 1.0 = fully blurred background).
     * @param radiusX Horizontal blur radius.
     * @param radiusY Vertical blur radius.
     */
    public static void applyBackgroundBlur(FastTerminalScene scene, int x, int y, int w, int h, double alpha, double radiusX, double radiusY) {
        if (radiusX <= 0.0 && radiusY <= 0.0 || alpha <= 0.0) return;
        
        int sceneW = scene.getWidth();
        int sceneH = scene.getHeight();
        int[] bg = scene.getBgBuffer();

        // 1. Copy the target background region to a temporary buffer to prevent feedback loop
        int[] tempBuffer = new int[w * h];
        for (int r = 0; r < h; r++) {
            int row = y + r;
            for (int c = 0; c < w; c++) {
                int col = x + c;
                int currentBg = 0x0C0C0C; // Default dark terminal background fallback
                if (row >= 0 && row < sceneH && col >= 0 && col < sceneW) {
                    int originalBg = bg[row * sceneW + col];
                    if (originalBg != -1 && originalBg != -2) {
                        currentBg = originalBg;
                    }
                }
                tempBuffer[r * w + c] = currentBg;
            }
        }

        int intRadiusX = (int) Math.ceil(radiusX);
        int intRadiusY = (int) Math.ceil(radiusY);

        // 2. Perform RGB Box Blur and blend it with original background using alpha
        for (int r = 0; r < h; r++) {
            int row = y + r;
            if (row < 0 || row >= sceneH) continue;

            for (int c = 0; c < w; c++) {
                int col = x + c;
                if (col < 0 || col >= sceneW) continue;

                if (isCornerCell(c, r, w, h)) continue;

                double sumR = 0, sumG = 0, sumB = 0;
                double totalWeight = 0;

                // Accumulate neighbor colors inside the kernel window using fractional weights
                for (int dr = -intRadiusY; dr <= intRadiusY; dr++) {
                    int neighborY = r + dr;
                    if (neighborY < 0 || neighborY >= h) continue;

                    double distY = Math.abs(dr);
                    double weightY;
                    if (distY <= radiusY) {
                        weightY = 1.0;
                    } else if (distY < radiusY + 1.0) {
                        weightY = (radiusY + 1.0) - distY;
                    } else {
                        weightY = 0.0;
                    }

                    for (int dc = -intRadiusX; dc <= intRadiusX; dc++) {
                        int neighborX = c + dc;
                        if (neighborX < 0 || neighborX >= w) continue;

                        double distX = Math.abs(dc);
                        double weightX;
                        if (distX <= radiusX) {
                            weightX = 1.0;
                        } else if (distX < radiusX + 1.0) {
                            weightX = (radiusX + 1.0) - distX;
                        } else {
                            weightX = 0.0;
                        }

                        double weight = weightX * weightY;
                        if (weight > 0.0) {
                            int pixel = tempBuffer[neighborY * w + neighborX];
                            sumR += ((pixel >> 16) & 0xFF) * weight;
                            sumG += ((pixel >> 8) & 0xFF) * weight;
                            sumB += (pixel & 0xFF) * weight;
                            totalWeight += weight;
                        }
                    }
                }

                if (totalWeight > 0.0) {
                    int idx = row * sceneW + col;
                    int originalBg = bg[idx];
                    if (originalBg == -1 || originalBg == -2) {
                        originalBg = 0x0C0C0C;
                    }

                    int origR = (originalBg >> 16) & 0xFF;
                    int origG = (originalBg >> 8) & 0xFF;
                    int origB = originalBg & 0xFF;

                    int blurR = (int) (sumR / totalWeight);
                    int blurG = (int) (sumG / totalWeight);
                    int blurB = (int) (sumB / totalWeight);

                    // Blend original background with the blurred background using alpha
                    double factor = 1.0 - alpha;
                    int blendedR = (int) (origR * factor + blurR * alpha);
                    int blendedG = (int) (origG * factor + blurG * alpha);
                    int blendedB = (int) (origB * factor + blurB * alpha);

                    int blendedColor = (blendedR << 16) | (blendedG << 8) | blendedB;
                    scene.writeCell(col, row, -2, -2, blendedColor);
                }
            }
        }
    }
}
