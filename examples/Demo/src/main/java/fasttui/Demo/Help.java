package fasttui.Demo;

public final class Help {

    private Help() {
    }

    public static void print() {
        System.out.println();
        System.out.println("FastTUI Demo");
        System.out.println("============");
        System.out.println("Usage:");
        System.out.println("  run-demo [stream|scene] [component] \"params...\"");
        System.out.println();
        System.out.println("Components:");
        System.out.println("  line         Simple horizontal line");
        System.out.println("  box          Box with border");
        System.out.println("  contentbox   Box with text content");
        System.out.println("  loader       Simple loader line");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  run-demo stream box \"size=40x8 style=half_block bg=0x18181b\"");
        System.out.println("  run-demo scene contentbox \"size=45x5 style=double fg=0xef4444 bg=0x0f172a\"");
        System.out.println();
        System.out.println("Notes:");
        System.out.println("  - Default mode is 'stream'");
        System.out.println("  - Parameters are space-separated key=value pairs");
        System.out.println();
    }
}
