package fasttui.Demo;

import fasttui.component.Component;

public class Demo {

    public static void main(String[] args) {
        Config config = CommandParser.parse(args);
        if (config == null) {
            Help.print();
            return;
        }
        Component component = ComponentFactory.create(config);
        if (component == null) {
            System.out.println("Unknown component: " + config.componentName);
            return;
        }
        Runner.run(config, component);
    }
}

