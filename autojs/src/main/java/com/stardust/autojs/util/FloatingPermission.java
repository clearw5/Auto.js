package com.stardust.autojs.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import androidx.annotation.RequiresApi;
import android.text.TextUtils;
import android.widget.Toast;

import com.stardust.autojs.R;
import com.stardust.enhancedfloaty.util.FloatingWindowPermissionUtil;

import java.lang.reflect.Method;

import ezy.assist.compat.RomUtil;
import ezy.assist.compat.SettingsCompat;

/**
 * Created by Stardust on 2018/1/30.
 */

public class FloatingPermission {


    private static final int OP_SYSTEM_ALERT_WINDOW = 24;
    private static Method sCheckOp;

    static {
        try {
            sCheckOp = SettingsCompat.class.getDeclaredMethod("checkOp", Context.class, int.class);
            sCheckOp.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static boolean ensurePermissionGranted(Context context) {
        if (!canDrawOverlays(context)) {
            Toast.makeText(context, R.string.text_no_floating_window_permission, Toast.LENGTH_SHORT).show();
            manageDrawOverlays(context);
            return false;
        }
        return true;
    }

    public static void waitForPermissionGranted(Context context) throws InterruptedException {
        if (canDrawOverlays(context)) {
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
            if (canDrawOverlays(context))
                return;
            Thread.sleep(200);
        }

    }


    public static void manageDrawOverlays(Context context) {
        try {
            if (RomUtil.isMiui() && TextUtils.equals("V10", RomUtil.getVersion())
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                manageDrawOverlaysForAndroidM(context);
            } else {
                SettingsCompat.manageDrawOverlays(context);
            }
        } catch (Exception ex) {
            FloatingWindowPermissionUtil.goToAppDetailSettings(context, context.getPackageName());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void manageDrawOverlaysForAndroidM(Context context) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static boolean canDrawOverlays(Context context) {
        return SettingsCompat.canDrawOverlays(context);
    }

    private static boolean checkOp(Context context, int op) {
        if (sCheckOp == null) {
            return SettingsCompat.canDrawOverlays(context);
        }
        try {
            return (boolean) sCheckOp.invoke(null, context, op);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
