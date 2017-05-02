package com.stardust.autojs;

import android.content.Context;

import com.stardust.autojs.engine.JavaScriptEngineManager;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.api.Console;
import com.stardust.util.Supplier;
import com.stardust.util.UiHandler;

/**
 * Created by Stardust on 2017/4/2.
 */

public class ScriptEngineServiceBuilder {

    Supplier<ScriptRuntime> mRuntimeSupplier;
    JavaScriptEngineManager mJavaScriptEngineManager;
    Console mGlobalConsole;
    UiHandler mUiHandler;

    public ScriptEngineServiceBuilder() {

    }

    public ScriptEngineServiceBuilder runtime(Supplier<ScriptRuntime> runtimeSupplier) {
        mRuntimeSupplier = runtimeSupplier;
        return this;
    }

    public ScriptEngineServiceBuilder uiHandler(UiHandler uiHandler) {
        mUiHandler = uiHandler;
        return this;
    }

    public ScriptEngineServiceBuilder engineManger(JavaScriptEngineManager manager) {
        mJavaScriptEngineManager = manager;
        return this;
    }

    public ScriptEngineServiceBuilder globalConsole(Console console) {
        mGlobalConsole = console;
        return this;
    }

    public ScriptEngineService build() {
        return new ScriptEngineService(this);
    }


}
