package com.stardust.autojs.runtime;

import android.content.Context;
import android.os.Build;
import android.support.annotation.CallSuper;

import com.stardust.autojs.engine.ScriptEngine;
import com.stardust.autojs.runtime.api.AbstractShell;
import com.stardust.autojs.runtime.api.AppUtils;
import com.stardust.autojs.runtime.api.Loopers;
import com.stardust.autojs.runtime.api.ScriptBridges;
import com.stardust.autojs.runtime.api.Console;
import com.stardust.autojs.runtime.api.Events;
import com.stardust.autojs.runtime.api.Timers;
import com.stardust.autojs.runtime.api.UiSelector;
import com.stardust.autojs.runtime.api.image.Images;
import com.stardust.autojs.runtime.api.image.ScreenCaptureRequester;
import com.stardust.autojs.runtime.api.ui.Dialogs;
import com.stardust.autojs.runtime.api.ui.UI;
import com.stardust.autojs.runtime.simpleaction.SimpleActionAutomator;
import com.stardust.util.ScreenMetrics;
import com.stardust.util.UiHandler;
import com.stardust.view.accessibility.AccessibilityInfoProvider;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Stardust on 2017/5/4.
 */

public abstract class AbstractScriptRuntime {

    @ScriptVariable
    public AppUtils app;

    @ScriptVariable
    public Console console;

    @ScriptVariable
    public SimpleActionAutomator automator;

    @ScriptVariable
    public AccessibilityInfoProvider info;

    @ScriptVariable
    public UI ui;

    @ScriptVariable
    public Dialogs dialogs;

    @ScriptVariable
    public Events events;

    @ScriptVariable
    public ScriptBridges bridges = new ScriptBridges();

    @ScriptVariable
    public Loopers loopers;

    @ScriptVariable
    public Timers timers;

    @ScriptVariable
    public AccessibilityBridge accessibilityBridge;

    private Images images;

    private UiHandler mUiHandler;
    private static WeakReference<Context> applicationContext;
    private Map<String, Object> mProperties = new ConcurrentHashMap<>();

    public AbstractScriptRuntime(UiHandler uiHandler, Console console, AccessibilityBridge bridge, AppUtils appUtils, ScreenCaptureRequester screenCaptureRequester) {
        this.app = appUtils;
        this.console = console;
        accessibilityBridge = bridge;
        mUiHandler = uiHandler;
        this.automator = new SimpleActionAutomator(bridge, this);
        this.info = bridge.getInfoProvider();
        Context context = uiHandler.getContext();
        this.ui = new UI(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            images = new Images(context, this, screenCaptureRequester);
        }
        dialogs = new Dialogs(app, uiHandler);
    }

    /**
     * Call in init.js
     */
    @CallSuper
    public void init() {
        if (loopers != null)
            throw new IllegalStateException("already initialized");
        loopers = new Loopers();
        events = new Events(mUiHandler.getContext(), accessibilityBridge, bridges, loopers);
        timers = new Timers(bridges);
    }

    public static void setApplicationContext(Context context) {
        applicationContext = new WeakReference<>(context);
    }

    public static Context getApplicationContext() {
        if (applicationContext == null || applicationContext.get() == null) {
            throw new ScriptEnvironmentException("No application context");
        }
        return applicationContext.get();
    }

    @ScriptInterface
    public abstract void toast(final String text);

    @ScriptInterface
    public abstract void sleep(long millis);

    @ScriptInterface
    public abstract void setClip(final String text);

    @ScriptInterface
    public abstract AbstractShell getRootShell();

    @ScriptInterface
    public abstract AbstractShell.Result shell(String cmd, int root);

    @ScriptInterface
    public abstract UiSelector selector(ScriptEngine engine);

    @ScriptInterface
    public abstract boolean isStopped();

    @ScriptInterface
    public abstract void requiresApi(int i);

    @ScriptInterface
    public abstract void loadJar(String path);

    @ScriptInterface
    public abstract void exit();

    @ScriptInterface
    public abstract void setScreenMetrics(int width, int height);

    @ScriptInterface
    public abstract ScreenMetrics getScreenMetrics();

    public abstract void ensureAccessibilityServiceEnabled();

    @CallSuper
    public void onExit() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            images.releaseScreenCapturer();
        }
        if (events != null)
            events.recycle();
        if (loopers != null)
            loopers.quitAll();
    }

    public Object getImages() {
        return images;
    }

    public Object getProperty(String key) {
        return mProperties.get(key);
    }

    public Object putProperty(String key, Object value) {
        return mProperties.put(key, value);
    }

    public Object removeProperty(String key) {
        return mProperties.remove(key);
    }

}
