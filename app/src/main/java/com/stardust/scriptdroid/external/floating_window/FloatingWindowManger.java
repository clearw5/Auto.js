package com.stardust.scriptdroid.external.floating_window;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.layout_inspector.view.LayoutInspectView;
import com.stardust.scriptdroid.tool.IntentTool;
import com.stardust.view.Floaty;
import com.stardust.view.ResizableFloaty;

/**
 * Created by Stardust on 2017/3/10.
 */

public class FloatingWindowManger {

    public static void showFloatingWindow() {
        if (!HoverMenuService.isServiceRunning()) {
            if (!hasFloatingWindowPermission()) {
                goToFloatingWindowPermissionSetting();
            } else {
                HoverMenuService.startService(App.getApp());
            }
        }
    }

    public static void goToFloatingWindowPermissionSetting() {
        String packageName = App.getApp().getPackageName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                App.getApp().startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + packageName))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            } catch (Exception e) {
                IntentTool.goToAppSetting(App.getApp());
            }
        } else {
            IntentTool.goToAppSetting(App.getApp());
        }
    }

    private static boolean hasFloatingWindowPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(App.getApp());
        }
        return true;
    }

    public static boolean isFloatingWindowShowing() {
        return HoverMenuService.isServiceRunning();
    }


    public static void hideFloatingWindow() {
        if (HoverMenuService.isServiceRunning())
            App.getApp().stopService(new Intent(App.getApp(), HoverMenuService.class));
    }
}
