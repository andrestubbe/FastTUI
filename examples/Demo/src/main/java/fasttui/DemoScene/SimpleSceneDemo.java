package fasttui.DemoScene;

import fastansi.FastANSI;
import fastkeyboard.FastKeyboard;
import fastkeyboard.FastKeyboardImpl;
import fastmouse.FastMouseListener;
import fastterminal.AnsiMouse;
import fastterminal.FastTerminal;
import fastterminal.FastTerminalRenderer;
import fastterminal.FastTerminalScene;
import fasttui.behaviour.EventDispatcher;
import fasttui.component.BorderStyle;
import fasttui.component.Box;
import fasttui.component.Container;
import fasttui.component.Text;
import fasttui.composable.Button;
import fasttui.composable.Table;
import fasttui.composable.todo.Dropdown;

import java.util.Arrays;
import java.util.List;

public class SimpleSceneDemo {
    private static volatile boolean isRunning = true;

    public static void main(String[] args) {
        // Initialize full-screen interactive Terminal Scene
        System.out.print(FastANSI.ALT_BUFFER_ON + FastANSI.CURSOR_HIDE);

        int[] size = FastTerminal.getTerminalSize();
        int cols = size[0];
        int rows = size[1];

        FastTerminalRenderer renderer = new FastTerminalRenderer(cols, rows);
        FastTerminalScene scene = new FastTerminalScene(0, 0, cols, rows);
        renderer.addScene(scene);

        // Root container covering the terminal
        Container container = new Container(0, 0, cols, rows);

        // Commented out as requested:
        // setupFloatingBox(container);

        // Render Table:
        setupTable(container);

        // Interactive rendering loop
        while (isRunning) {
            scene.clear();
            container.render(scene);
            renderer.render();
            try {
                Thread.sleep(16); // ~60fps
            } catch (InterruptedException ignored) {
            }
        }

        System.exit(0);
    }

    private static void setupFloatingBox(Container container) {
        int cols = container.getWidth();
        int rows = container.getHeight();
        int boxW = 55;
        int boxH = 10;
        int boxX = (cols - boxW) / 2;
        int boxY = (rows - boxH) / 2;

        Box box = new Box(boxX, boxY, boxW, boxH);
        box.setBorderStyle(BorderStyle.ROUNDED);
        container.add(box);

        Text label = new Text(2, 0, boxW - 4, 1);
        label.setText("Selection");
        box.add(label);

        Text prompt = new Text(2, 2, boxW - 4, 1);
        prompt.setText("Choose an AI model and click Select:");
        box.add(prompt);

        Text status = new Text(2, 7, boxW - 4, 1);
        status.setText("Status: Waiting for selection...");
        box.add(status);

        List<String> models = Arrays.asList("Llama 3 (8B)", "Llama 3 (70B)", "Mistral 7B", "Gemma 7B");
        int dropdownX = 2;
        int dropdownY = 4;
        int dropdownW = 25;
        Dropdown dropdown = new Dropdown(dropdownX, dropdownY, dropdownW, 3, models, idx -> status.setText("Status: Selected " + models.get(idx)));
        box.add(dropdown);

        Button selectBtn = new Button(30, 4, 20, 3, "Select", () -> {
            int idx = dropdown.getSelectedIndex();
            status.setText("Selected: " + models.get(idx));
        });
        box.add(selectBtn);

        // Setup local events for Floating Box
        FastKeyboard keyboard = new FastKeyboardImpl();
        keyboard.startListening((h, vKey, mc, pressed, e0, ts, ch) -> {
            if (pressed) {
                if (vKey == 0x1B) { // ESC to exit
                    isRunning = false;
                }
                if (dropdown.isExpanded()) {
                    dropdown.handleKey(vKey, ch != null && ch.length() > 0 ? ch.charAt(0) : 0);
                }
            }
        });

        final int[] mouseCell = {-1, -1};
        AnsiMouse mouse = AnsiMouse.open(new FastMouseListener() {
            @Override
            public void onMouseMove(long deviceHandle, int deltaX, int deltaY, int absX, int absY) {
                mouseCell[0] = absX;
                mouseCell[1] = absY;
                EventDispatcher.dispatchMouseMove(container, absX, absY);
            }

            @Override
            public void onMouseButton(long deviceHandle, int buttonId, boolean isPressed) {
                if (buttonId == 0) {
                    EventDispatcher.dispatchMouseClick(container, mouseCell[0], mouseCell[1], isPressed);
                }
            }

            @Override
            public void onMouseWheel(long deviceHandle, int delta) {
                if (dropdown.isExpanded()) {
                    dropdown.handleMouseWheel(delta);
                }
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.print(FastANSI.ALT_BUFFER_OFF + FastANSI.CURSOR_SHOW + FastANSI.RESET);
            keyboard.stopListening();
            mouse.close();
        }));
    }

