package com.stardust.autojs.engine;

import com.stardust.autojs.script.ScriptSource;

import java.io.Serializable;

/**
 * Created by Stardust on 2017/4/2.
 */

public interface ScriptExecutionListener extends Serializable {

    void onStart(JavaScriptEngine engine, ScriptSource source);

    void onSuccess(JavaScriptEngine engine, ScriptSource source, Object result);

    void onException(JavaScriptEngine engine, ScriptSource source, Exception e);
}
