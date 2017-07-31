package com.stardust.scriptdroid.autojs;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.stardust.app.OnActivityResultDelegate;
import com.stardust.app.SimpleActivityLifecycleCallbacks;
import com.stardust.autojs.ScriptEngineService;
import com.stardust.autojs.ScriptEngineServiceBuilder;
import com.stardust.autojs.engine.RhinoJavaScriptEngineManager;
import com.stardust.autojs.engine.ScriptEngineManager;
import com.stardust.autojs.runtime.AccessibilityBridge;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.ScriptStopException;
import com.stardust.autojs.runtime.api.AbstractShell;
import com.stardust.autojs.runtime.api.AppUtils;
import com.stardust.autojs.runtime.api.Console;
import com.stardust.autojs.runtime.api.image.ScreenCaptureRequester;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.Pref;
import com.stardust.scriptdroid.R;
import com.stardust.autojs.runtime.api.image.ScreenCaptureRequestActivity;
import com.stardust.autojs.runtime.api.Shell;
import com.stardust.autojs.runtime.console.StardustConsole;
import com.stardust.util.ScreenMetrics;
import com.stardust.util.Supplier;
import com.stardust.util.UiHandler;
import com.stardust.view.accessibility.AccessibilityInfoProvider;
import com.stardust.autojs.runtime.record.accessibility.AccessibilityActionRecorder;
import com.stardust.view.accessibility.AccessibilityService;
import com.stardust.scriptdroid.tool.AccessibilityServiceTool;
import com.stardust.scriptdroid.ui.console.JraskaConsole;
import com.stardust.view.accessibility.AccessibilityServiceUtils;
import com.stardust.view.accessibility.LayoutInspector;


/**
 * Created by Stardust on 2017/4/2.
 */

public class AutoJs {

    private static AutoJs instance;

    public static AutoJs getInstance() {
        return instance;
    }

    public static void initInstance(Context context) {
        instance = new AutoJs(context);
    }

    private final AccessibilityActionRecorder mAccessibilityActionRecorder = new AccessibilityActionRecorder();
    private final LayoutInspector mLayoutInspector = new LayoutInspector();
    private final Context mContext;
    private final UiHandler mUiHandler;
    private final AppUtils mAppUtils;
    private final AccessibilityInfoProvider mAccessibilityInfoProvider;
    private final ScreenCaptureRequester mScreenCaptureRequester = new ScreenCaptureRequesterImpl();
    private final ScriptEngineService mScriptEngineService;


    private AutoJs(final Context context) {
        mContext = context;
        mUiHandler = new UiHandler(context);
        mAppUtils = new AppUtils(context);
        mAccessibilityInfoProvider = new AccessibilityInfoProvider(context.getPackageManager());
        mScriptEngineService = buildScriptEngineService();
        addAccessibilityServiceDelegates();
        mScriptEngineService.registerGlobalScriptExecutionListener(new ScriptExecutionGlobalListener());
        registerActivityLifecycleCallbacks();
    }

    private ScriptEngineService buildScriptEngineService() {
        ScriptEngineManager manager = createScriptEngineManager(mContext);
        final Console globalConsole = new JraskaConsole();
        return new ScriptEngineServiceBuilder()
                .uiHandler(mUiHandler)
                .globalConsole(globalConsole)
                .engineManger(manager)
                .runtime(new Supplier<com.stardust.autojs.runtime.ScriptRuntime>() {

                    @Override
                    public com.stardust.autojs.runtime.ScriptRuntime get() {
                        return new ScriptRuntime.Builder()
                                .setConsole(new StardustConsole(mUiHandler, globalConsole))
                                .setScreenCaptureRequester(mScreenCaptureRequester)
                                .setAccessibilityBridge(new AccessibilityBridgeImpl())
                                .setUiHandler(mUiHandler)
                                .setAppUtils(mAppUtils)
                                .setShellSupplier(new Supplier<AbstractShell>() {
                                    @Override
                                    public AbstractShell get() {
                                        return new Shell(mContext, true);
                                    }
                                }).build();
                    }
                })
                .build();
    }

    private void registerActivityLifecycleCallbacks() {
        App.getApp().registerActivityLifecycleCallbacks(new SimpleActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                ScreenMetrics.initIfNeeded(activity);
                mAppUtils.setCurrentActivity(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                mAppUtils.setCurrentActivity(null);
            }

            @Override
            public void onActivityResumed(Activity activity) {
                mAppUtils.setCurrentActivity(activity);
            }
        });
    }

    private ScriptEngineManager createScriptEngineManager(Context context) {
        return new RhinoJavaScriptEngineManager(context);
    }

    private void addAccessibilityServiceDelegates() {
        AccessibilityService.addDelegate(100, mAccessibilityInfoProvider);
        AccessibilityService.addDelegate(300, mAccessibilityActionRecorder);
    }

    public AccessibilityActionRecorder getAccessibilityActionRecorder() {
        return mAccessibilityActionRecorder;
    }

    public AppUtils getAppUtils() {
        return mAppUtils;
    }

    public UiHandler getUiHandler() {
        return mUiHandler;
    }

    public LayoutInspector getLayoutInspector() {
        return mLayoutInspector;
    }


    public ScriptEngineService getScriptEngineService() {
        return mScriptEngineService;
    }

    public AccessibilityInfoProvider getInfoProvider() {
        return mAccessibilityInfoProvider;
    }

    public void ensureAccessibilityServiceEnabled() {
        if (AccessibilityService.getInstance() == null) {
            String errorMessage = null;
            if (AccessibilityServiceUtils.isAccessibilityServiceEnabled(App.getApp(), AccessibilityService.class)) {
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
                throw new ScriptStopException(errorMessage);
            }
        }
    }


    private class AccessibilityBridgeImpl extends AccessibilityBridge {

        @Override
        public void ensureServiceEnabled() {
            AutoJs.this.ensureAccessibilityServiceEnabled();
        }

        @Nullable
        @Override
        public AccessibilityService getService() {
            return AccessibilityService.getInstance();
        }

        @Override
        public AccessibilityInfoProvider getInfoProvider() {
            return mAccessibilityInfoProvider;
        }

    }

    private class ScreenCaptureRequesterImpl extends ScreenCaptureRequester.AbstractScreenCaptureRequester {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void request() {
            Activity activity = mAppUtils.getCurrentActivity();
            if (activity instanceof OnActivityResultDelegate.DelegateHost) {
                ScreenCaptureRequester requester = new ActivityScreenCaptureRequester(
                        ((OnActivityResultDelegate.DelegateHost) activity).getOnActivityResultDelegateMediator(), activity);
                requester.setOnActivityResultCallback(mCallback);
                requester.request();
            } else {
                ScreenCaptureRequestActivity.request(mContext, mCallback);
            }
        }
    }
}
