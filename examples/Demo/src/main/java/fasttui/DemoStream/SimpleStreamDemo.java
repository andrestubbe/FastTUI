package fasttui.DemoStream;

import fastterminal.FastTerminalScene;
import fastterminal.FastTerminalSceneStreamer;
import fasttui.component.Box;
import fasttui.component.TextArea;
import fasttui.component.BorderStyle;

public class SimpleStreamDemo {
    public static void main(String[] args) {
        int width = 45;
        int height = 6;
        FastTerminalScene scene = new FastTerminalScene(0, 0, width, height);

        // Create the box
        Box box = new Box(0, 0, width, height);
        box.setBorderStyle(BorderStyle.ROUNDED);
        box.setForegroundColor(0x7dcfff); // Border Color (Tokyo Night Cyan)
        box.setBackgroundColor(0x1a1b26); // Background Color (Tokyo Night BG)

        // Add the label (centered on the top border)
        TextArea label = new TextArea(2, 0, width - 4, 1);
        label.setText(" Tokyo Night ");
        label.setForegroundColor(0xf7768e); // Label Color (Tokyo Night Red)
        label.setBackgroundColor(0x1a1b26);
        box.add(label);

        // Add the content text (with 1-line vertical padding)
        TextArea content = new TextArea(1, 1, width - 2, height - 2);
        content.setPaddingX(1);
        content.setPaddingY(1);
        content.setText("Welcome to the new Box!\nLabel and text are composed.");
        content.setForegroundColor(0xc0caf5); // Text Color (Tokyo Night FG)
        content.setBackgroundColor(0x1a1b26);
        box.add(content);

        // Render to the terminal scene
        box.render(scene);

        // Output the ANSI stream to terminal
        System.out.print(FastTerminalSceneStreamer.sceneToAnsiStream(scene));
        System.out.println();
    }
}
