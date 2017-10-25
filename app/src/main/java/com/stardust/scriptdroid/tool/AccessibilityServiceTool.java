package com.stardust.scriptdroid.tool;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.text.TextUtils;

import com.stardust.scriptdroid.Pref;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;
import com.stardust.autojs.runtime.api.ProcessShell;
import com.stardust.scriptdroid.accessibility.AccessibilityService;
import com.stardust.view.accessibility.AccessibilityServiceUtils;

import java.util.Locale;

/**
 * Created by Stardust on 2017/1/26.
 */

public class AccessibilityServiceTool {

    private static final Class<AccessibilityService> sAccessibilityServiceClass = AccessibilityService.class;

    public static void enableAccessibilityService() {
        if (Pref.enableAccessibilityServiceByRoot()) {
            if (!enableAccessibilityServiceByRoot(sAccessibilityServiceClass)) {
                goToAccessibilitySetting();
            }
        } else {
            goToAccessibilitySetting();
        }
    }

    public static void goToAccessibilitySetting() {
        Context context = App.getApp();
        if (Pref.isFirstGoToAccessibilitySetting()) {
            App.getApp().getUiHandler().toast(context.getString(R.string.text_please_choose) + context.getString(R.string._app_name));
        }
        try {
            AccessibilityServiceUtils.goToAccessibilitySetting(context);
        } catch (ActivityNotFoundException e) {
            App.getApp().getUiHandler().toast(context.getString(R.string.go_to_accessibility_settings) + context.getString(R.string._app_name));
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
            "fi\n" +
            "settings put secure accessibility_enabled 1";

    public static boolean enableAccessibilityServiceByRoot(Class<? extends android.accessibilityservice.AccessibilityService> accessibilityService) {
        String serviceName = App.getApp().getPackageName() + "/" + accessibilityService.getName();
        try {
            return TextUtils.isEmpty(ProcessShell.execCommand(String.format(Locale.getDefault(), cmd, serviceName), true).error);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean enableAccessibilityServiceByRootAndWaitFor(long timeOut) {
        if (enableAccessibilityServiceByRoot(sAccessibilityServiceClass)) {
            return AccessibilityService.waitForEnabled(timeOut);
        }
        return false;
    }

    public static void enableAccessibilityServiceByRootIfNeeded() {
        if (AccessibilityService.getInstance() == null)
            if (Pref.enableAccessibilityServiceByRoot()) {
                AccessibilityServiceTool.enableAccessibilityServiceByRoot(sAccessibilityServiceClass);
            }
    }

    public static boolean isAccessibilityServiceEnabled(Context context) {
        return AccessibilityServiceUtils.isAccessibilityServiceEnabled(context, sAccessibilityServiceClass);
    }
}
