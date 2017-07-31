package com.stardust.autojs.engine;

import com.stardust.autojs.runtime.ScriptException;
import com.stardust.autojs.script.ScriptSource;

/**
 * Created by Stardust on 2017/4/2.
 * <p>
 * <p>
 * A ScriptEngine is created by {@link AbstractScriptEngineManager#createEngine()}, and then can be
 * used to execute script with {@link ScriptEngine#execute(ScriptSource)} in the **same** thread.
 * When the execution finish successfully, the engine should be destroy in the thread that created it.
 * <p>
 * If you want to stop the engine in other threads, you should call {@link ScriptEngine#forceStop()}.
 * It will throw a {@link ScriptException}.
 */

public interface ScriptEngine {


    void put(String name, Object value);

    Object execute(ScriptSource scriptSource);

    void forceStop();

    void destroy();

    boolean isDestroyed();

    void setTag(String key, Object value);

    Object getTag(String key);

    /**
     * @hide
     */
    void init();
}
