package com.stardust.autojs;

import android.content.Context;

import com.stardust.autojs.engine.JavaScriptEngineManager;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.api.Console;

/**
 * Created by Stardust on 2017/4/2.
 */

public class ScriptEngineServiceBuilder {

    ScriptRuntime mRuntime;
    Context mContext;
    Console mConsole;
    JavaScriptEngineManager mJavaScriptEngineManager;

    public ScriptEngineServiceBuilder() {

    }

    public ScriptEngineServiceBuilder runtime(ScriptRuntime runtime) {
        mRuntime = runtime;
        return this;
    }

    public ScriptEngineServiceBuilder context(Context context) {
        mContext = context;
        return this;
    }

    public ScriptEngineServiceBuilder console(Console console) {
        mConsole = console;
        return this;
    }

    public ScriptEngineServiceBuilder engineManger(JavaScriptEngineManager manager) {
        mJavaScriptEngineManager = manager;
        return this;
    }

    public ScriptEngineService build() {
        return new ScriptEngineService(this);
    }


}
