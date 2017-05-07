package com.stardust.autojs.engine;

import android.content.Context;

import com.stardust.autojs.BuildConfig;
import com.stardust.autojs.script.ScriptSource;
import com.stardust.autojs.script.StringScriptSource;
import com.stardust.pio.PFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Stardust on 2017/1/27.
 */

public abstract class AbstractScriptEngineManager implements ScriptEngineManager {

    private static final String TAG = "AbstractScriptEngineManager";

    private Map<String, Object> mGlobalVariableMap = new HashMap<>();
    private final Set<ScriptEngine> mEngines = new HashSet<>();
    private EngineLifecycleCallback mEngineLifecycleCallback;

    private android.content.Context mContext;

    public AbstractScriptEngineManager(Context context) {
        mContext = context;
    }

    public ScriptEngine createEngine() {
        ScriptEngine engine = createEngineInner();
        putProperties(engine);
        addEngine(engine);
        return engine;
    }

    private void addEngine(ScriptEngine engine) {
        synchronized (mEngines) {
            mEngines.add(engine);
            if (mEngineLifecycleCallback != null) {
                mEngineLifecycleCallback.onEngineCreate(engine);
            }
        }
    }

    public void putGlobal(String varName, Object value) {
        mGlobalVariableMap.put(varName, value);
    }

    public void setEngineLifecycleCallback(EngineLifecycleCallback engineLifecycleCallback) {
        mEngineLifecycleCallback = engineLifecycleCallback;
    }

    public Set<ScriptEngine> getEngines() {
        return mEngines;
    }

    protected abstract ScriptEngine createEngineInner();

    public abstract String[] getGlobalFunctions();

    public android.content.Context getContext() {
        return mContext;
    }

    protected void putProperties(ScriptEngine engine) {
        for (Map.Entry<String, Object> variable : mGlobalVariableMap.entrySet()) {
            engine.put(variable.getKey(), variable.getValue());
        }
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
}
