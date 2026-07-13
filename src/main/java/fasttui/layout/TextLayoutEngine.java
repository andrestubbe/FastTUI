package fasttui.layout;

import fastterminal.FastTerminalScene;

public final class TextLayoutEngine {

    // --- Token buffers ---
    private int[] tokenType = new int[2048];
    private int[] tokenStart = new int[2048];
    private int[] tokenEnd = new int[2048];
    private int tokenCount = 0;

    private static final int TYPE_WORD = 0;
    private static final int TYPE_SPACE = 1;
    private static final int TYPE_NEWLINE = 2;

    // --- Line buffers ---
    private int[] lineStart = new int[512];
    private int[] lineLength = new int[512];
    private int[] lineTokenIndex = new int[8192];
    private int lineCount = 0;

    private String currentText;

    public void layout(String text, int maxWidth) {
        this.currentText = text;
        tokenize(text);
        layoutTokens(maxWidth);
    }

    // ------------------------------------------------------------
    // TOKENIZER (ZERO ALLOCATION)
    // ------------------------------------------------------------
    private void tokenize(String text) {
        tokenCount = 0;
        int len = text.length();
        int i = 0;

        while (i < len) {
            char c = text.charAt(i);

            if (c == '\n') {
                tokenType[tokenCount] = TYPE_NEWLINE;
                tokenStart[tokenCount] = i;
                tokenEnd[tokenCount] = i + 1;
                tokenCount++;
                i++;
                continue;
            }

            if (c == ' ') {
                int start = i;
                while (i < len && text.charAt(i) == ' ') i++;

                tokenType[tokenCount] = TYPE_SPACE;
                tokenStart[tokenCount] = start;
                tokenEnd[tokenCount] = i;
                tokenCount++;
                continue;
            }

            int start = i;
            while (i < len && text.charAt(i) != ' ' && text.charAt(i) != '\n') i++;

            tokenType[tokenCount] = TYPE_WORD;
            tokenStart[tokenCount] = start;
            tokenEnd[tokenCount] = i;
            tokenCount++;
        }
    }

    // ------------------------------------------------------------
    // LAYOUT ENGINE (ZERO ALLOCATION)
    // ------------------------------------------------------------
    private void layoutTokens(int maxWidth) {
        lineCount = 0;
        int lineTokenWritePos = 0;

        int currentWidth = 0;
        int currentLineStart = 0;

        for (int t = 0; t < tokenCount; t++) {
            int type = tokenType[t];
            int start = tokenStart[t];
            int end = tokenEnd[t];
            int tokenWidth = end - start;

            if (type == TYPE_NEWLINE) {
                lineStart[lineCount] = currentLineStart;
                lineLength[lineCount] = lineTokenWritePos - currentLineStart;
                lineCount++;

                currentWidth = 0;
                currentLineStart = lineTokenWritePos;
                continue;
            }

            if (type == TYPE_SPACE) {
                if (currentWidth > 0 && currentWidth + tokenWidth <= maxWidth) {
                    lineTokenIndex[lineTokenWritePos++] = t;
                    currentWidth += tokenWidth;
                } else if (currentWidth > 0) {
                    lineStart[lineCount] = currentLineStart;
                    lineLength[lineCount] = lineTokenWritePos - currentLineStart;
                    lineCount++;

                    currentWidth = 0;
                    currentLineStart = lineTokenWritePos;
                }
                continue;
            }

            // WORD
            if (currentWidth + tokenWidth <= maxWidth) {
                lineTokenIndex[lineTokenWritePos++] = t;
                currentWidth += tokenWidth;
            } else {
                if (currentWidth > 0) {
                    lineStart[lineCount] = currentLineStart;
                    lineLength[lineCount] = lineTokenWritePos - currentLineStart;
                    lineCount++;

                    currentWidth = 0;
                    currentLineStart = lineTokenWritePos;
                }

                // Split long word
                int pos = start;
                while (pos < end) {
                    int chunk = Math.min(maxWidth, end - pos);

                    tokenStart[t] = pos;
                    tokenEnd[t] = pos + chunk;

                    lineTokenIndex[lineTokenWritePos++] = t;

                    pos += chunk;

                    if (pos < end) {
                        lineStart[lineCount] = currentLineStart;
                        lineLength[lineCount] = lineTokenWritePos - currentLineStart;
                        lineCount++;

                        currentLineStart = lineTokenWritePos;
                        currentWidth = 0;
                    } else {
                        currentWidth = chunk;
                    }
                }
            }
        }

        if (lineTokenWritePos > currentLineStart) {
            lineStart[lineCount] = currentLineStart;
            lineLength[lineCount] = lineTokenWritePos - currentLineStart;
            lineCount++;
        }
    }

    // ------------------------------------------------------------
    // RENDERER (ZERO ALLOCATION)
    // ------------------------------------------------------------
    public void render(FastTerminalScene scene, int x, int y, int fg, int bg, int maxWidth, int maxHeight) {

        int linesToRender = Math.min(lineCount, maxHeight);
        int[] sceneBgBuffer = scene.getBgBuffer();
        int sceneW = scene.getWidth();

        for (int row = 0; row < linesToRender; row++) {
            int yPos = y + row;
            int xPos = x;

            int ls = lineStart[row];
            int ll = lineLength[row];

            for (int i = 0; i < ll; i++) {
                int tokenIndex = lineTokenIndex[ls + i];
                int s = tokenStart[tokenIndex];
                int e = tokenEnd[tokenIndex];

                for (int p = s; p < e; p++) {
                    if (xPos < x + maxWidth && yPos >= 0 && yPos < scene.getHeight()) {
                        int finalBg = bg;
                        if (finalBg == -1 && sceneBgBuffer != null) {
                            int idx = yPos * sceneW + xPos;
                            if (idx >= 0 && idx < sceneBgBuffer.length) {
                                finalBg = sceneBgBuffer[idx];
                            }
                        }
                        scene.writeCell(xPos, yPos, currentText.charAt(p), fg, finalBg);
                        xPos++;
                    }
                }
            }
        }
    }
}
