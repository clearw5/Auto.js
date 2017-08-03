package com.stardust.autojs.engine;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.stardust.autojs.script.ScriptSource;
import com.stardust.util.Supplier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Stardust on 2017/1/27.
 */

public class ScriptEngineManager {

    public interface EngineLifecycleCallback {

        void onEngineCreate(ScriptEngine engine);

        void onEngineRemove(ScriptEngine engine);
    }

    private static final String TAG = "ScriptEngineManager";

    private final Set<ScriptEngine> mEngines = new HashSet<>();
    private EngineLifecycleCallback mEngineLifecycleCallback;
    private Map<String, Supplier<ScriptEngine>> mEngineSuppliers = new HashMap<>();
    private Map<String, Object> mGlobalVariableMap = new HashMap<>();
    private android.content.Context mAndroidContext;
    private ScriptEngine.OnDestroyListener mOnEngineDestroyListener = new ScriptEngine.OnDestroyListener() {
        @Override
        public void onDestroy(ScriptEngine engine) {
            removeEngine(engine);
        }
    };

    public ScriptEngineManager(Context androidContext) {
        mAndroidContext = androidContext;
    }

    private void addEngine(ScriptEngine engine) {
        engine.setOnDestroyListener(mOnEngineDestroyListener);
        synchronized (mEngines) {
            mEngines.add(engine);
            if (mEngineLifecycleCallback != null) {
                mEngineLifecycleCallback.onEngineCreate(engine);
            }
        }
    }

    public void setEngineLifecycleCallback(EngineLifecycleCallback engineLifecycleCallback) {
        mEngineLifecycleCallback = engineLifecycleCallback;
    }

    public Set<ScriptEngine> getEngines() {
        return mEngines;
    }

    public android.content.Context getAndroidContext() {
        return mAndroidContext;
    }

    public void removeEngine(ScriptEngine engine) {
        synchronized (mEngines) {
            if (mEngines.remove(engine) && mEngineLifecycleCallback != null) {
                mEngineLifecycleCallback.onEngineRemove(engine);
            }
        }
    }

    public int stopAll() {
        synchronized (mEngines) {
            int n = mEngines.size();
            for (ScriptEngine engine : mEngines) {
                engine.forceStop();
            }
            return n;
        }
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
        Supplier<ScriptEngine> s = mEngineSuppliers.get(name);
        if (s == null) {
            return null;
        }
        ScriptEngine engine = s.get();
        putProperties(engine);
        addEngine(engine);
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
            throw new ScriptEngineFactory.EngineNotFoundException("name: " + name);
        return engine;
    }

    @NonNull
    public ScriptEngine createEngineOfSourceOrThrow(ScriptSource source) {
        ScriptEngine engine = createEngineOfSource(source);
        if (engine == null)
            throw new ScriptEngineFactory.EngineNotFoundException("source: " + source.toString());
        return engine;
    }

    public void registerEngine(String name, Supplier<ScriptEngine> supplier) {
        mEngineSuppliers.put(name, supplier);
    }

    public void unregisterEngine(String name) {
        mEngineSuppliers.remove(name);
    }

}
