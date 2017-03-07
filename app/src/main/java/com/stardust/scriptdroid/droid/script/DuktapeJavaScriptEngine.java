package com.stardust.scriptdroid.droid.script;

import com.efurture.script.JSTransformer;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.droid.Droid;
import com.stardust.scriptdroid.droid.runtime.api.IDroidRuntime;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Stardust on 2017/1/27.
 */
/*
public class DuktapeJavaScriptEngine extends JavaScriptEngine {


    private final Map<Thread, DuktapeEngine> mThreadDuktapeEngineMap = new Hashtable<>();
    private static final String INIT_SCRIPT = parse(Init.getInitScript());
    private Map<String, Object> mVariableMap = new HashMap<>();

    public DuktapeJavaScriptEngine(IDroidRuntime runtime) {
        setRuntime(runtime);
    }

    private void setRuntime(IDroidRuntime runtime) {
        set("droid", runtime);
    }

    @Override
    public Object execute(String script) {
        DuktapeEngine duktapeEngine = new DuktapeEngine();
        init(duktapeEngine);
        add(duktapeEngine, Thread.currentThread());
        Object code = duktapeEngine.execute(parse(script));
        if (!script.startsWith(Droid.UI) && !script.startsWith(Droid.STAY))
            removeAndDestroy();
        return code;
    }

    private static String parse(String script) {
        try {
            return JSTransformer.parse(new StringReader(script));
        } catch (IOException e) {
            //Should not happen
            throw new RuntimeException(e);
        }
    }


    private void init(DuktapeEngine duktapeEngine) {
        duktapeEngine.put("context", App.getApp());
        duktapeEngine.execute(INIT_SCRIPT);
        for (Map.Entry<String, Object> variable : mVariableMap.entrySet()) {
            duktapeEngine.put(variable.getKey(), variable.getValue());
        }

    }


    @Override
    public void removeAndDestroy() {
        synchronized (mThreadDuktapeEngineMap) {
            DuktapeEngine engine = mThreadDuktapeEngineMap.remove(Thread.currentThread());
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

    private static final Field ptrField;

    static {
        Field field = null;
        try {
            field = DuktapeEngine.class.getDeclaredField("ptr");
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        ptrField = field;
    }

    public static boolean isDestroyed(DuktapeEngine engine) {
        try {
            return ((long) ptrField.get(engine)) == 0;
        } catch (IllegalAccessException e) {
            return true;
        }
    }
}
*/