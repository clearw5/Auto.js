package com.stardust.autojs.engine;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stardust.autojs.script.ScriptSource;
import com.stardust.util.Supplier;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stardust on 2017/8/2.
 */

public class ScriptEngineFactory {

    public static class EngineNotFoundException extends RuntimeException {

        public EngineNotFoundException(String s) {
            super(s);
        }
    }

    private static ScriptEngineFactory sInstance = new ScriptEngineFactory();
    private Map<String, Supplier<ScriptEngine>> mEngines = new HashMap<>();
    private Map<String, Object> mGlobalVariableMap = new HashMap<>();

    ScriptEngineFactory() {

    }

    public static ScriptEngineFactory getInstance() {
        return sInstance;
    }

    public void putGlobal(String varName, Object value) {
        mGlobalVariableMap.put(varName, value);
    }

    protected void putProperties(ScriptEngine engine) {
        for (Map.Entry<String, Object> variable : mGlobalVariableMap.entrySet()) {
            engine.put(variable.getKey(), variable.getValue());
        }
    }


    @Nullable
    public ScriptEngine createEngine(String name) {
        Supplier<ScriptEngine> s = mEngines.get(name);
        if (s == null) {
            return null;
        }
        ScriptEngine engine = s.get();
        putProperties(engine);
        return engine;
    }

    @Nullable
    public ScriptEngine createEngineOfSource(ScriptSource source) {
        return createEngine(source.getEngineName());
    }


    @NonNull
    public ScriptEngine createEngineByNameOrThrow(String name) {
        ScriptEngine engine = createEngine(name);
        if (engine == null)
            throw new EngineNotFoundException("name: " + name);
        return engine;
    }

    @NonNull
    public ScriptEngine createEngineOfSourceOrThrow(ScriptSource source) {
        ScriptEngine engine = createEngineOfSource(source);
        if (engine == null)
            throw new EngineNotFoundException("source: " + source.toString());
        return engine;
    }

    public void registerEngine(String name, Supplier<ScriptEngine> supplier) {
        mEngines.put(name, supplier);
    }

    public void unregisterEngine(String name) {
        mEngines.remove(name);
    }

}
