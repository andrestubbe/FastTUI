package fasttui.Demo;

import fasttui.component.*;
import fasttui.composable.Line;

public class ComponentFactory {

    public static Component create(Config config) {
        switch (config.componentName) {
            case "loader":
                return createLoader(config);
            case "line":
                return createLine(config);
            case "box":
            case "contentbox":
            case "labelbox":
                return createBox(config);
            case "code":
                return compileAndCreate(config.extraArg);
            default:
                return null;
        }
    }

    private static Component createLoader(Config config) {
        return new Line(0, 0, config.termCols);
    }

    private static Component createLine(Config config) {
        int width = ParamParser.getInt(config.extraArg, "width", config.termCols);
        Line line = new Line(0, 0, width);
        line.setBorderStyle(ParamParser.getStyle(config.extraArg, BorderStyle.SINGLE));
        line.setForegroundColor(ParamParser.getInt(config.extraArg, "fg", 0xFFFFFF));
        line.setBackgroundColor(ParamParser.getInt(config.extraArg, "bg", -1));
        return line;
    }

    private static Component createBox(Config config) {
        int[] size = ParamParser.getSize(config.extraArg, config.termCols, 5);
        Box box = new Box(0, 0, size[0], size[1]);
        
        BorderStyle style = ParamParser.getStyle(config.extraArg, BorderStyle.SINGLE);
        box.setBorderStyle(style);
        
        int fg = ParamParser.getInt(config.extraArg, "fg", 0xFFFFFF);
        box.setForegroundColor(fg);
        box.setBackgroundColor(ParamParser.getInt(config.extraArg, "bg", -1));
        
        int border = ParamParser.getInt(config.extraArg, "border", -1);
        if (border != -1) box.setBorderColor(border);

        // 1. Add label if specified
        String labelVal = ParamParser.get(config.extraArg, "label", null);
        if (labelVal != null) {
            int labelX = (style == BorderStyle.NONE) ? 0 : 1;
            int labelY = 0;
            int labelW = Math.max(0, size[0] - 2);
            TextArea label = new TextArea(labelX, labelY, labelW, 1);
            
            int labelForeground = ParamParser.getInt(config.extraArg, "labelForeground", -1);
            if (labelForeground == -1) {
                labelForeground = ParamParser.getInt(config.extraArg, "labeForeground", -1);
            }
            label.setForegroundColor(labelForeground != -1 ? labelForeground : (border != -1 ? border : fg));
            
            int labelBackground = ParamParser.getInt(config.extraArg, "labelBackground", -1);
            label.setBackgroundColor(labelBackground != -1 ? labelBackground : box.getBackgroundColor());
            
            label.setText(labelVal.replace("_", " ").replace("\\n", "\n"));
            box.add(label);
        }

        // 2. Add text if specified
        String textVal = ParamParser.get(config.extraArg, "text", null);
        if (textVal != null) {
            int textOffsetLeft = (style == BorderStyle.NONE) ? 0 : 1;
            int textOffsetRight = (style == BorderStyle.NONE) ? 0 : 1;
            int textOffsetTop = (labelVal != null) ? 1 : ((style == BorderStyle.NONE) ? 0 : 1);
            int textOffsetBottom = (style == BorderStyle.NONE) ? 0 : 1;
            
            int textW = Math.max(0, size[0] - textOffsetLeft - textOffsetRight);
            int textH = Math.max(0, size[1] - textOffsetTop - textOffsetBottom);
            
            TextArea textarea = new TextArea(textOffsetLeft, textOffsetTop, textW, textH);
            
            int padX = ParamParser.getInt(config.extraArg, "padX", 0);
            int padY = ParamParser.getInt(config.extraArg, "padY", 0);
            textarea.setPaddingX(padX);
            textarea.setPaddingY(padY);
            
            int contentFg = ParamParser.getInt(config.extraArg, "contentForeground", -1);
            textarea.setForegroundColor(contentFg != -1 ? contentFg : fg);
            textarea.setBackgroundColor(box.getBackgroundColor());
            
            textarea.setText(textVal.replace("_", " ").replace("\\n", "\n"));
            box.add(textarea);
        }

        return box;
    }

    private static Component compileAndCreate(String code) {
        try {
            String processedCode = replaceSingleQuotes(code);
            StringBuilder sb = new StringBuilder();
            sb.append("package fasttui.Demo;\n");
            sb.append("import fasttui.component.*;\n");
            sb.append("import fasttui.composable.*;\n");
            sb.append("import fasttui.composable.todo.*;\n");
            sb.append("import fasttui.layout.*;\n");
            sb.append("import java.util.Arrays;\n");
            sb.append("public class DynamicComponent {\n");
            sb.append("    public static Component create() {\n");
            
            sb.append(processedCode).append("\n");
            
            if (!processedCode.contains("return ")) {
                if (processedCode.contains("box")) {
                    sb.append("        return box;\n");
                } else if (processedCode.contains("container")) {
                    sb.append("        return container;\n");
                } else {
                    sb.append("        return null;\n");
                }
            }
            sb.append("    }\n");
            sb.append("}\n");

            String source = sb.toString();

            java.io.File targetDir = new java.io.File("target/generated-sources");
            if (!targetDir.exists()) targetDir.mkdirs();
            java.io.File javaFile = new java.io.File(targetDir, "DynamicComponent.java");
            java.nio.file.Files.writeString(javaFile.toPath(), source);

            javax.tools.JavaCompiler compiler = javax.tools.ToolProvider.getSystemJavaCompiler();
            if (compiler == null) {
                System.err.println("❌ JDK System Java Compiler not found. Make sure you are running with a JDK, not a JRE.");
                return null;
            }

            java.io.File classesDir = new java.io.File("target/classes");
            if (!classesDir.exists()) classesDir.mkdirs();

            int result = compiler.run(null, null, null,
                "-d", classesDir.getAbsolutePath(),
                "-cp", System.getProperty("java.class.path"),
                javaFile.getAbsolutePath()
            );

            if (result != 0) {
                System.err.println("❌ Compilation failed.");
                return null;
            }

            java.net.URLClassLoader classLoader = new java.net.URLClassLoader(
                new java.net.URL[]{classesDir.toURI().toURL()},
                ComponentFactory.class.getClassLoader()
            );
            Class<?> clazz = classLoader.loadClass("fasttui.Demo.DynamicComponent");
            java.lang.reflect.Method method = clazz.getMethod("create");
            Component comp = (Component) method.invoke(null);
            classLoader.close();
            return comp;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String replaceSingleQuotes(String code) {
        StringBuilder sb = new StringBuilder();
        int len = code.length();
        for (int i = 0; i < len; i++) {
            char c = code.charAt(i);
            if (c == '\'') {
                if (i + 2 < len && code.charAt(i + 2) == '\'') {
                    char middle = code.charAt(i + 1);
                    if (Character.isLetterOrDigit(middle)) {
                        sb.append('"');
                        sb.append(middle);
                        sb.append('"');
                    } else {
                        sb.append('\'');
                        sb.append(middle);
                        sb.append('\'');
                    }
                    i += 2;
                    continue;
                }
                if (i + 3 < len && code.charAt(i + 1) == '\\' && code.charAt(i + 3) == '\'') {
                    sb.append(c);
                    sb.append(code.charAt(i + 1));
                    sb.append(code.charAt(i + 2));
                    sb.append('\'');
                    i += 3;
                    continue;
                }
                sb.append('"');
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
