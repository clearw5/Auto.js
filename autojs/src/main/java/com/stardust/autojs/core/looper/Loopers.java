package com.stardust.autojs.core.looper;

import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;

import com.stardust.autojs.rhino.AutoJsContext;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.api.Threads;
import com.stardust.autojs.runtime.api.Timers;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.lang.ThreadCompat;

import org.mozilla.javascript.Context;

import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArrayList;

import androidx.annotation.Nullable;

/**
 * Created by Stardust on 2017/7/29.
 */

@SuppressWarnings("ConstantConditions")
public class Loopers implements MessageQueue.IdleHandler {

    private static final String LOG_TAG = "Loopers";

    public interface LooperQuitHandler {
        boolean shouldQuit();
    }

    private static final Runnable EMPTY_RUNNABLE = () -> {
    };

    private volatile ThreadLocal<Boolean> waitWhenIdle = new ThreadLocal<Boolean>() {
        @Nullable
        @Override
        protected Boolean initialValue() {
            return Looper.myLooper() == Looper.getMainLooper();
        }
    };
    private volatile ThreadLocal<HashSet<Integer>> waitIds = new ThreadLocal<HashSet<Integer>>() {
        @Nullable
        @Override
        protected HashSet<Integer> initialValue() {
            return new HashSet<>();
        }
    };
    private volatile ThreadLocal<Integer> maxWaitId = new ThreadLocal<Integer>() {
        @Nullable
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };
    private volatile ThreadLocal<CopyOnWriteArrayList<LooperQuitHandler>> looperQuitHandlers = new ThreadLocal<>();
    private volatile Looper mServantLooper;
    private Timers mTimers;
    private ScriptRuntime mScriptRuntime;
    private LooperQuitHandler mMainLooperQuitHandler;
    private Handler mMainHandler;
    private Looper mMainLooper;
    private Threads mThreads;
    private MessageQueue mMainMessageQueue;

    public Loopers(ScriptRuntime runtime) {
        mTimers = runtime.timers;
        mThreads = runtime.threads;
        mScriptRuntime = runtime;
        prepare();
        mMainLooper = Looper.myLooper();
        mMainHandler = new Handler();
        mMainMessageQueue = Looper.myQueue();
    }


    public Looper getMainLooper() {
        return mMainLooper;
    }

    public void addLooperQuitHandler(LooperQuitHandler handler) {
        CopyOnWriteArrayList<LooperQuitHandler> handlers = looperQuitHandlers.get();
        if (handlers == null) {
            handlers = new CopyOnWriteArrayList<>();
            looperQuitHandlers.set(handlers);
        }
        handlers.add(handler);
    }

    public boolean removeLooperQuitHandler(LooperQuitHandler handler) {
        CopyOnWriteArrayList<LooperQuitHandler> handlers = looperQuitHandlers.get();
        return handlers != null && handlers.remove(handler);
    }

    private boolean shouldQuitLooper() {
        if (Thread.currentThread().isInterrupted()) {
            return true;
        }
        if (mTimers.hasPendingCallbacks()) {
            return false;
        }
        if (waitWhenIdle.get() || !waitIds.get().isEmpty()) {
            return false;
        }
        if (((AutoJsContext) Context.getCurrentContext()).hasPendingContinuation()) {
            return false;
        }
        CopyOnWriteArrayList<LooperQuitHandler> handlers = looperQuitHandlers.get();
        if (handlers == null) {
            return true;
        }
        for (LooperQuitHandler handler : handlers) {
            if (!handler.shouldQuit()) {
                return false;
            }
        }
        return true;
    }


    private void initServantThread() {
        new ThreadCompat(() -> {
            Looper.prepare();
            final Object lock = Loopers.this;
            mServantLooper = Looper.myLooper();
            synchronized (lock) {
                lock.notifyAll();
            }
            Looper.loop();
        }).start();
    }

    public Looper getServantLooper() {
        if (mServantLooper == null) {
            initServantThread();
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new ScriptInterruptedException();
                }
            }
        }
        return mServantLooper;
    }

    private void quitServantLooper() {
        if (mServantLooper == null)
            return;
        mServantLooper.quit();
    }

    public int waitWhenIdle() {
        int id = maxWaitId.get();
        Log.d(LOG_TAG, "waitWhenIdle: " + id);
        maxWaitId.set(id + 1);
        waitIds.get().add(id);
        return id;
    }

    public void doNotWaitWhenIdle(int waitId) {
        Log.d(LOG_TAG, "doNotWaitWhenIdle: " + waitId);
        waitIds.get().remove(waitId);
    }

    public void waitWhenIdle(boolean b) {
        waitWhenIdle.set(b);
    }

    public void recycle() {
        quitServantLooper();
        mMainMessageQueue.removeIdleHandler(this);
    }

    public void setMainLooperQuitHandler(LooperQuitHandler mainLooperQuitHandler) {
        mMainLooperQuitHandler = mainLooperQuitHandler;
    }

    @Override
    public boolean queueIdle() {
        Looper l = Looper.myLooper();
        if (l == null)
            return true;
        if (l == mMainLooper) {
            Log.d(LOG_TAG, "main looper queueIdle");
            if (shouldQuitLooper() && !mThreads.hasRunningThreads() &&
                    mMainLooperQuitHandler != null && mMainLooperQuitHandler.shouldQuit()) {
                Log.d(LOG_TAG, "main looper quit");
                l.quit();
            }
        } else {
            Log.d(LOG_TAG, "looper queueIdle: " + l);
            if (shouldQuitLooper()) {
                l.quit();
            }
        }
        return true;
    }

    public void prepare() {
        if (Looper.myLooper() == null)
            LooperHelper.prepare();
        Looper.myQueue().addIdleHandler(this);
    }

    public void notifyThreadExit(TimerThread thread) {
        Log.d(LOG_TAG, "notifyThreadExit: " + thread);
        //当子线程退成时，主线程需要检查自身是否退出（主线程在所有子线程执行完成后才能退出，如果主线程已经执行完任务仍然要等待所有子线程），
        //此时通过向主线程发送一个空的Runnable，主线程执行完这个Runnable后会触发IdleHandler，从而检查自身是否退出
        mMainHandler.post(EMPTY_RUNNABLE);
    }
}
