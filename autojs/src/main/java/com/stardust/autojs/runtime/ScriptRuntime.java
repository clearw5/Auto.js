package com.stardust.autojs.runtime;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.widget.Toast;

import com.stardust.autojs.R;
import com.stardust.autojs.engine.JavaScriptEngine;
import com.stardust.autojs.rhino_android.AndroidClassLoader;
import com.stardust.autojs.runtime.api.AbstractShell;
import com.stardust.autojs.runtime.api.SlowShell;
import com.stardust.autojs.runtime.simple_action.SimpleActionAutomator;
import com.stardust.autojs.runtime.api.AppUtils;
import com.stardust.autojs.runtime.api.Console;
import com.stardust.autojs.runtime.api.UiSelector;
import com.stardust.pio.UncheckedIOException;
import com.stardust.util.ClipboardUtil;
import com.stardust.util.SdkVersionUtil;
import com.stardust.util.Shell;
import com.stardust.view.accessibility.AccessibilityInfoProvider;
import com.stardust.view.accessibility.AccessibilityNodeInfoAllocator;

import org.mozilla.javascript.ContextFactory;

import java.io.File;
import java.io.IOException;


/**
 * Created by Stardust on 2017/1/27.
 */

public class ScriptRuntime {

    private static final String TAG = "ScriptRuntime";

    private Handler mUIHandler;
    private Context mContext;
    private AccessibilityBridge mAccessibilityBridge;

    @JavascriptField
    public AppUtils app;

    @JavascriptField
    public Console console;

    @JavascriptField
    public SimpleActionAutomator automator;

    @JavascriptField
    public AccessibilityInfoProvider info;

    private AbstractShell mRootShell;

    public ScriptRuntime(Context context, Console console, AccessibilityBridge accessibilityBridge) {
        mContext = context;
        mAccessibilityBridge = accessibilityBridge;
        mUIHandler = new Handler(mContext.getMainLooper());
        app = new AppUtils(context);
        info = accessibilityBridge.getInfoProvider();
        this.console = console;
        automator = new SimpleActionAutomator(accessibilityBridge, this);
    }

    @JavascriptInterface
    public void toast(final String text) {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @JavascriptInterface
    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new ScriptInterrupptedException();
        }
    }

    @JavascriptInterface
    public void setClip(final String text) {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                ClipboardUtil.setClip(mContext, text);
            }
        });
    }


    @JavascriptInterface
    public void shellExecNotReturnResultWithRoot(String cmd){
        if(mRootShell == null){
            mRootShell = new SlowShell(true);
        }
        mRootShell.exec(cmd);
    }

    @JavascriptInterface
    public Shell.CommandResult shell(String cmd, int root) {
        return Shell.execCommand(cmd, root != 0);
    }

    @JavascriptInterface
    public UiSelector selector(JavaScriptEngine engine) {
        AccessibilityNodeInfoAllocator allocator = (AccessibilityNodeInfoAllocator) engine.getTag("allocator");
        if (allocator == null) {
            allocator = new AccessibilityNodeInfoAllocator();
            engine.setTag("allocator", allocator);
        }
        return new UiSelector(mAccessibilityBridge, allocator);
    }

    @JavascriptInterface
    public boolean isStopped() {
        return Thread.currentThread().isInterrupted();
    }

    @JavascriptInterface
    public void requiresApi(int i) {
        if (Build.VERSION.SDK_INT < i) {
            throw new ScriptStopException(mContext.getString(R.string.text_requires_sdk_version_to_run_the_script) + SdkVersionUtil.sdkIntToString(i));
        }
    }

    @JavascriptInterface
    public void loadJar(String path) {
        try {
            ((AndroidClassLoader) ContextFactory.getGlobal().getApplicationClassLoader()).loadJar(new File(path));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @JavascriptInterface
    public void stop() {
        Thread.interrupted();
    }

    public void ensureAccessibilityServiceEnabled() {
        mAccessibilityBridge.ensureServiceEnabled();
    }
}
