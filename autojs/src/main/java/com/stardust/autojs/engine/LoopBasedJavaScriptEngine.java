package com.stardust.autojs.engine;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;

import com.stardust.autojs.runtime.api.Loopers;
import com.stardust.autojs.script.JavaScriptSource;
import com.stardust.autojs.script.ScriptSource;
import com.stardust.util.Callback;

/**
 * Created by Stardust on 2017/7/28.
 */

public class LoopBasedJavaScriptEngine extends RhinoJavaScriptEngine {

    private Handler mHandler;
    private boolean mLooping = false;

    public LoopBasedJavaScriptEngine(Context context) {
        super(context);
    }

    @Override
    public Object execute(final JavaScriptSource source) {
        execute(source, null);
        return null;
    }


    public void execute(final ScriptSource source, final Callback<Object> callback) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Object o = LoopBasedJavaScriptEngine.super.execute((JavaScriptSource) source);
                if (callback != null)
                    callback.call(o);

            }
        };
        mHandler.post(r);
        if (!mLooping && Looper.myLooper() != Looper.getMainLooper()) {
            mLooping = true;
            Looper.loop();
            mLooping = false;
        }
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
