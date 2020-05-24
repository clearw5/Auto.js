package com.stardust.autojs.runtime.api;

import androidx.annotation.NonNull;

import com.stardust.autojs.core.looper.MainThreadProxy;
import com.stardust.autojs.core.looper.TimerThread;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.concurrent.VolatileDispose;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Stardust on 2017/12/3.
 */

public class Threads {

    private final HashSet<Thread> mThreads = new HashSet<>();
    private ScriptRuntime mRuntime;
    private final Thread mMainThread;
    private MainThreadProxy mMainThreadProxy;
    private int mSpawnCount = 0;
    private boolean mExit = false;

    public Threads(ScriptRuntime runtime) {
        mRuntime = runtime;
        mMainThread = Thread.currentThread();
        mMainThreadProxy = new MainThreadProxy(Thread.currentThread(), mRuntime);
    }

    public Thread getMainThread() {
        return mMainThread;
    }

    public Object currentThread() {
        Thread thread = Thread.currentThread();
        if (thread == mMainThread)
            return mMainThreadProxy;
        return thread;
    }

    public TimerThread start(Runnable runnable) {
        TimerThread thread = createThread(runnable);
        synchronized (mThreads) {
            if (mExit) {
                throw new IllegalStateException("script exiting");
            }
            mThreads.add(thread);
            thread.setName(mMainThread.getName() + " (Spawn-" + mSpawnCount + ")");
            mSpawnCount++;
            thread.start();
        }
        return thread;
    }

    @NonNull
    private TimerThread createThread(Runnable runnable) {
        return new TimerThread(mRuntime, mRuntime.timers.getMaxCallbackUptimeMillisForAllThreads(),
                runnable
        ) {
            @Override
            protected void onExit() {
                synchronized (mThreads) {
                    mThreads.remove(Thread.currentThread());
                }
                super.onExit();
            }
        };
    }

    public VolatileDispose disposable() {
        return new VolatileDispose();
    }

    public AtomicLong atomic(long value) {
        return new AtomicLong(value);
    }

    public AtomicLong atomic() {
        return new AtomicLong();
    }

    public Lock lock() {
        return new ReentrantLock();
    }

    public void shutDownAll() {
        synchronized (mThreads) {
            for (Thread thread : mThreads) {
                thread.interrupt();
            }
            mThreads.clear();
        }
    }

    public void exit() {
        synchronized (mThreads) {
            shutDownAll();
            mExit = true;
        }
    }

    public boolean hasRunningThreads() {
        synchronized (mThreads) {
            return !mThreads.isEmpty();
        }
    }
}
