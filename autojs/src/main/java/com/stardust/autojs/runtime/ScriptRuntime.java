package com.stardust.autojs.runtime;

import android.os.Build;

import com.stardust.autojs.R;
import com.stardust.autojs.engine.ScriptEngine;
import com.stardust.autojs.rhino_android.AndroidClassLoader;
import com.stardust.autojs.runtime.api.AbstractShell;
import com.stardust.autojs.runtime.api.AppUtils;
import com.stardust.autojs.runtime.api.Console;
import com.stardust.autojs.runtime.api.UiSelector;
import com.stardust.pio.UncheckedIOException;
import com.stardust.util.ClipboardUtil;
import com.stardust.autojs.runtime.api.ProcessShell;
import com.stardust.util.SdkVersionUtil;
import com.stardust.util.UiHandler;

import org.mozilla.javascript.ContextFactory;

import java.io.File;
import java.io.IOException;


/**
 * Created by Stardust on 2017/1/27.
 */

public class ScriptRuntime extends AbstractScriptRuntime {

    private static final String TAG = "ScriptRuntime";

    private UiHandler mUiHandler;
    private AccessibilityBridge mAccessibilityBridge;

    private AbstractShell mRootShell;

    public ScriptRuntime(UiHandler uiHandler, Console console, AccessibilityBridge accessibilityBridge) {
        super(new AppUtils(uiHandler.getContext()), console, accessibilityBridge);
        mAccessibilityBridge = accessibilityBridge;
        mUiHandler = uiHandler;
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

    public void shellExecAsync(String cmd) {
        if (mRootShell == null) {
            mRootShell = new ProcessShell(true);
        }
        mRootShell.exec(cmd);
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
        Thread.interrupted();
    }

    public void ensureAccessibilityServiceEnabled() {
        mAccessibilityBridge.ensureServiceEnabled();
    }
}
