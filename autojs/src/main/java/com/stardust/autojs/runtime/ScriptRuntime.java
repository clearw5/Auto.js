package com.stardust.autojs.runtime;

import android.os.Build;
import android.os.Looper;

import com.stardust.autojs.R;
import com.stardust.autojs.engine.ScriptEngine;
import com.stardust.autojs.engine.preprocess.Preprocessor;
import com.stardust.autojs.rhino_android.AndroidClassLoader;
import com.stardust.autojs.runtime.api.AbstractShell;
import com.stardust.autojs.runtime.api.AppUtils;
import com.stardust.autojs.runtime.api.Console;
import com.stardust.autojs.runtime.api.UiSelector;
import com.stardust.concurrent.VolatileBox;
import com.stardust.autojs.runtime.api.ui.UI;
import com.stardust.pio.UncheckedIOException;
import com.stardust.util.ClipboardUtil;
import com.stardust.autojs.runtime.api.ProcessShell;
import com.stardust.util.ScreenMetrics;
import com.stardust.util.SdkVersionUtil;
import com.stardust.util.Supplier;
import com.stardust.util.UiHandler;

import org.mozilla.javascript.ContextFactory;

import java.io.File;
import java.io.IOException;


/**
 * Created by Stardust on 2017/1/27.
 */

public class ScriptRuntime extends AbstractScriptRuntime {

    private static final String TAG = "ScriptRuntime";

    public static class Builder {
        private AppUtils mAppUtils;
        private UiHandler mUiHandler;
        private Console mConsole;
        private AccessibilityBridge mAccessibilityBridge;
        private Supplier<AbstractShell> mShellSupplier;
        public UI mUi;


        public Builder() {

        }

        public Builder setAppUtils(AppUtils appUtils) {
            mAppUtils = appUtils;
            return this;
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

        public Builder setUI(UI ui) {
            mUi = ui;
            return this;
        }

        public ScriptRuntime build() {
            return new ScriptRuntime(this);
        }

    }

    private UiHandler mUiHandler;
    private AccessibilityBridge mAccessibilityBridge;

    private AbstractShell mRootShell;
    private Supplier<AbstractShell> mShellSupplier;
    private ScreenMetrics mScreenMetrics = new ScreenMetrics();


    protected ScriptRuntime(Builder builder) {
        super(builder.mAppUtils, builder.mConsole, builder.mAccessibilityBridge, builder.mUi);
        mAccessibilityBridge = builder.mAccessibilityBridge;
        mUiHandler = builder.mUiHandler;
        mShellSupplier = builder.mShellSupplier;
        if (ui == null) {
            ui = new UI(mUiHandler.getContext());
        }
        automator.setScreenMetrics(mScreenMetrics);
    }

    public UiHandler getUiHandler() {
        return mUiHandler;
    }

    public AccessibilityBridge getAccessibilityBridge() {
        return mAccessibilityBridge;
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
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                ClipboardUtil.setClip(mUiHandler.getContext(), text);
            }
        });
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
        return clip.blockedGet();
    }

    @Override
    public AbstractShell getRootShell() {
        ensureRootShell();
        return mRootShell;
    }

    private void ensureRootShell() {
        if (mRootShell == null) {
            if (mShellSupplier == null) {
                throw new ScriptInterruptedException();
            }
            mRootShell = mShellSupplier.get();
            mRootShell.SetScreenMetrics(mScreenMetrics);
            mShellSupplier = null;
        }
    }

    public AbstractShell.Result shell(String cmd, int root) {
        return ProcessShell.execCommand(cmd, root != 0);
    }

    public UiSelector selector(ScriptEngine engine) {
        return new UiSelector(mAccessibilityBridge);
    }

    public boolean isStopped() {
        return Thread.currentThread().isInterrupted();
    }

    public void requiresApi(int i) {
        if (Build.VERSION.SDK_INT < i) {
            throw new ScriptStopException(mUiHandler.getContext().getString(R.string.text_requires_sdk_version_to_run_the_script) + SdkVersionUtil.sdkIntToString(i));
        }
    }

    public void loadJar(String path) {
        try {
            ((AndroidClassLoader) ContextFactory.getGlobal().getApplicationClassLoader()).loadJar(new File(path));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void stop() {
        Thread.currentThread().interrupt();
        throw new ScriptInterruptedException();
    }


    @Override
    public void setScreenMetrics(int width, int height) {
        mScreenMetrics.setScreenMetrics(width, height);
    }

    @Override
    public ScreenMetrics getScreenMetrics() {
        return mScreenMetrics;
    }

    public void ensureAccessibilityServiceEnabled() {
        mAccessibilityBridge.ensureServiceEnabled();
    }

    @Override
    public void onStop() {
        if (mRootShell != null) {
            mRootShell.exit();
            mRootShell = null;
        }
        mShellSupplier = null;
    }
}
