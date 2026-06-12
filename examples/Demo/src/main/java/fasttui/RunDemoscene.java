package fasttui;

import fastansi.FastANSI;
import fastkeyboard.FastKeyboard;
import fastkeyboard.FastKeyboardImpl;
import fastkeyboard.FastKeyboardListener;
import fastterminal.FastTerminal;
import fastterminal.FastTerminalRenderer;
import fastterminal.FastTerminalScene;
import fasttui.component.Panel;
import fasttui.demoscene.DemosceneEffect;
import fasttui.demoscene.effects.*;

import java.util.ArrayList;
import java.util.List;

public class RunDemoscene {
    private static volatile boolean running = true;
    private static volatile int currentEffectIndex = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("Starting FastTUI Demoscene...");

        System.out.print(FastANSI.ALT_BUFFER_ON + FastANSI.CURSOR_HIDE);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.print(FastANSI.ALT_BUFFER_OFF + FastANSI.CURSOR_SHOW + FastANSI.RESET);
        }));

        int cols = 100, rows = 30;
        int[] size = FastTerminal.getTerminalSize();
        if (size != null && size[0] > 0 && size[1] > 0) {
            cols = size[0];
            rows = size[1];
        }

        FastTerminalRenderer renderer = new FastTerminalRenderer(cols, rows);
        FastTerminalScene canvas = new FastTerminalScene(0, 0, cols, rows);
        renderer.addScene(canvas);

        FastKeyboard keyboard = new FastKeyboardImpl();
        keyboard.startListening(new FastKeyboardListener() {
            @Override
            public void onKeyEvent(long timestamp, int keyCode, int charCode, boolean keyDown, boolean keyUp, long repeatCount, String keyName) {
                if (keyDown) {
                    if (keyCode == 27 || charCode == 'q' || charCode == 'Q') {
                        running = false;
                    } else if (keyCode == 39 || charCode == 'n') { // Right arrow
                        currentEffectIndex++;
                    } else if (keyCode == 37 || charCode == 'p') { // Left arrow
                        currentEffectIndex--;
                    }
                }
            }
        });

        List<DemosceneEffect> effects = new ArrayList<>();
        effects.add(new IntroEffect());
        effects.add(new MatrixRainEffect());
        effects.add(new CubeEffect());
        effects.add(new DoomFireEffect());
        effects.add(new AttractorEffect());
        effects.add(new AsciiTunnelEffect());
        
        for (DemosceneEffect effect : effects) {
            effect.init(cols, rows);
        }

        Panel uiPanel = new Panel(2, 2, 38, 12, 0x1E1E2E);
        uiPanel.setBorderStyle(Panel.BorderStyle.DOUBLE);
        uiPanel.setBorderFg(0x89B4FA);
        uiPanel.setTitle(" FastTUI Demoscene ");

        long startTime = System.currentTimeMillis();
        long lastTime = startTime;

        while (running) {
            long now = System.currentTimeMillis();
            double time = (now - startTime) / 1000.0;
            double deltaTime = (now - lastTime) / 1000.0;
            lastTime = now;

            // Handle wrapping
            if (currentEffectIndex >= effects.size()) currentEffectIndex = 0;
            if (currentEffectIndex < 0) currentEffectIndex = effects.size() - 1;

            DemosceneEffect activeEffect = effects.get(currentEffectIndex);

            // 1. Draw effect to background
            activeEffect.update(time, deltaTime);
            activeEffect.render(canvas);

            // 2. Draw FastTUI Panel over it
            uiPanel.render(canvas);
            
            // UI Content
            canvas.writeString(4, 4, "Currently Playing:", 0xCDD6F4, uiPanel.getBgColor());
            canvas.writeString(4, 5, "[ " + activeEffect.getClass().getSimpleName() + " ]", 0xF38BA8, uiPanel.getBgColor());
            
            canvas.writeString(4, 7, "Controls:", 0xA6E3A1, uiPanel.getBgColor());
            canvas.writeString(4, 8, " [LEFT/RIGHT] Change Effect", 0xBAC2DE, uiPanel.getBgColor());
            canvas.writeString(4, 9, " [Q] Quit Demo", 0xBAC2DE, uiPanel.getBgColor());
            
            canvas.writeString(4, 11, String.format("Effect %d of %d", currentEffectIndex + 1, effects.size()), 0x89DCEB, uiPanel.getBgColor());

            renderer.render();
            Thread.sleep(16);
        }
        
        keyboard.stopListening();
        System.exit(0);
    }
}
