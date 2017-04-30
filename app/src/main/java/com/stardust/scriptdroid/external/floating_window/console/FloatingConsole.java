package com.stardust.scriptdroid.external.floating_window.console;

import android.content.Context;

import com.stardust.enhancedfloaty.ResizableExpandableFloatyService;
import com.stardust.enhancedfloaty.ResizableFloatyService;

/**
 * Created by Stardust on 2017/4/18.
 */

public class FloatingConsole {

    public static void startFloatingService(Context context) {
        ResizableExpandableFloatyService.startService(context, new ConsoleFloaty());
    }
}
