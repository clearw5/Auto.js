package com.stardust.util;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

/**
 * Created by Stardust on 2017/3/10.
 */

public class FloatingWindowUtils {

    public static boolean checkFloatingWindowPermission(Context context, int messageResId) {
        if (!isFloatingWindowPermitted(context)) {
            Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show();
            goToAppSetting(context);
            return false;
        }
        return true;
    }

    private static void goToAppSetting(Context context) {
        try {
            Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.addCategory(Intent.CATEGORY_DEFAULT);
            i.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(i);
        } catch (ActivityNotFoundException ignored) {

        }
    }

    public static boolean isFloatingWindowPermitted(Context context) {
        PackageManager pm = context.getPackageManager();
        return pm.checkPermission(Manifest.permission.SYSTEM_ALERT_WINDOW, context.getPackageName())
                == PackageManager.PERMISSION_GRANTED;
    }

}
