package com.stardust.util;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import static com.stardust.scriptdroid.tool.IntentTool.goToAppSetting;

/**
 * Created by Stardust on 2017/3/10.
 */

public class FloatingWindowUtils {

    public static boolean checkFloatingWindowPermission(Context context, int messageResId) {
        if (!isFloatingWindowPermitted(context)) {
            Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show();
            goToAppSetting(context, context.getPackageName());
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
