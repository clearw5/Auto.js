package org.autojs.autojs.tool;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

import com.stardust.app.GlobalAppContext;

import org.autojs.autojs.BuildConfig;
import org.autojs.autojs.Pref;
import org.autojs.autojs.R;

import com.stardust.autojs.core.accessibility.AccessibilityService;
import com.stardust.autojs.core.util.ProcessShell;
import com.stardust.view.accessibility.AccessibilityServiceUtils;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by Stardust on 2017/1/26.
 */

public class AccessibilityServiceTool {

    private static final Class<AccessibilityService> sAccessibilityServiceClass = AccessibilityService.class;

    public static void enableAccessibilityService() {
        boolean enabled = false;
        if (Pref.shouldEnableAccessibilityServiceByRoot()) {
            enabled = enableAccessibilityServiceByRoot(sAccessibilityServiceClass);
        } else if (Pref.haveAdbPermission(GlobalAppContext.get())) {
            enabled = enableAccessibilityServiceByAdb(sAccessibilityServiceClass);
        }
        if (!enabled) {
            goToAccessibilitySetting();
        }
    }

    public static void goToAccessibilitySetting() {
        Context context = GlobalAppContext.get();
        if (Pref.isFirstGoToAccessibilitySetting()) {
            GlobalAppContext.toast(context.getString(R.string.text_please_choose) + context.getString(R.string.app_name));
        }
        try {
            AccessibilityServiceUtils.INSTANCE.goToAccessibilitySetting(context);
        } catch (ActivityNotFoundException e) {
            GlobalAppContext.toast(context.getString(R.string.go_to_accessibility_settings) + context.getString(R.string.app_name));
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
    private static final Pattern SERVICE_PATTERN = Pattern.compile("^(((\\w+\\.)+\\w+)[/]?){2}$");

    public static boolean enableAccessibilityServiceByRoot() {
        return enableAccessibilityServiceByRoot(sAccessibilityServiceClass);
    }

    public static boolean enableAccessibilityServiceByRoot(Class<? extends android.accessibilityservice.AccessibilityService> accessibilityService) {
        String serviceName = GlobalAppContext.get().getPackageName() + "/" + accessibilityService.getName();
        try {
            return TextUtils.isEmpty(ProcessShell.execCommand(String.format(Locale.getDefault(), cmd, serviceName), true).error);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean enableAccessibilityServiceByRootAndWaitFor(long timeOut) {
        if (enableAccessibilityServiceByRoot(sAccessibilityServiceClass)) {
            return AccessibilityService.Companion.waitForEnabled(timeOut);
        }
        return false;
    }

    public static void enableAccessibilityServiceByRootIfNeeded() {
        if (AccessibilityService.Companion.getInstance() == null)
            if (Pref.shouldEnableAccessibilityServiceByRoot()) {
                AccessibilityServiceTool.enableAccessibilityServiceByRoot(sAccessibilityServiceClass);
            }
    }

    public static boolean enableAccessibilityServiceByAdbAndWaitFor(long timeout) {
        if (enableAccessibilityServiceByAdb(sAccessibilityServiceClass)) {
            return AccessibilityService.Companion.waitForEnabled(timeout);
        }
        return false;
    }

    public static boolean enableAccessibilityServiceByAdb() {
        return enableAccessibilityServiceByAdb(sAccessibilityServiceClass);
    }

    /**
     * 尝试自动设置无障碍权限，需要ADB授权 adb shell pm grant ${BuildConfig.APPLICATION_ID} android.permission.WRITE_SECURE_SETTINGS
     *
     * @param accessibilityService
     * @return
     */
    public static boolean enableAccessibilityServiceByAdb(Class<? extends android.accessibilityservice.AccessibilityService> accessibilityService) {
        try {
            String enabledServices = Settings.Secure.getString(GlobalAppContext.get().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            String requiredService = BuildConfig.APPLICATION_ID + "/" + accessibilityService.getName();
            String services = enabledServices + ":" + requiredService;
            String[] serviceInfo = services.split(":");
            StringBuilder sb = new StringBuilder();
            for (String service : serviceInfo) {
                if (SERVICE_PATTERN.matcher(service).find()) {
                    sb.append(service).append(":");
                }
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            Settings.Secure.putString(GlobalAppContext.get().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, sb.toString());
            Settings.Secure.putString(GlobalAppContext.get().getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, "1");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean disableAccessibilityServiceByAdb() {
        return disableAccessibilityServiceByAdb(sAccessibilityServiceClass);
    }

    public static boolean disableAccessibilityServiceByAdb(Class<? extends android.accessibilityservice.AccessibilityService> accessibilityService) {
        try {
            String enabledServices = Settings.Secure.getString(GlobalAppContext.get().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            String requiredService = BuildConfig.APPLICATION_ID + "/" + accessibilityService.getName();
            String services = enabledServices.replace(requiredService, "");
            String[] serviceInfo = services.split(":");
            StringBuilder sb = new StringBuilder();
            for (String service : serviceInfo) {
                if (SERVICE_PATTERN.matcher(service).find()) {
                    sb.append(service).append(":");
                }
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            Settings.Secure.putString(GlobalAppContext.get().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, sb.toString());
            Settings.Secure.putString(GlobalAppContext.get().getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, "1");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isAccessibilityServiceEnabled(Context context) {
        return AccessibilityServiceUtils.INSTANCE.isAccessibilityServiceEnabled(context, sAccessibilityServiceClass);
    }
}
