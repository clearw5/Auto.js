package com.stardust.autojs.engine;

import com.stardust.autojs.script.ScriptSource;

/**
 * Created by Stardust on 2017/4/2.
 * <p>
 * <p>
 * A JavaScriptEngine is created by {@link JavaScriptEngineManager#createEngine()}, and then can be
 * used to execute script with {@link JavaScriptEngine#execute(ScriptSource)} in the **same** thread.
 * When the execution finish successfully, the engine should be destroy in the thread that created it.
 * <p>
 * If you want to stop the engine in other threads, you should call {@link JavaScriptEngine#forceStop()}.
 * It will throw a {@link com.stardust.autojs.runtime.ScriptStopException}.
 */

public interface JavaScriptEngine {


    void put(String name, Object value);

    Object execute(ScriptSource scriptSource);

    ScriptSource getExecutedScript();

    void forceStop();

    void destroy();

    /**
     * @hide
     */
    void init();
}
