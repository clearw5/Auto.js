package com.stardust.scriptdroid.tool;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.Pref;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.service.AccessibilityWatchDogService;
import com.stardust.view.accessibility.AccessibilityServiceUtils;

/**
 * Created by Stardust on 2017/1/26.
 */

public class AccessibilityServiceTool {

    public static void enableAccessibilityService() {
        if (Pref.def().getBoolean(App.getApp().getString(R.string.key_enable_accessibility_service_by_root), false)) {
            if (!AccessibilityServiceUtils.enableAccessibilityServiceByRootAndWaitFor(App.getApp(), AccessibilityWatchDogService.class, 3000)) {
                AccessibilityServiceUtils.goToAccessibilitySetting(App.getApp());
            }
        } else {
            AccessibilityServiceUtils.goToAccessibilitySetting(App.getApp());
        }
    }

}
