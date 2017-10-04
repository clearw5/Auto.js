package com.stardust.scriptdroid.external.floatingwindow;

import android.content.Context;
import android.content.Intent;
import android.view.WindowManager;
import android.widget.Toast;

import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.enhancedfloaty.FloatyWindow;
import com.stardust.enhancedfloaty.util.FloatingWindowPermissionUtil;
import com.stardust.scriptdroid.R;

import ezy.assist.compat.SettingsCompat;

/**
 * Created by Stardust on 2017/9/30.
 */

public class FloatyWindowManger {

    public static void addWindow(Context context, FloatyWindow window) {
        context.startService(new Intent(context, FloatyService.class));
        if (!SettingsCompat.canDrawOverlays(context)) {
            Toast.makeText(context, R.string.text_no_floating_window_permission, Toast.LENGTH_SHORT).show();
            manageDrawOverlays(context);
            return;
        }
        try {
            FloatyService.addWindow(window);
            // SecurityException: https://github.com/hyb1996-guest/AutoJsIssueReport/issues/4781
        } catch (Exception e) {
            e.printStackTrace();
            manageDrawOverlays(context);
            Toast.makeText(context, R.string.text_no_floating_window_permission, Toast.LENGTH_SHORT).show();

        }
    }


    public static void manageDrawOverlays(Context context) {
        try {
            SettingsCompat.manageDrawOverlays(context);
        } catch (Exception ex) {
            FloatingWindowPermissionUtil.goToAppDetailSettings(context, context.getPackageName());
        }
    }


    public static void closeWindow(FloatyWindow window) {
        window.close();
    }
}
