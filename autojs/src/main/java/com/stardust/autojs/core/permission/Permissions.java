package com.stardust.autojs.core.permission;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

public class Permissions {

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

     static final int REQUEST_CODE = 18777;

    public static String[] getPermissionsNeedToRequest(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return EMPTY_STRING_ARRAY;
        }
        ArrayList<String> list = new ArrayList<>();
        for (String permission : permissions) {
            if (!permission.startsWith("android.permission.")) {
                permission = "android.permission." + permission.toUpperCase();
            }
            if (context.checkSelfPermission(permission) == PERMISSION_DENIED) {
                list.add(permission);
            }
        }
        return list.toArray(EMPTY_STRING_ARRAY);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void requestPermissions(PermissionRequestProxyActivity activity, String[] permissions, OnRequestPermissionsResultCallback callback) {
        if (callback != null) {
            activity.addRequestPermissionsCallback((code, p, grantResults) -> {
                activity.removeRequestPermissionsCallback(callback);
                callback.onRequestPermissionsResult(code, p, grantResults);
            });
        }
        activity.requestPermissions(permissions, REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void requestPermissions(Context context, String[] permissions) {
        context.startActivity(new Intent(context, PermissionRequestActivity.class)
                .putExtra(PermissionRequestActivity.EXTRA_PERMISSIONS, permissions)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
