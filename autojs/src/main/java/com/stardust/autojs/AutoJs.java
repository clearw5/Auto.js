package com.stardust.autojs;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.stardust.app.OnActivityResultDelegate;
import com.stardust.app.SimpleActivityLifecycleCallbacks;
import com.stardust.autojs.core.accessibility.AccessibilityBridge;
import com.stardust.autojs.core.console.GlobalStardustConsole;
import com.stardust.autojs.core.console.StardustConsole;
import com.stardust.autojs.core.image.ScreenCaptureRequestActivity;
import com.stardust.autojs.core.image.ScreenCaptureRequester;
import com.stardust.autojs.core.inputevent.InputEventObserver;
import com.stardust.autojs.core.record.accessibility.AccessibilityActionRecorder;
import com.stardust.autojs.core.util.Shell;
import com.stardust.autojs.engine.LoopBasedJavaScriptEngine;
import com.stardust.autojs.engine.RootAutomatorEngine;
import com.stardust.autojs.engine.ScriptEngine;
import com.stardust.autojs.engine.ScriptEngineManager;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.api.AppUtils;
import com.stardust.autojs.runtime.api.Console;
import com.stardust.autojs.runtime.exception.ScriptException;
import com.stardust.autojs.script.AutoFileSource;
import com.stardust.autojs.script.JavaScriptSource;
import com.stardust.util.ScreenMetrics;
import com.stardust.util.Supplier;
import com.stardust.util.UiHandler;
import com.stardust.view.accessibility.AccessibilityInfoProvider;
import com.stardust.view.accessibility.AccessibilityNotificationObserver;
import com.stardust.view.accessibility.AccessibilityService;
import com.stardust.view.accessibility.LayoutInspector;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;

/**
 * Created by Stardust on 2017/11/29.
 */

public abstract class AutoJs {

    private final AccessibilityActionRecorder mAccessibilityActionRecorder = new AccessibilityActionRecorder();
    private final AccessibilityNotificationObserver mNotificationObserver;
    private ScriptEngineManager mScriptEngineManager;
    private final LayoutInspector mLayoutInspector = new LayoutInspector();
    private final Context mContext;
    private final UiHandler mUiHandler;
    private final AppUtils mAppUtils;
    private final AccessibilityInfoProvider mAccessibilityInfoProvider;
    private final ScreenCaptureRequester mScreenCaptureRequester = new ScreenCaptureRequesterImpl();
    private final ScriptEngineService mScriptEngineService;
    private final Console mGlobalConsole;


    protected AutoJs(final Context context) {
        mContext = context;
        mUiHandler = new UiHandler(context);
        mAppUtils = new AppUtils(context);
        mGlobalConsole = createGlobalConsole();
        mNotificationObserver = new AccessibilityNotificationObserver(context);
        mAccessibilityInfoProvider = new AccessibilityInfoProvider(context.getPackageManager());
        mScriptEngineService = buildScriptEngineService();
        init();
    }

    protected Console createGlobalConsole() {
        return new GlobalStardustConsole(mUiHandler);
    }

    protected void init() {
        addAccessibilityServiceDelegates();
        registerActivityLifecycleCallbacks();
        InputEventObserver.initGlobal(mContext);
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_13, mContext, new BaseLoaderCallback(mContext) {
        });
    }

    public abstract void ensureAccessibilityServiceEnabled();

    protected abstract Application getApplication();

    protected ScriptEngineService buildScriptEngineService() {
        initScriptEngineManager();
        return new ScriptEngineServiceBuilder()
                .uiHandler(mUiHandler)
                .globalConsole(mGlobalConsole)
                .engineManger(mScriptEngineManager)
                .build();
    }

    protected void initScriptEngineManager() {
        mScriptEngineManager = new ScriptEngineManager(mContext);
        mScriptEngineManager.registerEngine(JavaScriptSource.ENGINE, () -> {
            LoopBasedJavaScriptEngine engine = new LoopBasedJavaScriptEngine(mContext);
            engine.setRuntime(new ScriptRuntime.Builder()
                    .setConsole(new StardustConsole(mUiHandler, mGlobalConsole))
                    .setScreenCaptureRequester(mScreenCaptureRequester)
                    .setAccessibilityBridge(new AccessibilityBridgeImpl())
                    .setUiHandler(mUiHandler)
                    .setAppUtils(mAppUtils)
                    .setEngineService(mScriptEngineService)
                    .setShellSupplier(() -> new Shell(mContext, true)).build());
            return engine;
        });
        mScriptEngineManager.registerEngine(AutoFileSource.ENGINE, new Supplier<ScriptEngine>() {
            @Override
            public ScriptEngine get() {
                return new RootAutomatorEngine(mContext);
            }
        });

    }

    protected void registerActivityLifecycleCallbacks() {
        getApplication().registerActivityLifecycleCallbacks(new SimpleActivityLifecycleCallbacks() {

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


    private void addAccessibilityServiceDelegates() {
        AccessibilityService.addDelegate(100, mAccessibilityInfoProvider);
        AccessibilityService.addDelegate(200, mNotificationObserver);
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

    public Console getGlobalConsole() {
        return mGlobalConsole;
    }

    public ScriptEngineService getScriptEngineService() {
        return mScriptEngineService;
    }

    public AccessibilityInfoProvider getInfoProvider() {
        return mAccessibilityInfoProvider;
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

        @Override
        public AccessibilityNotificationObserver getNotificationObserver() {
            return mNotificationObserver;
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
