package fasttui.composable.todo;

import fastterminal.FastTerminalScene;
import fasttui.component.Component;

public class CheckBox extends Component {
    private String label;
    private boolean checked;
    private boolean isHovered = false;
    private Runnable onChange;

    public CheckBox(int x, int y, String label, boolean checked, Runnable onChange) {
        super(x, y, label.length() + 4, 1);
        this.label = label;
        this.checked = checked;
        this.onChange = onChange;
    }

    @Override
    public void render(FastTerminalScene scene) {
        if (!visible) return;
        String box = checked ? "[X]" : "[ ]";
        String txt = box + " " + label;

        int currentBg = isHovered ? 0x333333 : backgroundColor;
        for (int i = 0; i < width; i++) {
            char ch = (i < txt.length()) ? txt.charAt(i) : ' ';
            scene.writeCell(x + i, y, ch, foregroundColor, currentBg);
        }
    }

//    @Override
//    protected void onPress() {
//        checked = !checked;
//        if (onChange != null) onChange.run();
//    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void setLabel(String label) {
        this.label = label;
        this.width = label.length() + 4;
    }
}
