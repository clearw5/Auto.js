package com.stardust.autojs.core.looper;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.CallSuper;

import com.stardust.autojs.engine.RhinoJavaScriptEngine;
import com.stardust.autojs.runtime.ScriptBridges;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.concurrent.VolatileBox;
import com.stardust.lang.ThreadCompat;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Stardust on 2017/12/27.
 */

public class TimerThread extends ThreadCompat {

    private static ConcurrentHashMap<Thread, Timer> sTimerMap = new ConcurrentHashMap<>();

    private Timer mTimer;
    private final VolatileBox<Long> mMaxCallbackUptimeMillisForAllThreads;
    private final ScriptRuntime mRuntime;
    private Runnable mTarget;

    public TimerThread(ScriptRuntime runtime, VolatileBox<Long> maxCallbackUptimeMillisForAllThreads, Runnable target) {
        super(target);
        mRuntime = runtime;
        mTarget = target;
        mMaxCallbackUptimeMillisForAllThreads = maxCallbackUptimeMillisForAllThreads;
    }

    @Override
    public void run() {
        mRuntime.loopers.prepare();
        mTimer = new Timer(mRuntime.bridges, mMaxCallbackUptimeMillisForAllThreads);
        sTimerMap.put(Thread.currentThread(), mTimer);
        new Handler().post(mTarget);
        try {
            Looper.loop();
        } catch (Exception e) {
            if (!ScriptInterruptedException.causedByInterrupted(e)) {
                mRuntime.console.error(Thread.currentThread().toString() + ": " + e);
            }
        } finally {
            onExit();
            sTimerMap.remove(Thread.currentThread(), mTimer);
        }
    }

    @CallSuper
    protected void onExit() {
        mRuntime.loopers.notifyThreadExit(this);
    }

    public static Timer getTimerForThread(Thread thread) {
        return sTimerMap.get(thread);
    }

    public static Timer getTimerForCurrentThread() {
        return getTimerForThread(Thread.currentThread());
    }

    public int setTimeout(Object callback, long delay, Object... args) {
        return mTimer.setTimeout(callback, delay, args);
    }

    public boolean clearTimeout(int id) {
        return mTimer.clearTimeout(id);
    }

    public int setInterval(Object listener, long interval, Object... args) {
        return mTimer.setInterval(listener, interval, args);
    }

    public boolean clearInterval(int id) {
        return mTimer.clearInterval(id);
    }

    public int setImmediate(Object listener, Object... args) {
        return mTimer.setImmediate(listener, args);
    }

    public boolean clearImmediate(int id) {
        return mTimer.clearImmediate(id);
    }
}
