package com.stardust.autojs.rhino;

import android.os.Looper;
import android.util.Log;

import com.stardust.autojs.runtime.exception.ScriptInterruptedException;

import org.mozilla.javascript.Context;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class InterruptibleAndroidContextFactory extends AndroidContextFactory {

    private AtomicInteger mContextCount = new AtomicInteger();
    private static final String LOG_TAG = "ContextFactory";

    /**
     * Create a new factory. It will cache generated code in the given directory
     *
     * @param cacheDirectory the cache directory
     */
    public InterruptibleAndroidContextFactory(File cacheDirectory) {
        super(cacheDirectory);
    }

    @Override
    protected void observeInstructionCount(Context cx, int instructionCount) {
        if (Thread.currentThread().isInterrupted() && Looper.myLooper() != Looper.getMainLooper()) {
            throw new ScriptInterruptedException();
        }
    }

    @Override
    protected Context makeContext() {
        Context cx = new AutoJsContext(this);
        cx.setInstructionObserverThreshold(10000);
        return cx;
    }

    @Override
    protected void onContextCreated(Context cx) {
        super.onContextCreated(cx);
        int i = mContextCount.incrementAndGet();
        Log.d(LOG_TAG, "onContextCreated: count = " + i);
    }

    @Override
    protected void onContextReleased(Context cx) {
        super.onContextReleased(cx);
        int i = mContextCount.decrementAndGet();
        Log.d(LOG_TAG, "onContextReleased: count = " + i);
    }

}
