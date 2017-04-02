package com.stardust.autojs.runtime;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.stardust.autojs.runtime.action.ActionAutomator;
import com.stardust.autojs.runtime.api.AppUtils;
import com.stardust.autojs.runtime.api.Console;
import com.stardust.autojs.runtime.api.UiSelector;
import com.stardust.util.Shell;

;

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
    public ActionAutomator automator;

    public ScriptRuntime(Context context, Console console, AccessibilityBridge accessibilityBridge) {
        mContext = context;
        mAccessibilityBridge = accessibilityBridge;
        mUIHandler = new Handler(mContext.getMainLooper());
        app = new AppUtils(context);
        this.console = console;
        automator = new ActionAutomator(accessibilityBridge, this);
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
            throw new ScriptStopException(e);
        }
    }

    @JavascriptInterface
    public void setClip(final String text) {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                ((ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText(TAG, text));
            }
        });
    }

    @JavascriptInterface
    public Shell.CommandResult shell(String cmd, int root) {
        return Shell.execCommand(cmd, root != 0);
    }

    @JavascriptInterface
    public UiSelector selector() {
        return new UiSelector(mAccessibilityBridge);
    }

    @JavascriptInterface
    public boolean isStopped() {
        return Thread.currentThread().isInterrupted();
    }

    @JavascriptInterface
    public void stop() {
        Thread.interrupted();
    }

    public void stoppedByInterrupted(InterruptedException e) {
        throw new ScriptStopException(e);
    }

    public void ensureAccessibilityServiceEnabled() {
        mAccessibilityBridge.ensureServiceEnabled();
    }
}
