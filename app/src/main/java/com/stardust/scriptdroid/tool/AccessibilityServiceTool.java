package com.stardust.scriptdroid.tool;

import android.accessibilityservice.AccessibilityService;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.stardust.scriptdroid.Pref;
import com.stardust.scriptdroid.service.AccessibilityWatchDogService;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;
import com.stardust.autojs.runtime.api.ProcessShell;
import com.stardust.view.accessibility.AccessibilityServiceUtils;

import java.util.Locale;

/**
 * Created by Stardust on 2017/1/26.
 */

public class AccessibilityServiceTool {

    public static void enableAccessibilityService() {
        if (Pref.enableAccessibilityServiceByRoot()) {
            if (!enableAccessibilityServiceByRoot(AccessibilityWatchDogService.class)) {
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
        try {
            AccessibilityServiceUtils.goToAccessibilitySetting(context);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, context.getString(R.string.go_to_accessibility_settings) + context.getString(R.string._app_name), Toast.LENGTH_LONG).show();
        }
    }

    private static final String cmd = "enabled=$(settings get secure enabled_accessibility_services)\n" +
            "pkg=%s\n" +
            "if [[ $enabled == *$pkg* ]]\n" +
            "then\n" +
            "echo already_enabled\n" +
            "else\n" +
            "enabled=$pkg:$enabled\n" +
            "settings put secure enabled_accessibility_services $enabled\n" +
            "fi";

    public static boolean enableAccessibilityServiceByRoot(Class<? extends AccessibilityService> accessibilityService) {
        String serviceName = App.getApp().getPackageName() + "/" + accessibilityService.getName();
        return TextUtils.isEmpty(ProcessShell.execCommand(String.format(Locale.getDefault(), cmd, serviceName), true).error);
    }

    public static boolean enableAccessibilityServiceByRootAndWaitFor(long timeOut) {
        if (enableAccessibilityServiceByRoot(AccessibilityWatchDogService.class)) {
            AccessibilityWatchDogService.waitForEnabled(timeOut);
        }
        return true;
    }

    public static void enableAccessibilityServiceByRootIfNeeded() {
        if (AccessibilityWatchDogService.getInstance() == null)
            if (Pref.enableAccessibilityServiceByRoot()) {
                AccessibilityServiceTool.enableAccessibilityServiceByRoot(AccessibilityWatchDogService.class);
            }
    }
}
