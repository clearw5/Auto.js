package com.stardust.scriptdroid.autojs.api;

import com.stardust.autojs.runtime.ScriptInterruptedException;

/**
 * Created by Stardust on 2017/5/8.
 */

public class VolatileBox<T> {

    private volatile T mValue;

    public VolatileBox() {

    }

    public VolatileBox(T value) {
        set(value);
    }

    public T get() {
        return mValue;
    }

    public void set(T value) {
        mValue = value;
    }

    public void setAndNotify(T value) {
        mValue = value;
        synchronized (this) {
            notify();
        }
    }

    public T blockedGet() {
        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new ScriptInterruptedException();
            }
        }
        return mValue;
    }

}
