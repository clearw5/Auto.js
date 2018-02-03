package com.stardust.autojs.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.stardust.R;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.enhancedfloaty.util.FloatingWindowPermissionUtil;
import com.stardust.lang.ThreadCompat;

import ezy.assist.compat.SettingsCompat;

/**
 * Created by Stardust on 2018/1/30.
 */

public class FloatingPermission {


    public static void ensurePermissionGranted(Context context) {
        if (!SettingsCompat.canDrawOverlays(context)) {
            Toast.makeText(context, R.string.text_no_floating_window_permission, Toast.LENGTH_SHORT).show();
            manageDrawOverlays(context);
            return;
        }
    }

    public static void waitForPermissionGranted(Context context) throws InterruptedException {
        if (SettingsCompat.canDrawOverlays(context)) {
            return;
        }
        Runnable r = () -> {
            manageDrawOverlays(context);
            Toast.makeText(context, R.string.text_no_floating_window_permission, Toast.LENGTH_SHORT).show();
        };
        if (Looper.myLooper() != Looper.getMainLooper()) {
            new Handler(Looper.getMainLooper()).post(r);
        } else {
            r.run();
        }
        while (true) {
            if (SettingsCompat.canDrawOverlays(context))
                return;
            Thread.sleep(200);
        }

    }


    public static void manageDrawOverlays(Context context) {
        try {
            SettingsCompat.manageDrawOverlays(context);
        } catch (Exception ex) {
            FloatingWindowPermissionUtil.goToAppDetailSettings(context, context.getPackageName());
        }
    }


}
