package fasttui.composable.todo;

import fastterminal.FastTerminalScene;
import fasttui.component.Component;

public class RadioButton extends Component {
    private String label;
    private boolean selected;
    private boolean isHovered = false;
    private RadioGroup group;

    public RadioButton(int x, int y, String label, RadioGroup group, boolean selected) {
        super(x, y, label.length() + 4, 1);
        this.label = label;
        this.group = group;
        this.selected = selected;
        if (group != null) {
            group.add(this);
            if (selected) group.select(this);
        }
    }

    @Override
    public void render(FastTerminalScene scene) {
        if (!visible) return;
        String box = selected ? "(o)" : "( )";
        String txt = box + " " + label;

        int currentBg = isHovered ? 0x333333 : backgroundColor;
        for (int i = 0; i < width; i++) {
            char ch = (i < txt.length()) ? txt.charAt(i) : ' ';
            scene.writeCell(x + i, y, ch, foregroundColor, currentBg);
        }
    }

//    @Override
//    protected void onPress() {
//        if (!selected && group != null) {
//            group.select(this);
//        } else if (!selected) {
//            selected = true;
//        }
//    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }
}