    private static void setupTable(Container container) {
        String[][] data = {
                {"1", "Alice", "Developer"},
                {"2", "Bob", "Designer"},
                {"3", "Charlie", "Product Mgr"},
                {"4", "Dave", "QA Engineer"},
                {"1", "Alice", "Developer"},
                {"2", "Bob", "Designer"},
                {"3", "Charlie", "Product Mgr"},
                {"4", "Dave", "QA Engineer"},
                {"1", "Alice", "Developer"},
                {"2", "Bob", "Designer"},
                {"3", "Charlie", "Product Mgr"},
                {"4", "Dave", "QA Engineer"},
                {"1", "Alice", "Developer"},
                {"2", "Bob", "Designer"},
                {"3", "Charlie", "Product Mgr"},
                {"4", "Dave", "QA Engineer"},
                {"5", "Eve", "DevOps"}
        };
        int[] columnWidths = {20, 20, 20, 20}; // Total width: 6+12+16+10 = 44

        int rows = data.length;
        int cols = columnWidths.length;

        int tableW = 80;
        int tableH = rows;
        int tableX = 2;
        int tableY = 2;

        Table table = new Table(tableX, tableY, tableW, tableH, rows, cols, columnWidths);
        table.setBorderStyle(BorderStyle.NONE); // No dividers (|)

        // Add a local status label just for the table demo
        Text tableStatus = new Text(5, container.getHeight() - 3, container.getWidth() - 10, 1);
        tableStatus.setText("Table Demo. Click any 'Action' button...");
        container.add(tableStatus);

        // Populate all cells from the outside
        for (int r = 0; r < rows; r++) {
            // Passive cells (Text)
            for (int c = 0; c < 3; c++) {
                Text txtCell = new Text(0, 0, columnWidths[c], 1);
                txtCell.setText(data[r][c]);
                txtCell.setPaddingX(1);
                table.setCell(r, c, txtCell);
            }

            // Active cell (Button)
            final int rowNum = r + 1;
            Button actionBtn = new Button(0, 0, columnWidths[3], 1, "Action " + rowNum, () -> {
                tableStatus.setText("Table Action: Executed on Row " + rowNum);
            });
            table.setCell(r, 3, actionBtn);
        }

        container.add(table);

        // Setup local events for Table
        FastKeyboard keyboard = new FastKeyboardImpl();
        keyboard.startListening((h, vKey, mc, pressed, e0, ts, ch) -> {
            if (pressed && vKey == 0x1B) { // ESC to exit
                isRunning = false;
            }
        });

        final int[] mouseCell = {-1, -1};
        AnsiMouse mouse = AnsiMouse.open(new FastMouseListener() {
            @Override
            public void onMouseMove(long deviceHandle, int deltaX, int deltaY, int absX, int absY) {
                mouseCell[0] = absX;
                mouseCell[1] = absY;
                EventDispatcher.dispatchMouseMove(container, absX, absY);
            }

            @Override
            public void onMouseButton(long deviceHandle, int buttonId, boolean isPressed) {
                if (buttonId == 0) {
                    EventDispatcher.dispatchMouseClick(container, mouseCell[0], mouseCell[1], isPressed);
                }
            }

            @Override
            public void onMouseWheel(long deviceHandle, int delta) {
                // No mouse wheel actions needed for table
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.print(FastANSI.ALT_BUFFER_OFF + FastANSI.CURSOR_SHOW + FastANSI.RESET);
            keyboard.stopListening();
            mouse.close();
        }));
    }
}
