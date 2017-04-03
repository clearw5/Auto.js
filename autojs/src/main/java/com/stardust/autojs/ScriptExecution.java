package com.stardust.autojs;

import com.stardust.autojs.engine.JavaScriptEngine;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.script.ScriptSource;

/**
 * Created by Stardust on 2017/4/3.
 */

public interface ScriptExecution {

    JavaScriptEngine getEngine();

    ScriptRuntime getRuntime();

    ScriptSource getSource();

    ScriptExecutionListener getListener();

    ExecutionConfig getConfig();
}
