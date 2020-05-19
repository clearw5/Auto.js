package com.stardust.autojs;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.stardust.app.OnActivityResultDelegate;
import com.stardust.app.SimpleActivityLifecycleCallbacks;
import com.stardust.autojs.core.accessibility.AccessibilityBridge;
import com.stardust.autojs.core.console.GlobalConsole;
import com.stardust.autojs.core.console.ConsoleImpl;
import com.stardust.autojs.core.image.capture.ScreenCaptureRequestActivity;
import com.stardust.autojs.core.image.capture.ScreenCaptureRequester;
import com.stardust.autojs.core.record.accessibility.AccessibilityActionRecorder;
import com.stardust.autojs.core.util.Shell;
import com.stardust.autojs.engine.LoopBasedJavaScriptEngine;
import com.stardust.autojs.engine.RootAutomatorEngine;
import com.stardust.autojs.engine.ScriptEngineManager;
import com.stardust.autojs.rhino.InterruptibleAndroidContextFactory;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.accessibility.AccessibilityConfig;
import com.stardust.autojs.runtime.api.AppUtils;
import com.stardust.autojs.script.AutoFileSource;
import com.stardust.autojs.script.JavaScriptSource;
import com.stardust.util.ResourceMonitor;
import com.stardust.util.ScreenMetrics;
import com.stardust.util.UiHandler;
import com.stardust.autojs.core.activity.ActivityInfoProvider;
import com.stardust.view.accessibility.AccessibilityNotificationObserver;
import com.stardust.view.accessibility.AccessibilityService;
import com.stardust.view.accessibility.LayoutInspector;

import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.WrappedException;

import java.io.File;

/**
 * Created by Stardust on 2017/11/29.
 */

public abstract class AutoJs {

    private final AccessibilityActionRecorder mAccessibilityActionRecorder = new AccessibilityActionRecorder();
    private final AccessibilityNotificationObserver mNotificationObserver;
    private ScriptEngineManager mScriptEngineManager;
    private final LayoutInspector mLayoutInspector;
    private final Context mContext;
    private final Application mApplication;
    private final UiHandler mUiHandler;
    private final AppUtils mAppUtils;
    private final ActivityInfoProvider mActivityInfoProvider;
    private final ScreenCaptureRequester mScreenCaptureRequester = new ScreenCaptureRequesterImpl();
    private final ScriptEngineService mScriptEngineService;
    private final GlobalConsole mGlobalConsole;


    protected AutoJs(final Application application) {
        mContext = application.getApplicationContext();
        mApplication = application;
        mLayoutInspector = new LayoutInspector(mContext);
        mUiHandler = new UiHandler(mContext);
        mAppUtils = createAppUtils(mContext);
        mGlobalConsole = createGlobalConsole();
        mNotificationObserver = new AccessibilityNotificationObserver(mContext);
        mActivityInfoProvider = new ActivityInfoProvider(mContext);
        mScriptEngineService = buildScriptEngineService();
        ScriptEngineService.setInstance(mScriptEngineService);
        init();
    }

    protected AppUtils createAppUtils(Context context) {
        return new AppUtils(mContext);
    }

    protected GlobalConsole createGlobalConsole() {
        return new GlobalConsole(mUiHandler);
    }

    protected void init() {
        addAccessibilityServiceDelegates();
        registerActivityLifecycleCallbacks();
        ResourceMonitor.setExceptionCreator(resource -> {
            Exception exception;
            if (org.mozilla.javascript.Context.getCurrentContext() != null) {
                exception = new WrappedException(new ResourceMonitor.UnclosedResourceException(resource));
            } else {
                exception = new ResourceMonitor.UnclosedResourceException(resource);
            }
            exception.fillInStackTrace();
            return exception;
        });
        ResourceMonitor.setUnclosedResourceDetectedHandler(detectedException -> mGlobalConsole.error(detectedException));
    }

    public abstract void ensureAccessibilityServiceEnabled();

    protected Application getApplication() {
        return mApplication;
    }

    public ScriptEngineManager getScriptEngineManager() {
        return mScriptEngineManager;
    }

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
            engine.setRuntime(createRuntime());
            return engine;
        });
        initContextFactory();
        mScriptEngineManager.registerEngine(AutoFileSource.ENGINE, () -> new RootAutomatorEngine(mContext));
    }

    protected void initContextFactory() {
        ContextFactory.initGlobal(new InterruptibleAndroidContextFactory(new File(mContext.getCacheDir(), "classes")));
    }

    protected ScriptRuntime createRuntime() {
        return new ScriptRuntime.Builder()
                .setConsole(new ConsoleImpl(mUiHandler, mGlobalConsole))
                .setScreenCaptureRequester(mScreenCaptureRequester)
                .setAccessibilityBridge(new AccessibilityBridgeImpl(mUiHandler))
                .setUiHandler(mUiHandler)
                .setAppUtils(mAppUtils)
                .setEngineService(mScriptEngineService)
                .setShellSupplier(() -> new Shell(mContext, true)).build();
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
        AccessibilityService.Companion.addDelegate(100, mActivityInfoProvider);
        AccessibilityService.Companion.addDelegate(200, mNotificationObserver);
        AccessibilityService.Companion.addDelegate(300, mAccessibilityActionRecorder);
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

    public GlobalConsole getGlobalConsole() {
        return mGlobalConsole;
    }

    public ScriptEngineService getScriptEngineService() {
        return mScriptEngineService;
    }

    public ActivityInfoProvider getInfoProvider() {
        return mActivityInfoProvider;
    }


    public abstract void waitForAccessibilityServiceEnabled();

    protected AccessibilityConfig createAccessibilityConfig() {
        return new AccessibilityConfig();
    }

    private class AccessibilityBridgeImpl extends AccessibilityBridge {

        public AccessibilityBridgeImpl(UiHandler uiHandler) {
            super(mContext, createAccessibilityConfig(), uiHandler);
        }

        @Override
        public void ensureServiceEnabled() {
            AutoJs.this.ensureAccessibilityServiceEnabled();
        }

        @Override
        public void waitForServiceEnabled() {
            AutoJs.this.waitForAccessibilityServiceEnabled();
        }

        @Nullable
        @Override
        public AccessibilityService getService() {
            return AccessibilityService.Companion.getInstance();
        }

        @Override
        public ActivityInfoProvider getInfoProvider() {
            return mActivityInfoProvider;
        }

        @Override
        public AccessibilityNotificationObserver getNotificationObserver() {
            return mNotificationObserver;
        }

    }

    private class ScreenCaptureRequesterImpl extends ScreenCaptureRequester.AbstractScreenCaptureRequester {

        @Override
        public void setOnActivityResultCallback(Callback callback) {
            super.setOnActivityResultCallback((result, data) -> {
                mResult = data;
                callback.onRequestResult(result, data);
            });
        }

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
