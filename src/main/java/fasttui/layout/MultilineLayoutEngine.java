package fasttui.layout;

import java.util.ArrayList;
import java.util.List;

public final class MultilineLayoutEngine {

    public static class LayoutResult {
        public List<String> lines = new ArrayList<>();
        public List<Integer> lineStarts = new ArrayList<>();
        public int caretRow = 0;
        public int caretCol = 0;
    }

    public static LayoutResult layout(String text, int width, int cursorPosition) {
        LayoutResult result = new LayoutResult();
        int maxW = width;
        if (maxW <= 0) maxW = 1;

        if (text.isEmpty()) {
            result.lines.add("");
            result.lineStarts.add(0);
            result.caretRow = 0;
            result.caretCol = 0;
            return result;
        }

        int len = text.length();
        int lineStart = 0;
        int i = 0;

        while (i < len) {
            int lineEnd = lineStart;
            int curWidth = 0;
            int lastSpace = -1;

            while (lineEnd < len) {
                char ch = text.charAt(lineEnd);
                if (ch == '\n') {
                    break;
                }

                int cp = text.codePointAt(lineEnd);
                int cpWidth = fastemojis.FastEmojis.getWidth(cp);
                int step = Character.charCount(cp);

                if (curWidth + cpWidth > maxW) {
                    if (lastSpace > lineStart) {
                        lineEnd = lastSpace;
                    }
                    break;
                }

                if (ch == ' ') {
                    lastSpace = lineEnd;
                }

                curWidth += cpWidth;
                lineEnd += step;
            }

            result.lines.add(text.substring(lineStart, lineEnd));
            result.lineStarts.add(lineStart);

            if (lineEnd < len && text.charAt(lineEnd) == '\n') {
                lineStart = lineEnd + 1;
            } else if (lineEnd < len && text.charAt(lineEnd) == ' ') {
                lineStart = lineEnd + 1;
            } else {
                lineStart = lineEnd;
            }
            i = lineStart;
        }

        if (len > 0 && text.charAt(len - 1) == '\n') {
            result.lines.add("");
            result.lineStarts.add(len);
        }

        // Caret pos mapping using visual column width
        for (int r = 0; r < result.lines.size(); r++) {
            int start = result.lineStarts.get(r);
            int end = start + result.lines.get(r).length();
            if (cursorPosition >= start && cursorPosition <= end) {
                result.caretRow = r;
                int visCol = 0;
                String sub = text.substring(start, Math.min(cursorPosition, end));
                for (int c = 0; c < sub.length(); ) {
                    int cp = sub.codePointAt(c);
                    visCol += fastemojis.FastEmojis.getWidth(cp);
                    c += Character.charCount(cp);
                }
                result.caretCol = visCol;
                break;
            }
        }

        return result;
    }
}
