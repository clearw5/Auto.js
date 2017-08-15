package com.stardust.autojs.runtime.api;

import android.os.Looper;
import android.os.MessageQueue;

import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.lang.ThreadCompat;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Stardust on 2017/7/29.
 */

public class Loopers {

    private volatile Looper mServantLooper;
    private static volatile ConcurrentHashMap<Thread, Looper> sLoopers = new ConcurrentHashMap<>();

    public volatile boolean waitWhenIdle = false;

    public Loopers() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            waitWhenIdle = true;
        }
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                Looper l = Looper.myLooper();
                if (l != null && !waitWhenIdle)
                    l.quit();
                return true;
            }
        });
    }


    private void initServantThread() {
        new ThreadCompat(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                final Object lock = Loopers.this;
                mServantLooper = Looper.myLooper();
                synchronized (lock) {
                    lock.notifyAll();
                }
                Looper.loop();
            }
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

    public void quitServantLooper() {
        if (mServantLooper == null)
            return;
        mServantLooper.quit();
    }

    public void waitWhenIdle(boolean b) {
        waitWhenIdle = b;
    }

    public void quitAll() {
        quitServantLooper();
    }

    public static void prepare() {
        if (Looper.myLooper() == Looper.getMainLooper())
            return;
        if (Looper.myLooper() == null)
            Looper.prepare();
        Looper l = Looper.myLooper();
        if (l != null)
            sLoopers.put(Thread.currentThread(), l);
    }

    public static void quitForThread(Thread thread) {
        Looper looper = sLoopers.remove(thread);
        if (looper != null)
            looper.quit();
    }
}
