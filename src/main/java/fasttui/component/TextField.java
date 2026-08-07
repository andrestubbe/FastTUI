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
        scene.writeString(getAbsoluteX(), getAbsoluteY(), text, this.foregroundColor, this.backgroundColor == -1 ? -2 : this.backgroundColor);
    }

    public void setText(final String text) {
        this.text = text != null ? text : "";
        int visWidth = 0;
        for (int i = 0; i < this.text.length(); ) {
            int cp = this.text.codePointAt(i);
            visWidth += fastemojis.FastEmojis.getWidth(cp);
            i += Character.charCount(cp);
        }
        this.width = visWidth;
    }

}
