package com.tony.autojs.tool.encrypt;

import android.app.Activity;

import com.stardust.autojs.execution.ExecutionConfig;
import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.autojs.runtime.api.Engines;

import java.lang.reflect.Method;

public final class EnginesDelegate {
    private Engines originEngine;

    public EnginesDelegate(Engines originEngine) {
        this.originEngine = originEngine;
    }

    public ScriptExecution execScript(String name, String script, ExecutionConfig config) {
        return originEngine.execScript(name, script, config);
    }

    public void execScript(Activity activity, String name, String script, ExecutionConfig config) {
        try {
            Method execScriptMethod = originEngine.getClass().getMethod("execScript", Activity.class, String.class, String.class, ExecutionConfig.class);
            execScriptMethod.invoke(originEngine, activity, name, script, config);
        } catch (Exception e) {
        }
    }
}
