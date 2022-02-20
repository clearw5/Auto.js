package com.tony.natives;

public class NativeLib {

    // Used to load the 'natives' library on application startup.
    static {
        System.loadLibrary("natives");
    }

    /**
     * A native method that is implemented by the 'natives' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}