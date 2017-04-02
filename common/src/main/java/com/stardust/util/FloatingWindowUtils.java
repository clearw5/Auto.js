package com.stardust.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

/**
 * Created by Stardust on 2017/3/10.
 */

public class FloatingWindowUtils {

    public static boolean checkFloatingWindowPermission(Context context, int messageResId) {
        if (!isFloatingWindowPermitted(context)) {
            Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show();
            IntentUtil.goToAppDetailSettings(context, context.getPackageName());
            return false;
        }
        return true;
    }



    public static boolean isFloatingWindowPermitted(Context context) {
        PackageManager pm = context.getPackageManager();
        return pm.checkPermission(Manifest.permission.SYSTEM_ALERT_WINDOW, context.getPackageName())
                == PackageManager.PERMISSION_GRANTED;
    }

}
