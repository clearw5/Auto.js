package com.stardust.autojs.runtime;

import com.stardust.autojs.engine.ScriptEngine;
import com.stardust.autojs.runtime.api.AbstractShell;
import com.stardust.autojs.runtime.api.AppUtils;
import com.stardust.autojs.runtime.api.Console;
import com.stardust.autojs.runtime.api.UiSelector;
import com.stardust.autojs.runtime.simple_action.SimpleActionAutomator;
import com.stardust.view.accessibility.AccessibilityInfoProvider;

/**
 * Created by Stardust on 2017/5/4.
 */

public abstract class AbstractScriptRuntime {

    @JavascriptField
    public  AppUtils app;

    @JavascriptField
    public  Console console;

    @JavascriptField
    public  SimpleActionAutomator automator;

    @JavascriptField
    public  AccessibilityInfoProvider info;

    public AbstractScriptRuntime(AppUtils app, Console console, AccessibilityBridge bridge) {
        this.app = app;
        this.console = console;
        this.automator = new SimpleActionAutomator(bridge, this);
        this.info = bridge.getInfoProvider();
    }

    public AbstractScriptRuntime() {
    }

    @JavascriptInterface
    public abstract void toast(final String text);

    @JavascriptInterface
    public abstract void sleep(long millis);

    @JavascriptInterface
    public abstract void setClip(final String text);

    @JavascriptInterface
    public abstract void shellExecAsync(String cmd);

    @JavascriptInterface
    public abstract AbstractShell.Result shell(String cmd, int root);

    @JavascriptInterface
    public abstract UiSelector selector(ScriptEngine engine);

    @JavascriptInterface
    public abstract boolean isStopped();

    @JavascriptInterface
    public abstract void requiresApi(int i);

    @JavascriptInterface
    public abstract void loadJar(String path);

    @JavascriptInterface
    public abstract void stop();

    public abstract void ensureAccessibilityServiceEnabled();
}
