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

public abstract class JavaScriptEngineManager {

    public interface EngineLifecycleCallback {

        void onEngineCreate(JavaScriptEngine engine);

        void onEngineRemove(JavaScriptEngine engine);
    }

    private static final String TAG = "JavaScriptEngineManager";

    private Map<String, Object> mGlobalVariableMap = new HashMap<>();
    private final Set<JavaScriptEngine> mEngines = new HashSet<>();
    private boolean mIsStopping = false;
    private EngineLifecycleCallback mEngineLifecycleCallback;
    private final ScriptSource INIT_SCRIPT;

    private android.content.Context mContext;

    public JavaScriptEngineManager(Context context) {
        mContext = context;
        INIT_SCRIPT = new StringScriptSource(readInitScript());
    }

    public JavaScriptEngine createEngine() {
        JavaScriptEngine engine = createEngineInner();
        putProperties(engine);
        engine.init();
        synchronized (mEngines) {
            mEngines.add(engine);
            if(mEngineLifecycleCallback != null){
                mEngineLifecycleCallback.onEngineCreate(engine);
            }
        }
        return engine;
    }

    public void put(String varName, Object value) {
        mGlobalVariableMap.put(varName, value);
    }

    public void setEngineLifecycleCallback(EngineLifecycleCallback engineLifecycleCallback) {
        mEngineLifecycleCallback = engineLifecycleCallback;
    }

    public Set<JavaScriptEngine> getEngines() {
        return mEngines;
    }

    protected abstract JavaScriptEngine createEngineInner();

    public abstract String[] getGlobalFunctions();

    public android.content.Context getContext() {
        return mContext;
    }

    protected void putProperties(JavaScriptEngine engine) {
        for (Map.Entry<String, Object> variable : mGlobalVariableMap.entrySet()) {
            engine.put(variable.getKey(), variable.getValue());
        }
    }

    void removeEngine(JavaScriptEngine engine) {
        synchronized (mEngines) {
            if (mIsStopping)
                return;
            mEngines.remove(engine);
            if(mEngineLifecycleCallback != null){
                mEngineLifecycleCallback.onEngineRemove(engine);
            }
        }
    }

    private String readInitScript() {
        try {
            return PFile.read(mContext.getAssets().open("javascript_engine_init.js"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ScriptSource getInitScript() {
        if (BuildConfig.DEBUG) {
            // 调试时不缓存INIT_SCRIPT否则修改javascript_engine_init.js后不会更新
            return new StringScriptSource(readInitScript());
        } else {
            return INIT_SCRIPT;
        }
    }


    public int stopAll() {
        synchronized (mEngines) {
            mIsStopping = true;
            int n = mEngines.size();
            for (JavaScriptEngine engine : mEngines) {
                engine.forceStop();
                if(mEngineLifecycleCallback != null){
                    mEngineLifecycleCallback.onEngineRemove(engine);
                }
            }
            mEngines.clear();
            mIsStopping = false;
            return n;
        }
    }
}
