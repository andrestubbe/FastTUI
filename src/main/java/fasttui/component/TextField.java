package fasttui.component;

import fastterminal.FastTerminalScene;

public class TextField extends Component {

    private final int fgColor;
    private String text = "";

    public TextField(int x, int y, int fgColor) {
        super(x, y, 1, 1);
        this.fgColor = fgColor;
    }

    public TextField(int x, int y, String text, int fgColor) {
        super(x, y, 1, 1);
        this.fgColor = fgColor;
        this.setText(text);
    }

    @Override
    public void render(FastTerminalScene scene) {
        scene.writeString(x, y, text, fgColor, -1);
    }

    public void setText(String text) {
        this.text = text;
        this.width = this.text.length();
    }


}
