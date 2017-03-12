package com.stardust.scriptdroid.tool;

import android.content.Context;
import android.widget.Toast;

import com.stardust.scriptdroid.Pref;
import com.stardust.scriptdroid.service.AccessibilityWatchDogService;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;
import com.stardust.view.accessibility.AccessibilityServiceUtils;

import static com.stardust.view.accessibility.AccessibilityServiceUtils.isAccessibilityServiceEnabled;

/**
 * Created by Stardust on 2017/1/26.
 */

public class AccessibilityServiceTool {

    public static void enableAccessibilityService() {
        if (Pref.def().getBoolean(App.getApp().getString(R.string.key_enable_accessibility_service_by_root), false)) {
            if (!enableAccessibilityServiceByRootAndWaitFor(AccessibilityWatchDogService.class, 3000)) {
                goToAccessibilitySetting();
            }
        } else {
            goToAccessibilitySetting();
        }
    }

    public static void goToAccessibilitySetting() {
        Context context = App.getApp();
        if (Pref.isFirstGoToAccessibilitySetting()) {
            Toast.makeText(context, context.getString(R.string.text_please_choose) + context.getString(R.string._app_name), Toast.LENGTH_LONG).show();
        }
        AccessibilityServiceUtils.goToAccessibilitySetting(context);
    }

    public static boolean enableAccessibilityServiceByRootAndWaitFor(Class<AccessibilityWatchDogService> accessibilityService, int timeout) {
        Shell.execCommand("settings put secure enabled_accessibility_services %accessibility:"
                + App.getApp().getPackageName() + "/" + accessibilityService.getName(), true);
        long millis = System.currentTimeMillis();
        while (true) {
            if (isAccessibilityServiceEnabled(App.getApp(), accessibilityService))
                return true;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (System.currentTimeMillis() - millis >= timeout) {
                return false;
            }
        }
    }
}
