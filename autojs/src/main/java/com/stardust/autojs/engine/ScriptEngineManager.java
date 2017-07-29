package com.stardust.autojs.engine;

import java.util.Set;

/**
 * Created by Stardust on 2017/5/7.
 */

public interface ScriptEngineManager {


    interface EngineLifecycleCallback {

        void onEngineCreate(ScriptEngine engine);

        void onEngineRemove(ScriptEngine engine);
    }

    ScriptEngine createEngine();

    void putGlobal(String varName, Object value);

    void setEngineLifecycleCallback(EngineLifecycleCallback engineLifecycleCallback);

    void removeEngine(ScriptEngine engine);

    Set<ScriptEngine> getEngines();

    int stopAll();

}
