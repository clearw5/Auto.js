package com.stardust.autojs.runtime.api;

import android.os.Handler;
import android.util.SparseArray;

import com.stardust.autojs.runtime.ScriptBridges;

/**
 * Created by Stardust on 2017/7/21.
 */

public class Timers {

    private SparseArray<Runnable> mHandlerCallbacks = new SparseArray<>();
    private int mCallbackMaxId = 0;
    private ScriptBridges mBridges;
    private Handler mHandler;

    public Timers(ScriptBridges bridges) {
        mBridges = bridges;
    }

    private void ensureHander(){
        if(mHandler == null){
            mHandler = new Handler();
        }
    }

    public int setTimeout(final Object callback, long delay, final Object... args) {
        ensureHander();
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
        ensureHander();
        mHandler.post(r);
    }

    public void clearTimeout(int id) {
        clearCallback(id);
    }

    public int setInterval(final Object listener, final long interval, final Object... args) {
        ensureHander();
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
        ensureHander();
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


}
