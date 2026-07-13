package fasttui.Demo;

import fastterminal.FastTerminal;

public class CommandParser {

    public static Config parse(String[] args) {
        if (args.length == 0) return null;
        Config config = new Config();
        config.termCols = FastTerminal.getTerminalSize()[0];

        String firstArg = args[0];
        if (firstArg.contains("new Box") || firstArg.contains("Box ") || firstArg.contains("Text ") || firstArg.contains("box.add")) {
            config.mode = "stream";
            config.componentName = "code";
            config.extraArg = firstArg;
            return config;
        }

        if (firstArg.equalsIgnoreCase("code")) {
            config.mode = "stream";
            config.componentName = "code";
            config.extraArg = args.length > 1 ? args[1] : "";
            return config;
        }

        config.mode = "stream";
        config.componentName = args[0].toLowerCase();
        config.extraArg = args.length > 1 ? args[1] : "";
        if (config.componentName.equals("stream") || config.componentName.equals("scene")) {
            config.mode = config.componentName;
            config.componentName = args.length > 1 ? args[1].toLowerCase() : "";
            config.extraArg = args.length > 2 ? args[2] : "";
        }

        if (config.componentName.equals("code") || config.extraArg.contains("new Box") || config.extraArg.contains("Box ") || config.extraArg.contains("box.add")) {
            config.componentName = "code";
            if (config.extraArg.isEmpty()) {
                config.extraArg = args.length > 2 ? args[2] : (args.length > 1 ? args[1] : "");
            }
        }

        return config;
    }
}
