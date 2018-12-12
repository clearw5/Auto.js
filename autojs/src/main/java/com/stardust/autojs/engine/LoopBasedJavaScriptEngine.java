package com.stardust.autojs.engine;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;

import com.stardust.autojs.core.looper.LooperHelper;
import com.stardust.autojs.script.JavaScriptSource;
import com.stardust.autojs.script.ScriptSource;
import com.stardust.util.Callback;

import org.mozilla.javascript.ContinuationPending;

/**
 * Created by Stardust on 2017/7/28.
 */

public class LoopBasedJavaScriptEngine extends RhinoJavaScriptEngine {

    public interface ExecuteCallback {
        void onResult(Object r);

        void onException(Exception e);
    }

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


    public void execute(final ScriptSource source, final ExecuteCallback callback) {
        Runnable r = () -> {
            try {
                Object o = LoopBasedJavaScriptEngine.super.execute((JavaScriptSource) source);
                if (callback != null)
                    callback.onResult(o);
            } catch (ContinuationPending ignored) {
            } catch (Exception e) {
                if (callback == null) {
                    throw e;
                } else {
                    callback.onException(e);
                }
            }


        };
        mHandler.post(r);
        if (!mLooping && Looper.myLooper() != Looper.getMainLooper()) {
            mLooping = true;
            while (true) {
                try {
                    Looper.loop();
                } catch (ContinuationPending ignored) {
                    continue;
                } catch (Throwable t) {
                    mLooping = false;
                    throw t;
                }
                break;
            }
        }
    }

    @Override
    public void forceStop() {
        LooperHelper.quitForThread(getThread());
        Activity activity = (Activity) getTag("activity");
        if (activity != null) {
            activity.finish();
        }
        super.forceStop();
    }

    @Override
    public synchronized void destroy() {
        Thread thread = getThread();
        LooperHelper.quitForThread(thread);
        super.destroy();
    }

    @Override
    public void init() {
        LooperHelper.prepare();
        mHandler = new Handler();
        super.init();
    }


}
