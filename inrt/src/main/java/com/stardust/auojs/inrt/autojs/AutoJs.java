package com.stardust.auojs.inrt.autojs;

import android.app.Application;
import android.content.Context;

import com.stardust.auojs.inrt.App;
import com.stardust.auojs.inrt.Pref;
import com.stardust.auojs.inrt.R;
import com.stardust.autojs.runtime.exception.ScriptException;
import com.stardust.view.accessibility.AccessibilityService;
import com.stardust.view.accessibility.AccessibilityServiceUtils;


/**
 * Created by Stardust on 2017/4/2.
 */

public class AutoJs extends com.stardust.autojs.AutoJs {

    private static AutoJs instance;

    public static AutoJs getInstance() {
        return instance;
    }

    public static void initInstance(Context context) {
        instance = new AutoJs(context);
    }

    private AutoJs(Context context) {
        super(context);
    }


    @Override
    public void ensureAccessibilityServiceEnabled() {
        if (AccessibilityService.getInstance() != null) {
            return;
        }
        String errorMessage = null;
        if (AccessibilityServiceUtils.isAccessibilityServiceEnabled(getApplication(), AccessibilityService.class)) {
            errorMessage = App.getApp().getString(R.string.text_auto_operate_service_enabled_but_not_running);
        } else {
            if (Pref.shouldEnableAccessibilityServiceByRoot()) {
                if (!AccessibilityServiceTool.enableAccessibilityServiceByRootAndWaitFor(getApplication(), 2000)) {
                    errorMessage = App.getApp().getString(R.string.text_enable_accessibility_service_by_root_timeout);
                }
            } else {
                errorMessage = App.getApp().getString(R.string.text_no_accessibility_permission);
            }
        }
        if (errorMessage != null) {
            AccessibilityServiceTool.goToAccessibilitySetting();
            throw new ScriptException(errorMessage);
        }
    }


    @Override
    protected Application getApplication() {
        return App.getApp();
    }
}
