package com.stardust.autojs.runtime;

import android.content.Context;
import android.os.Build;
import android.os.Looper;

import com.stardust.autojs.R;
import com.stardust.autojs.ScriptEngineService;
import com.stardust.autojs.annotation.ScriptVariable;
import com.stardust.autojs.core.accessibility.AccessibilityBridge;
import com.stardust.autojs.engine.ScriptEngine;
import com.stardust.autojs.rhino.AndroidClassLoader;
import com.stardust.autojs.runtime.api.AbstractShell;
import com.stardust.autojs.runtime.api.AppUtils;
import com.stardust.autojs.runtime.api.Console;
import com.stardust.autojs.runtime.api.Engines;
import com.stardust.autojs.runtime.api.Events;
import com.stardust.autojs.runtime.api.Loopers;
import com.stardust.autojs.runtime.api.Timers;
import com.stardust.autojs.runtime.api.UiSelector;
import com.stardust.autojs.runtime.api.image.Images;
import com.stardust.autojs.runtime.api.image.ScreenCaptureRequester;
import com.stardust.autojs.runtime.api.ui.Dialogs;
import com.stardust.autojs.runtime.exception.ScriptEnvironmentException;
import com.stardust.autojs.runtime.exception.ScriptException;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.autojs.core.accessibility.SimpleActionAutomator;
import com.stardust.concurrent.VolatileBox;
import com.stardust.autojs.runtime.api.ui.UI;
import com.stardust.pio.UncheckedIOException;
import com.stardust.util.ClipboardUtil;
import com.stardust.autojs.runtime.api.ProcessShell;
import com.stardust.util.ScreenMetrics;
import com.stardust.util.SdkVersionUtil;
import com.stardust.util.Supplier;
import com.stardust.util.UiHandler;
import com.stardust.view.accessibility.AccessibilityInfoProvider;

import org.mozilla.javascript.ContextFactory;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by Stardust on 2017/1/27.
 */

public class ScriptRuntime {

    private static final String TAG = "ScriptRuntime";


    public static class Builder {
        private UiHandler mUiHandler;
        private Console mConsole;
        private AccessibilityBridge mAccessibilityBridge;
        private Supplier<AbstractShell> mShellSupplier;
        private ScreenCaptureRequester mScreenCaptureRequester;
        private AppUtils mAppUtils;
        private ScriptEngineService mEngineService;

        public Builder() {

        }

        public Builder setUiHandler(UiHandler uiHandler) {
            mUiHandler = uiHandler;
            return this;
        }

        public Builder setConsole(Console console) {
            mConsole = console;
            return this;
        }

        public Builder setAccessibilityBridge(AccessibilityBridge accessibilityBridge) {
            mAccessibilityBridge = accessibilityBridge;
            return this;
        }

        public Builder setShellSupplier(Supplier<AbstractShell> shellSupplier) {
            mShellSupplier = shellSupplier;
            return this;
        }

        public Builder setScreenCaptureRequester(ScreenCaptureRequester requester) {
            mScreenCaptureRequester = requester;
            return this;
        }

        public Builder setAppUtils(AppUtils appUtils) {
            mAppUtils = appUtils;
            return this;
        }

        public Builder setEngineService(ScriptEngineService service) {
            mEngineService = service;
            return this;
        }


        public ScriptRuntime build() {
            return new ScriptRuntime(this);
        }

    }


    @ScriptVariable
    public final AppUtils app;

    @ScriptVariable
    public final Console console;

    @ScriptVariable
    public final SimpleActionAutomator automator;

    @ScriptVariable
    public final AccessibilityInfoProvider info;

    @ScriptVariable
    public final UI ui;

    @ScriptVariable
    public final Dialogs dialogs;

    @ScriptVariable
    public Events events;

    @ScriptVariable
    public final ScriptBridges bridges = new ScriptBridges();

    @ScriptVariable
    public Loopers loopers;

    @ScriptVariable
    public Timers timers;

    @ScriptVariable
    public final AccessibilityBridge accessibilityBridge;

    @ScriptVariable
    public final Engines engines;

    private Images images;

