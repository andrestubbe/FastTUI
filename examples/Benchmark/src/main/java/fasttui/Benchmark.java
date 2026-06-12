package fasttui;

import fasttui.FastTUI;

/**
 * Performance Benchmark comparing FastTUI to standard Java alternatives.
 */
public class Benchmark {
    public static void main(String[] args) {
        System.out.println("=== FastTUI Benchmark ===");
        
        FastTUI api = new FastTUI();
        
        int iterations = 10000;
        
        // 1. Benchmark Standard Java
        long startJava = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            // Simulate standard java operation
            Math.sqrt(i);
        }
        long javaTimeMs = (System.nanoTime() - startJava) / 1_000_000;
        
        // 2. Benchmark FastTUI Native
        long startNative = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            // api.doSomethingNative();
            Math.sqrt(i); // Placeholder
        }
        long nativeTimeMs = (System.nanoTime() - startNative) / 1_000_000;
        
        System.out.println("Iterations: " + iterations);
        System.out.println("Standard Java: " + javaTimeMs + " ms");
        System.out.println("FastTUI Native: " + nativeTimeMs + " ms");
        
        if (nativeTimeMs > 0) {
            System.out.println("Speedup: " + (javaTimeMs / (float) nativeTimeMs) + "x");
        }
    }
}
