package com.stardust.scriptdroid.external.floating_window;

import android.content.Intent;
import android.view.View;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.layout_inspector.view.LayoutInspectView;
import com.stardust.view.Floaty;
import com.stardust.view.ResizableFloaty;

/**
 * Created by Stardust on 2017/3/10.
 */

public class FloatingWindowManger {

    public static void showFloatingWindow() {
        if (!HoverMenuService.isServiceRunning())
            App.getApp().startService(new Intent(App.getApp(), HoverMenuService.class));
    }

    public static boolean isFloatingWindowShowing() {
        return HoverMenuService.isServiceRunning();
    }


    public static void hideFloatingWindow() {
        if (HoverMenuService.isServiceRunning())
            HoverMenuService.stopService();
    }
}
