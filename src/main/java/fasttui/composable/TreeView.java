package fasttui.composable;

import fastterminal.FastTerminalScene;
import fasttui.component.Control;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TreeView extends Control {

    public static class TreeNode {
        private final File file;
        private final List<TreeNode> children = new ArrayList<>();
        private boolean expanded = false;
        private int depth = 0;

        public TreeNode(File file) {
            this.file = file;
        }

        public File getFile() {
            return file;
        }

        public List<TreeNode> getChildren() {
            return children;
        }

        public boolean isExpanded() {
            return expanded;
        }

        public void setExpanded(boolean expanded) {
            this.expanded = expanded;
        }

        public int getDepth() {
            return depth;
        }

        public void setDepth(int depth) {
            this.depth = depth;
        }

        public boolean isDirectory() {
            return file.isDirectory();
        }

        public boolean hasChildren() {
            return isDirectory() && !children.isEmpty();
        }
    }

    private TreeNode root;
    private final List<TreeNode> visibleNodes = new ArrayList<>();
    private int selectedIndex = 0;
    private int scrollOffset = 0;

    private int treeColorNormal = 0xCCCCCC;
    private int treeColorSelected = 0xFFFFFF;
    private int treeColorDirectory = 0xCCCCCC;
    private int treeColorFile = 0xCCCCCC;
    private int treeColorBackgroundSelected = 0x264F78;

    private static final HashMap<String, String> ICONS = new HashMap<>();
    static {
        ICONS.put("java", "[J]");
        ICONS.put("js", "[S]");
        ICONS.put("ts", "[T]");
        ICONS.put("py", "[P]");
        ICONS.put("md", "[M]");
        ICONS.put("txt", "[T]");
        ICONS.put("json", "[J]");
        ICONS.put("xml", "[X]");
        ICONS.put("html", "[H]");
        ICONS.put("css", "[C]");
        ICONS.put("png", "[I]");
        ICONS.put("jpg", "[I]");
        ICONS.put("jpeg", "[I]");
        ICONS.put("gif", "[I]");
        ICONS.put("svg", "[I]");
        ICONS.put("pdf", "[P]");
        ICONS.put("zip", "[Z]");
        ICONS.put("jar", "[Z]");
        ICONS.put("class", "[Z]");
    }

    private TreeSelectionListener selectionListener;

    public TreeView(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public void setRoot(File rootFile) {
        this.root = buildTree(rootFile, 0);
        root.setExpanded(true); // Auto-expand root
        refreshVisibleNodes();
    }

    public void expandPath(File targetPath) {
        if (root == null || targetPath == null) return;

        String targetPathStr = targetPath.getAbsolutePath();
        TreeNode targetNode = findNode(root, targetPathStr);

        if (targetNode != null) {
            expandToNode(root, targetNode);
            refreshVisibleNodes();
        }
    }

    private TreeNode findNode(TreeNode node, String targetPath) {
        if (node.getFile().getAbsolutePath().equals(targetPath)) {
            return node;
        }

        if (node.isDirectory()) {
            for (TreeNode child : node.getChildren()) {
                TreeNode found = findNode(child, targetPath);
                if (found != null) return found;
            }
        }

        return null;
    }

    private void expandToNode(TreeNode current, TreeNode target) {
        if (current == target) return;

        if (current.isDirectory()) {
            for (TreeNode child : current.getChildren()) {
                if (isAncestor(child, target)) {
                    current.setExpanded(true);
                    expandToNode(child, target);
                    break;
                }
            }
        }
    }

    private boolean isAncestor(TreeNode ancestor, TreeNode descendant) {
        String ancestorPath = ancestor.getFile().getAbsolutePath();
        String descendantPath = descendant.getFile().getAbsolutePath();
        return descendantPath.startsWith(ancestorPath);
    }

    private TreeNode buildTree(File file, int depth) {
        TreeNode node = new TreeNode(file);
        node.setDepth(depth);

        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    if (!child.isHidden()) {
                        node.getChildren().add(buildTree(child, depth + 1));
                    }
                }
            }
        }
        return node;
    }

    private void refreshVisibleNodes() {
        visibleNodes.clear();
        if (root != null) {
            collectVisibleNodes(root);
        }
    }

    private void collectVisibleNodes(TreeNode node) {
        visibleNodes.add(node);
        if (node.isExpanded() && node.hasChildren()) {
            for (TreeNode child : node.getChildren()) {
                collectVisibleNodes(child);
            }
        }
    }

    @Override
    public void render(FastTerminalScene scene) {
        if (!visible) return;
        if (visibleNodes.isEmpty()) return;

        int visibleHeight = height;
        int startIndex = Math.min(scrollOffset, visibleNodes.size() - 1);
        int endIndex = Math.min(startIndex + visibleHeight, visibleNodes.size());

        for (int i = startIndex; i < endIndex; i++) {
            TreeNode node = visibleNodes.get(i);
            int row = i - startIndex;
            boolean selected = (i == selectedIndex);

            int bg = selected ? treeColorBackgroundSelected : -1;
            int fg = selected ? treeColorSelected : (node.isDirectory() ? treeColorDirectory : treeColorFile);

            if (selected) {
                for (int c = x; c < x + width; c++) {
                    scene.writeCell(c, y + row, ' ', fg, bg);
                }
            }

            String prefix = buildPrefix(node);
            String name = node.getFile().getName();
            String display = prefix + name;

            if (display.length() > width) {
                display = display.substring(0, width);
            }

            scene.writeString(x, y + row, display, fg, bg);
        }
    }

    private String buildPrefix(TreeNode node) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < node.getDepth(); i++) {
            sb.append("  ");
        }
        if (node.isDirectory()) {
            sb.append(getIcon(node));
        } else {
            sb.append(getIcon(node));
        }
        return sb.toString();
    }

    private String getIcon(TreeNode node) {
        if (node.isDirectory()) {
            return node.isExpanded() ? "[+] " : "[ ] ";
        }
        File file = node.getFile();
        String name = file.getName();
        int dot = name.lastIndexOf('.');
        if (dot > 0 && dot < name.length() - 1) {
            String ext = name.substring(dot + 1).toLowerCase(Locale.ROOT);
            String icon = ICONS.get(ext);
            if (icon != null) return icon + " ";
        }
        return "[ ] ";
    }

    public void selectNext() {
        if (selectedIndex < visibleNodes.size() - 1) {
            selectedIndex++;
            ensureVisible();
        }
    }

    public void selectPrevious() {
        if (selectedIndex > 0) {
            selectedIndex--;
            ensureVisible();
        }
    }

    public void toggleExpanded() {
        if (selectedIndex >= 0 && selectedIndex < visibleNodes.size()) {
            TreeNode node = visibleNodes.get(selectedIndex);
            if (node.isDirectory()) {
                node.setExpanded(!node.isExpanded());
                refreshVisibleNodes();
            }
        }
    }

    public void activateSelected() {
        if (selectedIndex >= 0 && selectedIndex < visibleNodes.size()) {
            TreeNode node = visibleNodes.get(selectedIndex);
            if (selectionListener != null) {
                selectionListener.onNodeSelected(node);
            }
        }
    }

    private void ensureVisible() {
        int visibleHeight = height;
        if (selectedIndex < scrollOffset) {
            scrollOffset = selectedIndex;
        } else if (selectedIndex >= scrollOffset + visibleHeight) {
            scrollOffset = selectedIndex - visibleHeight + 1;
        }
    }

    public void setSelectionListener(TreeSelectionListener listener) {
        this.selectionListener = listener;
    }

    public void setTreeColorNormal(int color) {
        this.treeColorNormal = color;
    }

    public void setTreeColorSelected(int color) {
        this.treeColorSelected = color;
    }

    public void setTreeColorDirectory(int color) {
        this.treeColorDirectory = color;
    }

    public void setTreeColorFile(int color) {
        this.treeColorFile = color;
    }

    public void setTreeColorBackgroundSelected(int color) {
        this.treeColorBackgroundSelected = color;
    }

    public interface TreeSelectionListener {
        void onNodeSelected(TreeNode node);
    }
}
