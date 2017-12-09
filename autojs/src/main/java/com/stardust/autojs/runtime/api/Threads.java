package com.stardust.autojs.runtime.api;

import com.stardust.autojs.engine.RhinoJavaScriptEngine;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.concurrent.VolatileBox;
import com.stardust.concurrent.VolatileDispose;
import com.stardust.lang.ThreadCompat;
import com.stardust.pio.PFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Stardust on 2017/12/3.
 */

public class Threads {

    private List<ThreadCompat> mThreads = new ArrayList<>();
    private ScriptRuntime mScriptRuntime;

    public Threads(ScriptRuntime scriptRuntime) {
        mScriptRuntime = scriptRuntime;
    }

    public void start(Runnable runnable) {
        ThreadCompat threadCompat = new ThreadCompat(() -> {
            try {
                ((RhinoJavaScriptEngine) mScriptRuntime.engines.myEngine()).createContext();
                runnable.run();
            } catch (Exception e) {
                if (!ScriptInterruptedException.causedByInterrupted(e)) {
                    mScriptRuntime.console.error(e);
                }
            }
        });
        mThreads.add(threadCompat);
        threadCompat.start();
    }

    public VolatileBox variable() {
        return new VolatileBox();
    }

    public VolatileDispose disposable() {
        return new VolatileDispose();
    }

    public List list() {
        return Collections.synchronizedList(new ArrayList<>());
    }

    public Set set() {
        return new ConcurrentSkipListSet();
    }

    public Map map() {
        return new ConcurrentHashMap();
    }

    public AtomicInteger atomicInt() {
        return new AtomicInteger();
    }

    public void shutDownAll() {
        for (ThreadCompat threadCompat : mThreads) {
            threadCompat.interrupt();
        }
        mThreads.clear();
    }


}
