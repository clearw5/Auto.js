package com.stardust.enhancedfloaty.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

/**
 * Created by Stardust on 2017/3/10.
 */

public class FloatingWindowPermissionUtil {

    public static void goToFloatingWindowPermissionSettingIfNeeded(Context context) {
        if (!hasFloatingWindowPermission(context)) {
            goToFloatingWindowPermissionSetting(context);
        }
    }

    public static void goToFloatingWindowPermissionSetting(Context context) {
        String packageName = context.getPackageName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                context.startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + packageName))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            } catch (Exception e) {
                goToAppDetailSettings(context, packageName);
            }
        } else {
            goToAppDetailSettings(context, packageName);
        }
    }

    public static boolean hasFloatingWindowPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }

    public static boolean goToAppDetailSettings(Context context, String packageName) {
        try {
            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.addCategory(Intent.CATEGORY_DEFAULT);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setData(Uri.parse("package:" + packageName));
            context.startActivity(i);
            return true;
        } catch (ActivityNotFoundException ignored) {
            return false;
        }
    }

}
