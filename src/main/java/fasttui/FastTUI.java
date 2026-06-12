package fasttui;

import fastcore.FastCore;

/**
 * FastTUI Main API Class.
 * Native Windows capabilities exposed via JNI.
 */
public class FastTUI {

    // Load the native library once upon class initialization
    static {
        FastCore.loadLibrary("fasttui");
    }

    /**
     * Executes the native capability via C++.
     */
    public native void doSomethingNative();

}
