package fasttui.Demo;

import fastansi.FastANSI;
import fastkeyboard.FastKeyboard;
import fastkeyboard.FastKeyboardImpl;
import fastterminal.FastTerminal;
import fastterminal.FastTerminalRenderer;
import fastterminal.FastTerminalScene;
import fastterminal.FastTerminalSceneStreamer;
import fasttui.component.Component;
import fasttui.component.Container;

public class Runner {

    public static void run(Config config, Component component) {
        if (config.mode.equals("stream")) {
            runStream(config, component);
        } else {
            runScene(config, component);
        }
    }

    private static void runStream(Config cfg, Component component) {
        FastTerminalScene scene = new FastTerminalScene(0, 0, cfg.termCols, component.getHeight());
        component.render(scene);
        System.out.print(FastTerminalSceneStreamer.sceneToAnsiStream(scene));
    }

    private static void runScene(Config cfg, Component component) {
        System.out.print(FastANSI.ALT_BUFFER_ON + FastANSI.CURSOR_HIDE);

        int[] size = FastTerminal.getTerminalSize();
        int cols = size[0];
        int rows = size[1];

        FastTerminalRenderer renderer = new FastTerminalRenderer(cols, rows);
        FastTerminalScene scene = new FastTerminalScene(0, 0, cols, rows);
        renderer.addScene(scene);

        Container panel = new Container(0, 0, cols, rows);
        panel.setBackgroundColor(-1);
        panel.add(component);

        FastKeyboard keyboard = new FastKeyboardImpl();
        keyboard.startListening((h, vKey, mc, pressed, e0, ts, ch) -> {
            if (pressed && vKey == 0x1B) Config.isRunning = false;
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.print(FastANSI.ALT_BUFFER_OFF + FastANSI.CURSOR_SHOW + FastANSI.RESET);
            // FastTerminal.setSystemCursorVisible(true);
            keyboard.stopListening();
        }));

        while (Config.isRunning) {
            // scene.clear();
            panel.render(scene);
            renderer.render();
            try {
                Thread.sleep(16);
            } catch (InterruptedException ignored) {
            }
        }

        System.exit(0);
    }
}
