package com.stardust.scriptdroid.external.floating_window;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.external.floating_window.menu.HoverMenuService;
import com.stardust.util.IntentUtil;

/**
 * Created by Stardust on 2017/3/10.
 */

public class FloatingWindowManger {

    private static final String KEY_FLOATING_WINDOW_PERMISSION = "May we go back..I...miss..you..Eating..17.5.9";
    private static final String TAG = "FloatingWindowManger";

    public static void showHoverMenu() {
        if (!HoverMenuService.isServiceRunning()) {
            if (!hasFloatingWindowPermission(App.getApp())) {
                Toast.makeText(App.getApp(), R.string.text_no_floating_window_permission, Toast.LENGTH_SHORT).show();
                goToFloatingWindowPermissionSetting();
            } else {
                HoverMenuService.startService(App.getApp());
            }
        }
    }

    public static void goToFloatingWindowPermissionSetting() {
        IntentUtil.goToAppDetailSettings(App.getApp());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void goToOverlayPermissionSettings(Context context, String packageName) {
        try {
            App.getApp().startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + context))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (Exception e) {
            IntentUtil.goToAppDetailSettings(App.getApp());
        }
    }

    public static void checkPermission() {
        final OverlayPermissionChecker checker = new OverlayPermissionChecker(App.getApp());
        checker.setCallback(new OverlayPermissionChecker.Callback() {
            @Override
            public void onCheckResult(boolean granted) {
                checker.setCallback(null);
                Log.d(TAG, "onCheckResult：" + granted);
                setHasFloatingWindowPermission(App.getApp(), granted);
            }
        });
        checker.check(1500);
    }

    public static boolean hasFloatingWindowPermission(Context context) {
        return hasOverlayPermission();
    }

    private static void setHasFloatingWindowPermission(Context context, boolean has) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(KEY_FLOATING_WINDOW_PERMISSION, has).apply();
    }

    private static boolean hasOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(App.getApp());
        }
        return true;
    }

    public static boolean isHoverMenuShowing() {
        return HoverMenuService.isServiceRunning();
    }


    public static void hideHoverMenu() {
        if (HoverMenuService.isServiceRunning())
            App.getApp().stopService(new Intent(App.getApp(), HoverMenuService.class));
    }
}
