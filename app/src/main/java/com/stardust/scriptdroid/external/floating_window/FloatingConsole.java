package com.stardust.scriptdroid.external.floating_window;

import android.content.Context;

import com.stardust.enhancedfloaty.ResizableFloatyService;

/**
 * Created by Stardust on 2017/4/18.
 */

public class FloatingConsole {

    public static void startFloatingService(Context context) {
        ResizableFloatyService.startService(context, new ConsoleFloaty());
    }
}
