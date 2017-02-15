package com.stardust.scriptdroid.droid.script;

import com.efurture.script.JSTransformer;
import com.furture.react.DuktapeEngine;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.droid.Droid;
import com.stardust.scriptdroid.droid.runtime.api.IDroidRuntime;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Stardust on 2017/1/27.
 */

public class DuktapeJavaScriptEngine implements JavaScriptEngine {


    private final Map<Thread, DuktapeEngine> mThreadDuktapeEngineMap = new Hashtable<>();
    private static final String INIT_SCRIPT;
    private Map<String, Object> mVariableMap = new HashMap<>();

    static {
        try {
            INIT_SCRIPT = JSTransformer.parse(new StringReader(Init.INIT_SCRIPT));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public DuktapeJavaScriptEngine(IDroidRuntime runtime) {
        setRuntime(runtime);
    }

    private void setRuntime(IDroidRuntime runtime) {
        set("droid", IDroidRuntime.class, runtime);
    }

    @Override
    public Object execute(String script) throws IOException {
        DuktapeEngine duktapeEngine = new DuktapeEngine();
        init(duktapeEngine);
        add(duktapeEngine, Thread.currentThread());
        Object result;
        try {
            result = duktapeEngine.execute(JSTransformer.parse(new StringReader(script)));
        } catch (IOException e) {
            removeAndStop(Thread.currentThread());
            throw e;
        }
        if (!script.startsWith(Droid.UI))
            removeAndDestroy(Thread.currentThread());
        return result;
    }


    private void init(DuktapeEngine duktapeEngine) {
        duktapeEngine.put("context", App.getApp());
        duktapeEngine.execute(INIT_SCRIPT);
        for (Map.Entry<String, Object> variable : mVariableMap.entrySet()) {
            duktapeEngine.put(variable.getKey(), variable.getValue());
        }

    }

    public void removeAndStop(Thread thread) {
        synchronized (mThreadDuktapeEngineMap) {
            DuktapeEngine engine = mThreadDuktapeEngineMap.remove(thread);
            forceStop(engine, thread);
        }
    }


    @Override
    public void removeAndDestroy(Thread thread) {
        synchronized (mThreadDuktapeEngineMap) {
            DuktapeEngine engine = mThreadDuktapeEngineMap.remove(thread);
            if (engine != null)
                engine.destory();
        }
    }


    private void forceStop(final DuktapeEngine engine, Thread thread) {
        try {
            thread.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (engine != null) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    engine.destory();
                }
            }, 1000);
        }
    }

    private void add(DuktapeEngine duktapeEngine, Thread thread) {
        synchronized (mThreadDuktapeEngineMap) {
            mThreadDuktapeEngineMap.put(thread, duktapeEngine);
        }
    }

    @Override
    public <T> void set(String varName, Class<T> c, T value) {
        mVariableMap.put(varName, value);
    }

    @Override
    public int stopAll() {
        int n;
        synchronized (mThreadDuktapeEngineMap) {
            for (Map.Entry<Thread, DuktapeEngine> entry : mThreadDuktapeEngineMap.entrySet()) {
                forceStop(entry.getValue(), entry.getKey());
            }
            n = mThreadDuktapeEngineMap.size();
            mThreadDuktapeEngineMap.clear();
        }
        return n;
    }

}
