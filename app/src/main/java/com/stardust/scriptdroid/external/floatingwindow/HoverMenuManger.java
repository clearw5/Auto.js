package com.stardust.scriptdroid.external.floatingwindow;

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
import com.stardust.scriptdroid.external.floatingwindow.menu.HoverMenuService;
import com.stardust.util.IntentUtil;

import ezy.assist.compat.SettingsCompat;

/**
 * Created by Stardust on 2017/3/10.
 */

public class HoverMenuManger {

    private static final String KEY_FLOATING_WINDOW_PERMISSION = "May we go back..I...miss..you..Eating..17.5.9";
    private static final String TAG = "HoverMenuManger";

    public static void showHoverMenu() {
        if (!HoverMenuService.isServiceRunning()) {
            if (!SettingsCompat.canDrawOverlays(App.getApp())) {
                Toast.makeText(App.getApp(), R.string.text_no_floating_window_permission, Toast.LENGTH_SHORT).show();
                SettingsCompat.manageDrawOverlays(App.getApp());
            } else {
                HoverMenuService.startService(App.getApp());
            }
        }
    }

    public static boolean isHoverMenuShowing() {
        return HoverMenuService.isServiceRunning();
    }


    public static void hideHoverMenu() {
        App.getApp().stopService(new Intent(App.getApp(), HoverMenuService.class));
    }
}
