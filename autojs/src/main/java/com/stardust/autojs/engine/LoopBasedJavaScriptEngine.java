package com.stardust.autojs.engine;

import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;

import com.stardust.autojs.runtime.api.Loopers;
import com.stardust.autojs.script.ScriptSource;

/**
 * Created by Stardust on 2017/7/28.
 */

public class LoopBasedJavaScriptEngine extends RhinoJavaScriptEngine {

    private Handler mHandler;
    private boolean mLooping = false;

    public LoopBasedJavaScriptEngine(RhinoJavaScriptEngineManager engineManager) {
        super(engineManager);
    }

    @Override
    public Object execute(final ScriptSource source) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                LoopBasedJavaScriptEngine.super.execute(source);
            }
        };
        mHandler.post(r);
        if (!mLooping && Looper.myLooper() != Looper.getMainLooper()) {
            mLooping = true;
            Looper.loop();
        }
        return null;
    }

    @Override
    public void forceStop() {
        Loopers.quitForThread(getThread());
        super.forceStop();
    }

    @Override
    public synchronized void destroy() {
        Loopers.quitForThread(getThread());
        super.destroy();
    }

    @Override
    public void init() {
        Loopers.prepare();
        mHandler = new Handler();
        super.init();
    }


}
