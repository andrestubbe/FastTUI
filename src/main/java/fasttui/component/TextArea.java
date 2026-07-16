package fasttui.component;

import fastterminal.FastTerminalScene;
import fasttui.layout.TextLayoutEngine;

public class TextArea extends Component {

    private final TextLayoutEngine layoutEngine = new TextLayoutEngine();

    private String text = "";
    private int paddingX = 0;
    private int paddingY = 0;

    private String lastText = null;
    private int lastWidth = -1;

    public TextArea(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.foregroundColor = 0xFFFFFF;
        this.backgroundColor = -1;
    }

    @Override
    public void render(FastTerminalScene scene) {
        if (!visible || text == null || text.isEmpty()) return;
        int maxW = width - paddingX * 2;
        int maxH = height - paddingY * 2;

        if (!text.equals(lastText) || maxW != lastWidth) {
            layoutEngine.layout(text, maxW);
            lastText = text;
            lastWidth = maxW;
        }

        layoutEngine.render(scene,
                x + paddingX, y + paddingY,
                foregroundColor, backgroundColor,
                maxW, maxH);
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPaddingX(int px) {
        this.paddingX = px;
    }

    public void setPaddingY(int py) {
        this.paddingY = py;
    }
}
