package fasttui.Demo;

import fasttui.component.BorderStyle;

public class ParamParser {

    public static String get(String args, String key, String def) {
        if (args == null) return def;
        for (String p : args.split(" ")) {
            if (p.startsWith(key + "=")) return p.substring(key.length() + 1);
        }
        return def;
    }

    public static int getInt(String args, String key, int def) {
        String v = get(args, key, null);
        if (v == null) return def;
        try {
            if (v.startsWith("0x")) return Integer.decode(v);
            return Integer.parseInt(v);
        } catch (Exception e) {
            return def;
        }
    }

    public static BorderStyle getStyle(String args, BorderStyle def) {
        String v = get(args, "style", null);
        if (v == null) return def;
        try {
            return BorderStyle.valueOf(v.toUpperCase());
        } catch (Exception e) {
            return def;
        }
    }

    public static int[] getSize(String args, int defW, int defH) {
        String s = get(args, "size", null);
        if (s != null && s.contains("x")) {
            String[] p = s.split("x");
            try {
                return new int[]{Integer.parseInt(p[0]), Integer.parseInt(p[1])};
            } catch (Exception ignored) {
            }
        }
        return new int[]{defW, defH};
    }
}
