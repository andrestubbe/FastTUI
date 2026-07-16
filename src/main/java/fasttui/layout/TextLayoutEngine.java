package fasttui.layout;

import fastterminal.FastTerminalScene;

public final class TextLayoutEngine {

    // --- Token types ---
    private static final int TYPE_WORD    = 0;
    private static final int TYPE_SPACE   = 1;
    private static final int TYPE_NEWLINE = 2;
    private static final int TYPE_TAB     = 3;

    private static final int TAB_WIDTH = 4;

    // --- Token buffers ---
    private final int[] tokenType  = new int[4096];
    private final int[] tokenStart = new int[4096];
    private final int[] tokenEnd   = new int[4096];
    private int tokenCount = 0;

    // --- Line buffers ---
    private final int[] lineStart      = new int[1024];
    private final int[] lineLength     = new int[1024];
    private final int[] lineTokenIndex = new int[16384];
    private int lineCount = 0;

    private String currentText;

    // ------------------------------------------------------------
    // PUBLIC API
    // ------------------------------------------------------------
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

            // NEWLINE
            if (c == '\n') {
                tokenType[tokenCount] = TYPE_NEWLINE;
                tokenStart[tokenCount] = i;
                tokenEnd[tokenCount] = i + 1;
                tokenCount++;
                i++;
                continue;
            }

            // SPACE
            if (c == ' ') {
                int start = i;
                while (i < len && text.charAt(i) == ' ') i++;

                tokenType[tokenCount] = TYPE_SPACE;
                tokenStart[tokenCount] = start;
                tokenEnd[tokenCount] = i;
                tokenCount++;
                continue;
            }

            // TAB
            if (c == '\t') {
                tokenType[tokenCount] = TYPE_TAB;
                tokenStart[tokenCount] = i;
                tokenEnd[tokenCount] = i + 1;
                tokenCount++;
                i++;
                continue;
            }

            // WORD
            int start = i;
            while (i < len) {
                char cc = text.charAt(i);
                if (cc == ' ' || cc == '\n' || cc == '\t') break;
                i++;
            }

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

            int tokenWidth =
                    (type == TYPE_TAB) ? TAB_WIDTH :
                            (type == TYPE_SPACE) ? (end - start) :
                            (type == TYPE_WORD) ? (end - start) :
                            0;

            // NEWLINE → force line break
            if (type == TYPE_NEWLINE) {
                lineStart[lineCount] = currentLineStart;
                lineLength[lineCount] = lineTokenWritePos - currentLineStart;
                lineCount++;

                currentWidth = 0;
                currentLineStart = lineTokenWritePos;
                continue;
            }

            // SPACE or TAB
            if (type == TYPE_SPACE || type == TYPE_TAB) {
                if (currentWidth > 0 && currentWidth + tokenWidth <= maxWidth) {
                    lineTokenIndex[lineTokenWritePos++] = t;
                    currentWidth += tokenWidth;
                } else if (currentWidth > 0) {
                    // wrap
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
                // wrap line
                if (currentWidth > 0) {
                    lineStart[lineCount] = currentLineStart;
                    lineLength[lineCount] = lineTokenWritePos - currentLineStart;
                    lineCount++;

                    currentWidth = 0;
                    currentLineStart = lineTokenWritePos;
                }

                // split long word
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

        // final line
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
                int type = tokenType[tokenIndex];
                int s = tokenStart[tokenIndex];
                int e = tokenEnd[tokenIndex];

                if (type == TYPE_TAB) {
                    // render TAB as spaces
                    for (int k = 0; k < TAB_WIDTH; k++) {
                        if (xPos < x + maxWidth && yPos >= 0 && yPos < scene.getHeight()) {
                            int finalBg = bg;
                            if (finalBg == -1 && sceneBgBuffer != null) {
                                int idx = yPos * sceneW + xPos;
                                if (idx >= 0 && idx < sceneBgBuffer.length) {
                                    finalBg = sceneBgBuffer[idx];
                                }
                            }
                            scene.writeCell(xPos, yPos, ' ', fg, finalBg);
                            xPos++;
                        }
                    }
                    continue;
                }

                // normal tokens
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
