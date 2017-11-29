package com.stardust.scriptdroid.autojs;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.stardust.app.OnActivityResultDelegate;
import com.stardust.app.SimpleActivityLifecycleCallbacks;
import com.stardust.autojs.ScriptEngineService;
import com.stardust.autojs.ScriptEngineServiceBuilder;
import com.stardust.autojs.engine.LoopBasedJavaScriptEngine;
import com.stardust.autojs.engine.RootAutomatorEngine;
import com.stardust.autojs.engine.ScriptEngine;
import com.stardust.autojs.engine.ScriptEngineManager;
import com.stardust.autojs.core.accessibility.AccessibilityBridge;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.core.console.GlobalStardustConsole;
import com.stardust.autojs.runtime.exception.ScriptException;
import com.stardust.autojs.runtime.api.AbstractShell;
import com.stardust.autojs.runtime.api.AppUtils;
import com.stardust.autojs.runtime.api.Console;
import com.stardust.autojs.core.image.ScreenCaptureRequester;
import com.stardust.autojs.core.inputevent.InputEventObserver;
import com.stardust.autojs.script.AutoFileSource;
import com.stardust.autojs.script.JavaScriptSource;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.Pref;
import com.stardust.scriptdroid.R;
import com.stardust.autojs.core.image.ScreenCaptureRequestActivity;
import com.stardust.autojs.core.util.Shell;
import com.stardust.autojs.core.console.StardustConsole;
import com.stardust.scriptdroid.pluginclient.DevPluginService;
import com.stardust.util.ScreenMetrics;
import com.stardust.util.Supplier;
import com.stardust.util.UiHandler;
import com.stardust.view.accessibility.AccessibilityInfoProvider;
import com.stardust.autojs.core.record.accessibility.AccessibilityActionRecorder;
import com.stardust.view.accessibility.AccessibilityNotificationObserver;
import com.stardust.view.accessibility.AccessibilityService;
import com.stardust.scriptdroid.tool.AccessibilityServiceTool;
import com.stardust.view.accessibility.LayoutInspector;
import com.stardust.view.accessibility.NotificationListener;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;


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


    private AutoJs(final Context context) {
        super(context);
        getScriptEngineService().registerGlobalScriptExecutionListener(new ScriptExecutionGlobalListener());
    }

    @Override
    protected Console createGlobalConsole() {
        return new GlobalStardustConsole(getUiHandler()) {
            @Override
            public String println(int level, CharSequence charSequence) {
                String log = super.println(level, charSequence);
                DevPluginService.getInstance().log(log);
                return log;
            }
        };
    }

    public void ensureAccessibilityServiceEnabled() {
        if (AccessibilityService.getInstance() != null) {
            return;
        }
        String errorMessage = null;
        if (AccessibilityServiceTool.isAccessibilityServiceEnabled(App.getApp())) {
            errorMessage = App.getApp().getString(R.string.text_auto_operate_service_enabled_but_not_running);
        } else {
            if (Pref.enableAccessibilityServiceByRoot()) {
                if (!AccessibilityServiceTool.enableAccessibilityServiceByRootAndWaitFor(2000)) {
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
