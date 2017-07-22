package com.stardust.autojs.runtime.api;

import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Stardust on 2017/7/21.
 */

public class Timers {

    private static ConcurrentHashMap<Thread, Looper> sLoopers = new ConcurrentHashMap<>();
    private Handler mHandler;
    private SparseArray<Runnable> mHandlerCallbacks = new SparseArray<>();
    private int mCallbackMaxId = 0;
    private ScriptBridges mBridges;

    public Timers(ScriptBridges bridges) {
        mBridges = bridges;
    }

    public int setTimeout(final Object callback, long delay, final Object... args) {
        prepareLoopIfNeeded();
        mCallbackMaxId++;
        final int id = mCallbackMaxId;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                mBridges.callFunction(callback, null, args);
                mHandlerCallbacks.remove(id);
            }
        };
        mHandlerCallbacks.put(id, r);
        mHandler.postDelayed(r, delay);
        return id;
    }

    public void post(Runnable r) {
        mHandler.post(r);
    }

    public void clearTimeout(int id) {
        clearCallback(id);
    }

    public int setInterval(final Object listener, final long interval, final Object... args) {
        prepareLoopIfNeeded();
        mCallbackMaxId++;
        final int id = mCallbackMaxId;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                mBridges.callFunction(listener, null, args);
                mHandler.postDelayed(this, interval);
            }
        };
        mHandlerCallbacks.put(id, r);
        mHandler.postDelayed(r, interval);
        return id;
    }

    public void clearInterval(int id) {
        clearTimeout(id);
    }

    public int setImmediate(final Object listener, final Object... args) {
        prepareLoopIfNeeded();
        mCallbackMaxId++;
        final int id = mCallbackMaxId;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                mBridges.callFunction(listener, null, args);
                mHandlerCallbacks.remove(id);
            }
        };
        mHandlerCallbacks.put(id, r);
        mHandler.post(r);
        return id;
    }

    public void clearImmediate(int id) {
        clearCallback(id);
    }

    private void clearCallback(int id) {
        Runnable callback = mHandlerCallbacks.get(id);
        if (callback != null) {
            mHandler.removeCallbacks(callback);
            mHandlerCallbacks.remove(id);
        }
    }

    public void prepareLoopIfNeeded() {
        if (Looper.myLooper() != null)
            return;
        Looper.prepare();
        Looper looper = Looper.myLooper();
        if (looper != null) {
            // null check is not necessary, just to make Android Studio happy
            sLoopers.put(Thread.currentThread(), looper);
        }
        mHandler = new Handler();
    }

    public void loop() {
        Looper.loop();
    }

    public static void removeThreadRecord(Thread thread) {
        sLoopers.remove(thread);
    }

    public static void quitLooperIfNeeded(Thread thread) {
        Looper looper = sLoopers.get(thread);
        if (looper != null) {
            looper.quit();
        }
    }


}
