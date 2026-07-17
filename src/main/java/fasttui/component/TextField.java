package fasttui.component;

import fastterminal.FastTerminalScene;

public class TextField extends Component {

    private String text = "";

    public TextField(final int x, final int y, final int foregroundColor) {
        super(x, y, 1, 1);
        this.foregroundColor = foregroundColor;
    }

    public TextField(final int x, final int y, final String text, final int foregroundColor) {
        super(x, y, 1, 1);
        this.foregroundColor = foregroundColor;
        this.setText(text);
    }

    public TextField(final int x, final int y, final String text) {
        super(x, y, 1, 1);
        this.setText(text);
    }

    public TextField(final int x, final int y, final int backgroundColor, final int foregroundColor) {
        super(x, y, 1, 1);
        this.backgroundColor = backgroundColor;
        this.foregroundColor = foregroundColor;
    }

    public TextField(final int x, final int y, final String text, final int backgroundColor, final int foregroundColor) {
        super(x, y, 1, 1);
        this.backgroundColor = backgroundColor;
        this.foregroundColor = foregroundColor;
        this.setText(text);
    }

    @Override
    public void render(final FastTerminalScene scene) {
        scene.writeString(x, y, text, this.foregroundColor, this.backgroundColor);
    }

    public void setText(final String text) {
        this.text = text;
        this.width = this.text.length();
    }

}
