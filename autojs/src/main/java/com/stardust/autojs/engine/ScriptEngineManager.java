package com.stardust.autojs.engine;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stardust.autojs.execution.ScriptExecution;
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
    public ScriptEngine createEngine(String name, int id) {
        Supplier<ScriptEngine> s = mEngineSuppliers.get(name);
        if (s == null) {
            return null;
        }
        ScriptEngine engine = s.get();
        engine.setId(id);
        putProperties(engine);
        addEngine(engine);
        return engine;
    }

    @Nullable
    public ScriptEngine createEngineOfSource(ScriptSource source, int id) {
        return createEngine(source.getEngineName(), id);
    }

    @NonNull
    public ScriptEngine createEngineOfSourceOrThrow(ScriptSource source, int id) {
        ScriptEngine engine = createEngineOfSource(source, id);
        if (engine == null)
            throw new ScriptEngineFactory.EngineNotFoundException("source: " + source.toString());
        return engine;
    }

    @NonNull
    public ScriptEngine createEngineOfSourceOrThrow(ScriptSource source) {
       return createEngineOfSourceOrThrow(source, ScriptExecution.NO_ID);
    }

    public void registerEngine(String name, Supplier<ScriptEngine> supplier) {
        mEngineSuppliers.put(name, supplier);
    }

    public void unregisterEngine(String name) {
        mEngineSuppliers.remove(name);
    }

}
