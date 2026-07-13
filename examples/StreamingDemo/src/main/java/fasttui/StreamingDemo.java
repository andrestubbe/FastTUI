package fasttui;

import fastterminal.FastTerminalScene;
import fastterminal.FastTerminalSceneStreamer;
import fastterminal.FastTerminal;

/**
 * Demonstrates how to use FastTUI components in a standard, stateless streaming CLI.
 */
public class StreamingDemo {

    public static void main(String[] args) {
        
        System.out.println("User: Show me the rounded box in streaming mode.");
        System.out.println("Agent Goal: Display a full-width rounded box.");
        System.out.println();
        
        // 1. Get terminal width
        int cols = 80; // default
        try {
            int[] size = FastTerminal.getTerminalSize();
            if (size != null && size[0] > 0) {
                cols = size[0];
            }
        } catch (Throwable ignored) {
        }
        
        // 2. Create a "Mini-Scene" specifically sized for the component
        int boxH = 4;
        FastTerminalScene miniScene = new FastTerminalScene(0, 0, cols, boxH);
        
        // 3. Draw the rounded box from Creation.java
        int boxX = 0;
        int boxY = 0;
        int boxW = cols;
        int color = 0x00A0FF; // light blue
        int bgColor = -1;

        for (int i = 1; i < boxW - 1; i++) {
            miniScene.writeCell(boxX + i, boxY, '─', color, bgColor);
            miniScene.writeCell(boxX + i, boxY + boxH - 1, '─', color, bgColor);
        }
        for (int i = 1; i < boxH - 1; i++) {
            miniScene.writeCell(boxX, boxY + i, '│', color, bgColor);
            miniScene.writeCell(boxX + boxW - 1, boxY + i, '│', color, bgColor);
        }
        miniScene.writeCell(boxX, boxY, '╭', color, bgColor);
        miniScene.writeCell(boxX + boxW - 1, boxY, '╮', color, bgColor);
        miniScene.writeCell(boxX, boxY + boxH - 1, '╰', color, bgColor);
        miniScene.writeCell(boxX + boxW - 1, boxY + boxH - 1, '╯', color, bgColor);

        String line1 = "Welcome to Gemini CLI!";
        String line2 = "I'm your AI assistant. How can I help you today?";
        for (int i = 0; i < line1.length(); i++) {
            miniScene.writeCell(boxX + 2 + i, boxY + 1, line1.charAt(i), 0xFFFFFF, bgColor);
        }
        for (int i = 0; i < line2.length(); i++) {
            miniScene.writeCell(boxX + 2 + i, boxY + 2, line2.charAt(i), 0xFFFFFF, bgColor);
        }
        
        // 4. Convert the scene to an ANSI string and print it!
        String ansiOutput = FastTerminalSceneStreamer.sceneToAnsiStream(miniScene);
        System.out.print(ansiOutput);
        
        System.out.println();
        System.out.println("Agent: Rendered the rounded box successfully.");
    }
}
