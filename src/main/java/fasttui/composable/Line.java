package fasttui.composable;

import fastterminal.FastTerminalScene;
import fasttui.component.Component;
import fasttui.component.BorderStyle;

public class Line extends Component {
    private BorderStyle borderStyle = BorderStyle.SINGLE;

    public Line(int x, int y, int width) {
        super(x, y, width, 1);
        this.foregroundColor = 0xFFFFFF;
        this.backgroundColor = -1;
    }

    public void setBorderStyle(BorderStyle style) {
        this.borderStyle = style;
    }

    @Override
    public void render(FastTerminalScene scene) {
        if (!visible || width <= 0) return;
        
        char lineChar = borderStyle.horizontalTop;
        for (int i = 0; i < width; i++) {
            scene.writeCell(x + i, y, lineChar, foregroundColor, backgroundColor);
        }
    }
}
