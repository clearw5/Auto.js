package com.stardust.auojs.inrt.rt;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.stardust.app.OnActivityResultDelegate;
import com.stardust.app.SimpleActivityLifecycleCallbacks;
import com.stardust.auojs.inrt.R;
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
import com.stardust.autojs.runtime.api.Shell;
import com.stardust.autojs.runtime.api.image.ScreenCaptureRequestActivity;
import com.stardust.autojs.runtime.api.image.ScreenCaptureRequester;
import com.stardust.autojs.runtime.console.StardustConsole;
import com.stardust.automator.AccessibilityEventCommandHost;
import com.stardust.automator.simple_action.SimpleActionPerformHost;
import com.stardust.util.ScreenMetrics;
import com.stardust.util.Supplier;
import com.stardust.util.UiHandler;
import com.stardust.view.accessibility.AccessibilityInfoProvider;
import com.stardust.view.accessibility.AccessibilityService;
import com.stardust.view.accessibility.AccessibilityServiceUtils;

/**
 * Created by Stardust on 2017/7/1.
 */

public class AutoJs implements AccessibilityBridge {

    private static AutoJs instance;

    public static AutoJs getInstance() {
        return instance;
    }

    public static void initInstance(Application context) {
        instance = new AutoJs(context);
    }

    private final AccessibilityEventCommandHost mAccessibilityEventCommandHost = new AccessibilityEventCommandHost();
    private final SimpleActionPerformHost mSimpleActionPerformHost = new SimpleActionPerformHost();
    private final ScriptEngineService mScriptEngineService;
    private final AccessibilityInfoProvider mAccessibilityInfoProvider;
    private final UiHandler mUiHandler;
    private final AppUtils mAppUtils;
    private final Application mApplication;
    private final ScreenCaptureRequester mScreenCaptureRequester = new ScreenCaptureRequester.AbstractScreenCaptureRequester() {

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
                ScreenCaptureRequestActivity.request(mUiHandler.getContext(), mCallback);
            }
        }

    };


    private AutoJs(final Application application) {
        mApplication = application;
        final Context context = application.getApplicationContext();
        mUiHandler = new UiHandler(context);
        mAppUtils = new AppUtils(context);
        mAccessibilityInfoProvider = new AccessibilityInfoProvider(context.getPackageManager());
        ScriptEngineManager manager = createScriptEngineManager(context);
        mScriptEngineService = new ScriptEngineServiceBuilder()
                .uiHandler(mUiHandler)
                .engineManger(manager)
                .runtime(new Supplier<ScriptRuntime>() {

                    @Override
                    public com.stardust.autojs.runtime.ScriptRuntime get() {
                        return new ScriptRuntime.Builder()
                                .setConsole(new StardustConsole(mUiHandler))
                                .setScreenCaptureRequester(mScreenCaptureRequester)
                                .setAccessibilityBridge(AutoJs.this)
                                .setUiHandler(mUiHandler)
                                .setAppUtils(mAppUtils)
                                .setShellSupplier(new Supplier<AbstractShell>() {
                                    @Override
                                    public AbstractShell get() {
                                        return new Shell(context, true);
                                    }
                                }).build();
                    }
                })
                .build();
        addAccessibilityServiceDelegates();
        registerActivityLifecycleCallbacks();
    }

    private void registerActivityLifecycleCallbacks() {
        mApplication.registerActivityLifecycleCallbacks(new SimpleActivityLifecycleCallbacks() {

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
    }


    @Override
    public AccessibilityEventCommandHost getCommandHost() {
        return mAccessibilityEventCommandHost;
    }

    @Override
    public SimpleActionPerformHost getActionPerformHost() {
        return mSimpleActionPerformHost;
    }

    @Nullable
    @Override
    public AccessibilityService getService() {
        return AccessibilityService.getInstance();
    }

    @Override
    public void ensureServiceEnabled() {
        Context context = mApplication.getApplicationContext();
        if (AccessibilityService.getInstance() == null) {
            String errorMessage = null;
            if (AccessibilityServiceUtils.isAccessibilityServiceEnabled(context, AccessibilityService.class)) {
                errorMessage = context.getString(R.string.text_auto_operate_service_enabled_but_not_running);
            } else {
                errorMessage = context.getString(R.string.text_no_accessibility_permission);
            }
            AccessibilityServiceUtils.goToAccessibilitySetting(context);
            throw new ScriptStopException(errorMessage);
        }
    }

    @Override
    public AccessibilityInfoProvider getInfoProvider() {
        return mAccessibilityInfoProvider;
    }

    public ScriptEngineService getScriptEngineService() {
        return mScriptEngineService;
    }
}
