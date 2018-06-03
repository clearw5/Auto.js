package com.stardust.auojs.inrt.autojs;

import android.app.Application;
import android.content.Context;

import com.stardust.app.GlobalAppContext;
import com.stardust.auojs.inrt.App;
import com.stardust.auojs.inrt.LogActivity;
import com.stardust.auojs.inrt.Pref;
import com.stardust.auojs.inrt.R;
import com.stardust.auojs.inrt.SettingsActivity;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.exception.ScriptException;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
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

    public static void initInstance(Application application) {
        instance = new AutoJs(application);
    }

    private AutoJs(Application application) {
        super(application);
        getScriptEngineService().registerGlobalScriptExecutionListener(new ScriptExecutionGlobalListener());
    }


    @Override
    public void ensureAccessibilityServiceEnabled() {
        if (AccessibilityService.getInstance() != null) {
            return;
        }
        String errorMessage = null;
        if (AccessibilityServiceUtils.isAccessibilityServiceEnabled(getApplication(), AccessibilityService.class)) {
            errorMessage = GlobalAppContext.getString(R.string.text_auto_operate_service_enabled_but_not_running);
        } else {
            if (Pref.shouldEnableAccessibilityServiceByRoot()) {
                if (!AccessibilityServiceTool.enableAccessibilityServiceByRootAndWaitFor(getApplication(), 2000)) {
                    errorMessage = GlobalAppContext.getString(R.string.text_enable_accessibility_service_by_root_timeout);
                }
            } else {
                errorMessage = GlobalAppContext.getString(R.string.text_no_accessibility_permission);
            }
        }
        if (errorMessage != null) {
            AccessibilityServiceTool.goToAccessibilitySetting();
            throw new ScriptException(errorMessage);
        }
    }

    @Override
    public void waitForAccessibilityServiceEnabled() {
        if (AccessibilityService.getInstance() != null) {
            return;
        }
        String errorMessage = null;
        if (AccessibilityServiceUtils.isAccessibilityServiceEnabled(getApplication(), AccessibilityService.class)) {
            errorMessage = GlobalAppContext.getString(R.string.text_auto_operate_service_enabled_but_not_running);
        } else {
            if (Pref.shouldEnableAccessibilityServiceByRoot()) {
                if (!AccessibilityServiceTool.enableAccessibilityServiceByRootAndWaitFor(getApplication(), 2000)) {
                    errorMessage = GlobalAppContext.getString(R.string.text_enable_accessibility_service_by_root_timeout);
                }
            } else {
                errorMessage = GlobalAppContext.getString(R.string.text_no_accessibility_permission);
            }
        }
        if (errorMessage != null) {
            AccessibilityServiceTool.goToAccessibilitySetting();
            if (!AccessibilityService.waitForEnabled(-1)) {
                throw new ScriptInterruptedException();
            }
        }
    }

    @Override
    protected ScriptRuntime createRuntime() {
        ScriptRuntime runtime = super.createRuntime();
        runtime.putProperty("class.settings", SettingsActivity.class);
        runtime.putProperty("class.console", LogActivity.class);
        return runtime;
    }
}
