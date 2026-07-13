package fasttui;

import fastkeyboard.FastKeyboard;
import fastkeyboard.FastKeyboardImpl;
import fastterminal.FastTerminal;
import fastterminal.FastTerminalScene;
import fastterminal.FastTerminalSceneStreamer;
import fasttui.component.Box;
import fasttui.component.Text;
import fasttui.composable.todo.Dropdown;

import java.io.IOException;
import java.util.Arrays;

public class InteractiveStreamingDemo {

    private static volatile boolean isRunning = true;
    private static volatile boolean needsRender = true;

    public static void main(String[] args) throws IOException {
        System.out.println("User: Please select a model.");
        System.out.println("Agent Goal: Provide an interactive dropdown inline.");
        System.out.println();
        
        int cols = 80; 
        try {
            int[] size = FastTerminal.getTerminalSize();
            if (size != null && size[0] > 0) {
                cols = size[0];
            }
        } catch (Throwable ignored) {}

        int boxH = 4;
        FastTerminalScene boxScene = new FastTerminalScene(0, 0, cols, boxH);
        Box cbox = new Box(0, 0, cols, boxH);
        Text text = new Text(2, 1, cols - 4, boxH - 2);
        text.setText("Welcome to the unified Component Architecture!\nThis box and the dropdown below use the exact same Component base class.");
        cbox.add(text);
        cbox.render(boxScene);
        System.out.print(FastTerminalSceneStreamer.sceneToAnsiStream(boxScene));
        
        System.out.println("\n--- INLINE INTERACTIVE DROPDOWN ---");
        System.out.println("Use UP/DOWN arrows to select, ENTER to confirm.");

        int w = 50;
        int h = 8;
        FastTerminalScene miniScene = new FastTerminalScene(0, 0, w, h);
        
        Dropdown dropdown = new Dropdown(0, 0, 40, Arrays.asList("Llama 3 (8B)", "Llama 3 (70B)", "Mistral 7B", "Gemma 7B"), null);
        dropdown.setExpanded(true); 
        dropdown.setHoveredItemIndex(0);
        
        final FastKeyboard keyboard = new FastKeyboardImpl();
        keyboard.startListening((deviceHandle, vKey, makeCode, isPressed, isE0, timestamp, keyCharString) -> {
            if (isPressed) {
                if (!FastTerminal.isTerminalFocused()) return;
                
                char keyChar = (keyCharString != null && keyCharString.length() > 0) ? keyCharString.charAt(0) : 0;
                
                if (vKey == 0x26) { // Up
                    dropdown.handleKey(vKey, keyChar);
                    needsRender = true;
                } else if (vKey == 0x28) { // Down
                    dropdown.handleKey(vKey, keyChar);
                    needsRender = true;
                } else if (vKey == 0x0D || keyChar == '\r' || keyChar == '\n') { // Enter
                    dropdown.handleKey(0x0D, '\n');
                    isRunning = false;
                } else if (vKey == 0x1B) { // Esc
                    isRunning = false;
                }
            }
        });

        // Hide system cursor to make interaction look clean
        System.out.print("\033[?25l");
        
        boolean firstRender = true;
        
        while (isRunning) {
            if (needsRender) {
                needsRender = false;
                
                for (int r = 0; r < h; r++) {
                    for (int c = 0; c < w; c++) {
                        miniScene.writeCell(c, r, ' ', 0xFFFFFF, -1);
                    }
                }
                
                dropdown.render(miniScene);
                String ansi = FastTerminalSceneStreamer.sceneToAnsiStream(miniScene);
                
                if (!firstRender) {
                    System.out.print("\033[" + h + "A");
                }
                firstRender = false;
                
                System.out.print(ansi);
            }
            
            try {
                Thread.sleep(16); // 60fps polling
            } catch (InterruptedException e) {}
        }
        
        keyboard.stopListening();
        
        // Show cursor again
        System.out.print("\033[?25h");
        
        // Final render (closed dropdown)
        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) {
                miniScene.writeCell(c, r, ' ', 0xFFFFFF, -1);
            }
        }
        dropdown.render(miniScene);
        String finalAnsi = FastTerminalSceneStreamer.sceneToAnsiStream(miniScene);
        System.out.print("\033[" + h + "A");
        System.out.print(finalAnsi);
        
        System.out.println("Agent: You selected Model index " + dropdown.getSelectedIndex() + ".");
    }
}
