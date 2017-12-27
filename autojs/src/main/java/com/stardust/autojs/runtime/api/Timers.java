package com.stardust.autojs.runtime.api;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseArray;

import com.stardust.autojs.core.looper.Timer;
import com.stardust.autojs.core.looper.TimerThread;
import com.stardust.autojs.runtime.ScriptBridges;
import com.stardust.concurrent.VolatileBox;

/**
 * Created by Stardust on 2017/7/21.
 */

public class Timers {

    private static final String LOG_TAG = "Timers";

    private VolatileBox<Long> mMaxCallbackUptimeMillisForAllThreads = new VolatileBox<>(0L);
    private Thread mMainThread;
    private Timer mMainTimer;

    public Timers(ScriptBridges bridges) {
        mMainThread = Thread.currentThread();
        mMainTimer = new Timer(bridges, mMaxCallbackUptimeMillisForAllThreads);
    }

    public VolatileBox<Long> getMaxCallbackUptimeMillisForAllThreads() {
        return mMaxCallbackUptimeMillisForAllThreads;
    }

    private Timer getTimerForCurrentThread() {
        if (Thread.currentThread() == mMainThread) {
            return mMainTimer;
        }
        return TimerThread.getTimerForCurrentThread();
    }

    public int setTimeout(Object callback, long delay, Object... args) {
        return getTimerForCurrentThread().setTimeout(callback, delay, args);
    }

    public boolean clearTimeout(int id) {
        return getTimerForCurrentThread().clearTimeout(id);
    }

    public int setInterval(Object listener, long interval, Object... args) {
        return getTimerForCurrentThread().setInterval(listener, interval, args);
    }

    public boolean clearInterval(int id) {
        return getTimerForCurrentThread().clearInterval(id);
    }

    public int setImmediate(Object listener, Object... args) {
        return getTimerForCurrentThread().setImmediate(listener, args);
    }

    public boolean clearImmediate(int id) {
        return getTimerForCurrentThread().clearImmediate(id);
    }

    public boolean hasPendingCallbacks() {
        //如果是脚本主线程，则检查所有子线程中的定时回调。mFutureCallbackUptimeMillis用来记录所有子线程中定时最久的一个。
        if (mMainThread == Thread.currentThread()) {
            Log.d(LOG_TAG, "[main thread]hasPendingCallbacks:" + (mMaxCallbackUptimeMillisForAllThreads.get() > SystemClock.uptimeMillis()));
            Log.d(LOG_TAG, "mMaxCallbackUptimeMillisForAllThreads:" + mMaxCallbackUptimeMillisForAllThreads.get());
            return mMaxCallbackUptimeMillisForAllThreads.get() > SystemClock.uptimeMillis();
        }
        //否则检查当前线程的定时回调
        return getTimerForCurrentThread().hasPendingCallbacks();
    }

}
