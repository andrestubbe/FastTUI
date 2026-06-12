package fasttui;

import fasttui.FastTUI;

/**
 * Basic Hello World Demo for FastTUI.
 */
public class Demo {
    public static void main(String[] args) {
        System.out.println("=== FastTUI Demo ===");
        
        FastTUI api = new FastTUI();
        
        System.out.println("Calling native method...");
        api.doSomethingNative();
        
        System.out.println("=== Demo Complete ===");
    }
}
