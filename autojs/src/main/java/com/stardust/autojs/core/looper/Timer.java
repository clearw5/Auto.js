package com.stardust.autojs.core.looper;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.concurrent.VolatileBox;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Stardust on 2017/12/27.
 */

public class Timer {

    private static final String LOG_TAG = "Timer";

    /**
     * SparseArray存在线程安全问题 改为ConcurrentHashMap
     */
    private Map<Integer, Runnable> mHandlerCallbacks = new ConcurrentHashMap<>();
    private AtomicInteger mCallbackMaxId = new AtomicInteger();
    private WeakReference<ScriptRuntime> mRuntime;
    private Handler mHandler;
    private long mMaxCallbackUptimeMillis = 0;
    private final VolatileBox<Long> mMaxCallbackMillisForAllThread;

    public Timer(ScriptRuntime runtime, VolatileBox<Long> maxCallbackMillisForAllThread) {
        mRuntime = new WeakReference<>(runtime);
        mMaxCallbackMillisForAllThread = maxCallbackMillisForAllThread;
        mHandler = new Handler();
    }

    public Timer(ScriptRuntime runtime, VolatileBox<Long> maxCallbackMillisForAllThread, Looper looper) {
        mRuntime = new WeakReference<>(runtime);
        mMaxCallbackMillisForAllThread = maxCallbackMillisForAllThread;
        mHandler = new Handler(looper);
    }

    public int setTimeout(final Object callback, final long delay, final Object... args) {
        final int id = mCallbackMaxId.getAndIncrement();
        Runnable r = () -> {
            callFunction(callback, null, args);
            mHandlerCallbacks.remove(id);
        };
        mHandlerCallbacks.put(id, r);
        postDelayed(r, delay);
        return id;
    }

    private void callFunction(Object callback, Object thiz, Object[] args) {
        ScriptRuntime runtime = mRuntime.get();
        if (runtime == null) {
            return;
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            try {
                runtime.bridges.callFunction(callback, thiz, args);
            } catch (Exception e) {
                runtime.exit(e);
            }
        } else {
            runtime.bridges.callFunction(callback, thiz, args);
        }
    }

    public boolean clearTimeout(int id) {
        return clearCallback(id);
    }

    public int setInterval(final Object listener, final long interval, final Object... args) {
        final int id = mCallbackMaxId.getAndIncrement();
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                if (mHandlerCallbacks.get(id) == null)
                    return;
                callFunction(listener, null, args);
                postDelayed(this, interval);
            }
        };
        mHandlerCallbacks.put(id, r);
        postDelayed(r, interval);
        return id;
    }

    public void postDelayed(Runnable r, long interval) {
        long uptime = SystemClock.uptimeMillis() + interval;
        mHandler.postAtTime(r, uptime);
        mMaxCallbackUptimeMillis = Math.max(mMaxCallbackUptimeMillis, uptime);
        synchronized (mMaxCallbackMillisForAllThread) {
            mMaxCallbackMillisForAllThread.set(Math.max(mMaxCallbackMillisForAllThread.get(), uptime));
        }
    }

    public void post(Runnable r) {

    }

    public boolean clearInterval(int id) {
        return clearCallback(id);
    }

    public int setImmediate(final Object listener, final Object... args) {
        final int id = mCallbackMaxId.getAndIncrement();
        Runnable r = () -> {
            callFunction(listener, null, args);
            mHandlerCallbacks.remove(id);
        };
        mHandlerCallbacks.put(id, r);
        postDelayed(r, 0);
        return id;
    }

    public boolean clearImmediate(int id) {
        return clearCallback(id);
    }

    private boolean clearCallback(int id) {
        Runnable callback = mHandlerCallbacks.get(id);
        if (callback != null) {
            mHandler.removeCallbacks(callback);
            mHandlerCallbacks.remove(id);
            return true;
        }
        return false;
    }

    public boolean hasPendingCallbacks() {
        return mMaxCallbackUptimeMillis > SystemClock.uptimeMillis();
    }

    public void removeAllCallbacks() {
        mHandler.removeCallbacksAndMessages(null);
    }

}
