package com.stardust.autojs.engine;

import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.script.JavaScriptSource;

/**
 * Created by Stardust on 2017/8/3.
 */

public abstract class JavaScriptEngine extends ScriptEngine.AbstractScriptEngine<JavaScriptSource> {
    private ScriptRuntime mRuntime;


    public ScriptRuntime getRuntime() {
        return mRuntime;
    }

    public void setRuntime(ScriptRuntime runtime) {
        mRuntime = runtime;
        put("__runtime__", runtime);
    }


}
