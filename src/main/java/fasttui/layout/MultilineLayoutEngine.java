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
            int lineEnd = lineStart + maxW;
            if (lineEnd > len) lineEnd = len;

            int newlineIdx = text.substring(lineStart, lineEnd).indexOf('\n');
            if (newlineIdx != -1) {
                lineEnd = lineStart + newlineIdx;
                result.lines.add(text.substring(lineStart, lineEnd));
                result.lineStarts.add(lineStart);
                lineStart = lineEnd + 1;
                i = lineStart;
                continue;
            }

            if (lineEnd < len) {
                int space = text.substring(lineStart, lineEnd).lastIndexOf(' ');
                if (space > 0) {
                    lineEnd = lineStart + space;
                }
            }

            result.lines.add(text.substring(lineStart, lineEnd));
            result.lineStarts.add(lineStart);

            if (lineEnd < len && text.charAt(lineEnd) == ' ') {
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

        // Caret pos mapping
        for (int r = 0; r < result.lines.size(); r++) {
            int start = result.lineStarts.get(r);
            int end = start + result.lines.get(r).length();
            if (cursorPosition >= start && cursorPosition <= end) {
                result.caretRow = r;
                result.caretCol = cursorPosition - start;
                break;
            }
        }

        return result;
    }
}
