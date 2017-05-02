package com.stardust.autojs;

import android.content.Context;

import com.stardust.autojs.engine.JavaScriptEngineManager;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.api.Console;
import com.stardust.util.Supplier;

/**
 * Created by Stardust on 2017/4/2.
 */

public class ScriptEngineServiceBuilder {

    Supplier<ScriptRuntime> mRuntimeSupplier;
    Context mContext;
    JavaScriptEngineManager mJavaScriptEngineManager;

    public ScriptEngineServiceBuilder() {

    }

    public ScriptEngineServiceBuilder runtime(Supplier<ScriptRuntime> runtimeSupplier) {
        mRuntimeSupplier = runtimeSupplier;
        return this;
    }

    public ScriptEngineServiceBuilder context(Context context) {
        mContext = context;
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
