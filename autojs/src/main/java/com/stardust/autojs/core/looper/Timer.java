package com.stardust.autojs.core.looper;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.SparseArray;

import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.concurrent.VolatileBox;

/**
 * Created by Stardust on 2017/12/27.
 */

public class Timer {

    private static final String LOG_TAG = "Timer";

    private SparseArray<Runnable> mHandlerCallbacks = new SparseArray<>();
    private int mCallbackMaxId = 0;
    private ScriptRuntime mRuntime;
    private Handler mHandler;
    private long mMaxCallbackUptimeMillis = 0;
    private final VolatileBox<Long> mMaxCallbackMillisForAllThread;

    public Timer(ScriptRuntime runtime, VolatileBox<Long> maxCallbackMillisForAllThread) {
        mRuntime = runtime;
        mMaxCallbackMillisForAllThread = maxCallbackMillisForAllThread;
        mHandler = new Handler();
    }

    public Timer(ScriptRuntime runtime, VolatileBox<Long> maxCallbackMillisForAllThread, Looper looper) {
        mRuntime = runtime;
        mMaxCallbackMillisForAllThread = maxCallbackMillisForAllThread;
        mHandler = new Handler(looper);
    }

    public int setTimeout(final Object callback, final long delay, final Object... args) {
        mCallbackMaxId++;
        final int id = mCallbackMaxId;
        Runnable r = () -> {
            callFunction(callback, null, args);
            mHandlerCallbacks.remove(id);
        };
        mHandlerCallbacks.put(id, r);
        postDelayed(r, delay);
        return id;
    }

    private void callFunction(Object callback, Object thiz, Object[] args) {
        if(Looper.myLooper() == Looper.getMainLooper()){
            try {
                mRuntime.bridges.callFunction(callback, thiz, args);
            }catch (Exception e){
                mRuntime.exit(e);
            }
        }else {
            mRuntime.bridges.callFunction(callback, thiz, args);
        }
    }

    public boolean clearTimeout(int id) {
        return clearCallback(id);
    }

    public int setInterval(final Object listener, final long interval, final Object... args) {
        mCallbackMaxId++;
        final int id = mCallbackMaxId;
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
        mCallbackMaxId++;
        final int id = mCallbackMaxId;
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
