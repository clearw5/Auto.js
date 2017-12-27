package com.stardust.autojs.runtime.api;

import android.support.annotation.NonNull;

import com.stardust.autojs.core.looper.TimerThread;
import com.stardust.autojs.engine.RhinoJavaScriptEngine;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.concurrent.VolatileBox;
import com.stardust.concurrent.VolatileDispose;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Stardust on 2017/12/3.
 */

public class Threads {

    private final HashSet<Thread> mThreads = new HashSet<>();
    private ScriptRuntime mRuntime;

    public Threads(ScriptRuntime runtime) {
        mRuntime = runtime;
    }

    public TimerThread start(Runnable runnable) {
        TimerThread thread = startThread(runnable);
        synchronized (mThreads) {
            mThreads.add(thread);
        }
        thread.start();
        return thread;
    }

    @NonNull
    private TimerThread startThread(Runnable runnable) {
        return new TimerThread(mRuntime, mRuntime.timers.getMaxCallbackUptimeMillisForAllThreads(),
                () -> {
                    ((RhinoJavaScriptEngine) mRuntime.engines.myEngine()).createContext();
                    runnable.run();
                }
        ) {
            @Override
            protected void onExit() {
                synchronized (mThreads) {
                    mThreads.remove(Thread.currentThread());
                }
                super.onExit();
            }
        };
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

    public AtomicLong atomic() {
        return new AtomicLong();
    }

    public AtomicLong atomic(long value) {
        return new AtomicLong(value);
    }

    public void shutDownAll() {
        synchronized (mThreads) {
            for (Thread thread : mThreads) {
                thread.interrupt();
            }
            mThreads.clear();
        }
    }


    public boolean hasRunningThreads() {
        synchronized (mThreads) {
            return !mThreads.isEmpty();
        }
    }
}