    private static WeakReference<Context> applicationContext;
    private Map<String, Object> mProperties = new ConcurrentHashMap<>();
    private UiHandler mUiHandler;
    private AbstractShell mRootShell;
    private Supplier<AbstractShell> mShellSupplier;
    private ScreenMetrics mScreenMetrics = new ScreenMetrics();


    protected ScriptRuntime(Builder builder) {
        app = builder.mAppUtils;
        mUiHandler = builder.mUiHandler;
        console = builder.mConsole;
        accessibilityBridge = builder.mAccessibilityBridge;
        mShellSupplier = builder.mShellSupplier;
        ui = new UI(mUiHandler.getContext());
        this.automator = new SimpleActionAutomator(accessibilityBridge, this);
        automator.setScreenMetrics(mScreenMetrics);
        this.info = accessibilityBridge.getInfoProvider();
        Context context = mUiHandler.getContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            images = new Images(context, this, builder.mScreenCaptureRequester);
        }
        engines = new Engines(builder.mEngineService);
        dialogs = new Dialogs(app, mUiHandler);
    }

    public void init() {
        if (loopers != null)
            throw new IllegalStateException("already initialized");
        timers = new Timers(bridges);
        loopers = new Loopers(timers);
        events = new Events(mUiHandler.getContext(), accessibilityBridge, bridges, loopers);
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

    public UiHandler getUiHandler() {
        return mUiHandler;
    }

    public AccessibilityBridge getAccessibilityBridge() {
        return accessibilityBridge;
    }

    public void toast(final String text) {
        mUiHandler.toast(text);
    }

    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new ScriptInterruptedException();
        }
    }

    public void setClip(final String text) {
        final Object lock = new Object();
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                ClipboardUtil.setClip(mUiHandler.getContext(), text);
                synchronized (lock) {
                    lock.notify();
                }
            }
        });
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                throw new ScriptInterruptedException();
            }
        }
    }

    public String getClip() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            return ClipboardUtil.getClipOrEmpty(mUiHandler.getContext()).toString();
        }
        final VolatileBox<String> clip = new VolatileBox<>("");
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                clip.setAndNotify(ClipboardUtil.getClipOrEmpty(mUiHandler.getContext()).toString());
            }
        });
        return clip.blockedGetOrThrow(ScriptInterruptedException.class);
    }

    public AbstractShell getRootShell() {
        ensureRootShell();
        return mRootShell;
    }

    private void ensureRootShell() {
        if (mRootShell == null) {
            mRootShell = mShellSupplier.get();
            mRootShell.SetScreenMetrics(mScreenMetrics);
            mShellSupplier = null;
        }
    }

    public AbstractShell.Result shell(String cmd, int root) {
        return ProcessShell.execCommand(cmd, root != 0);
    }

    public UiSelector selector(ScriptEngine engine) {
        return new UiSelector(accessibilityBridge);
    }

    public boolean isStopped() {
        return Thread.currentThread().isInterrupted();
    }

    public void requiresApi(int i) {
        if (Build.VERSION.SDK_INT < i) {
            throw new ScriptException(mUiHandler.getContext().getString(R.string.text_requires_sdk_version_to_run_the_script) + SdkVersionUtil.sdkIntToString(i));
        }
    }

    public void loadJar(String path) {
        try {
            ((AndroidClassLoader) ContextFactory.getGlobal().getApplicationClassLoader()).loadJar(new File(path));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void exit() {
        Thread.currentThread().interrupt();
        throw new ScriptInterruptedException();
    }

    @Deprecated
    public void stop() {
        exit();
    }


    public void setScreenMetrics(int width, int height) {
        mScreenMetrics.setScreenMetrics(width, height);
    }

    public ScreenMetrics getScreenMetrics() {
        return mScreenMetrics;
    }

    public void ensureAccessibilityServiceEnabled() {
        accessibilityBridge.ensureServiceEnabled();
    }

    public void onExit() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            images.releaseScreenCapturer();
        }
        if (mRootShell != null) {
            mRootShell.exitAndWaitFor();
        }
        mRootShell = null;
        mShellSupplier = null;
        if (events != null) {
            events.recycle();
        }
        if (loopers != null) {
            loopers.quitAll();
        }
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
