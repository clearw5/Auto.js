package com.stardust.lang;

import java.lang.ref.WeakReference;
import java.util.Hashtable;
import java.util.WeakHashMap;

/**
 * Created by Stardust on 2017/4/30.
 */

public class ThreadCompat extends Thread {

    private static volatile WeakHashMap<Thread, Boolean> interruptStatus = new WeakHashMap<>();

    public ThreadCompat() {
        init();
    }

    private void init() {
        interruptStatus.put(this, false);
    }

    public ThreadCompat(Runnable target) {
        super(target);
        init();
    }

    public ThreadCompat(ThreadGroup group, Runnable target) {
        super(group, target);
        init();
    }

    public ThreadCompat(String name) {
        super(name);
        init();
    }

    public ThreadCompat(ThreadGroup group, String name) {
        super(group, name);
        init();
    }

    public ThreadCompat(Runnable target, String name) {
        super(target, name);
        init();
    }

    public ThreadCompat(ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
        init();
    }

    public ThreadCompat(ThreadGroup group, Runnable target, String name, long stackSize) {
        super(group, target, name, stackSize);
        init();
    }

    @Override
    public void run() {
        try {
            super.run();
            interruptStatus.remove(this);
        } catch (Throwable e) {
            interruptStatus.remove(this);
            throw e;
        }
    }

    @Override
    public boolean isInterrupted() {
        Boolean isInterrupted = interruptStatus.get(this);
        return super.isInterrupted() || isInterrupted == null || isInterrupted;
    }

    @Override
    public void interrupt() {
        super.interrupt();
        interruptStatus.remove(this);
    }

}
