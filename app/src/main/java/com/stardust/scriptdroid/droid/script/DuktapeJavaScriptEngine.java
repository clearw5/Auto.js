package com.stardust.scriptdroid.droid.script;

import android.util.Pair;

import com.efurture.script.JSTransformer;
import com.furture.react.DuktapeEngine;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.droid.runtime.api.IDroidRuntime;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Stardust on 2017/1/27.
 */

public class DuktapeJavaScriptEngine implements JavaScriptEngine {


    private final List<Pair<DuktapeEngine, Thread>> mDuktapeEngineList = new ArrayList<>();
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
            remove(duktapeEngine);
            throw e;
        }
        remove(duktapeEngine);
        return result;
    }

    private void init(DuktapeEngine duktapeEngine) {
        duktapeEngine.put("context", App.getApp());
        duktapeEngine.execute(INIT_SCRIPT);
        for (Map.Entry<String, Object> variable : mVariableMap.entrySet()) {
            duktapeEngine.put(variable.getKey(), variable.getValue());
        }

    }

    private void remove(DuktapeEngine duktapeEngine) {
        duktapeEngine.destory();
        synchronized (mDuktapeEngineList) {
            Iterator<Pair<DuktapeEngine, Thread>> iterator = mDuktapeEngineList.iterator();
            while (iterator.hasNext()) {
                Pair<DuktapeEngine, Thread> pair = iterator.next();
                if (pair.first == duktapeEngine) {
                    stop(pair.first, pair.second);
                }
                iterator.remove();
                break;
            }
        }
    }

    private void stop(DuktapeEngine engine, Thread thread) {
        try {
            thread.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        engine.destory();
    }

    private void add(DuktapeEngine duktapeEngine, Thread thread) {
        synchronized (mDuktapeEngineList) {
            mDuktapeEngineList.add(0, new Pair<>(duktapeEngine, thread));
        }
    }

    @Override
    public <T> void set(String varName, Class<T> c, T value) {
        mVariableMap.put(varName, value);
    }

    @Override
    public int stopAll() {
        int n;
        synchronized (mDuktapeEngineList) {
            for (Pair<DuktapeEngine, Thread> pair : mDuktapeEngineList) {
                stop(pair.first, pair.second);
            }
            n = mDuktapeEngineList.size();
            mDuktapeEngineList.clear();
        }
        return n;
    }

}
