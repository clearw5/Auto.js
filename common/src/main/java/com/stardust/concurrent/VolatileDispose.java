package com.stardust.concurrent;

/**
 * Created by Stardust on 2017/10/28.
 */

public class VolatileDispose<T> {

    private volatile T mValue;

    public T blockedGet() {
        synchronized (this) {
            if (mValue != null) {
                return mValue;
            }
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return mValue;
    }

    public T blockedGetOrThrow(Class<? extends RuntimeException> exception) {
        synchronized (this) {
            if (mValue != null) {
                return mValue;
            }
            try {
                this.wait();
            } catch (InterruptedException e) {
                try {
                    throw exception.newInstance();
                } catch (InstantiationException e1) {
                    throw new RuntimeException(e1);
                } catch (IllegalAccessException e1) {
                    throw new RuntimeException(e1);
                }
            }
        }
        return mValue;
    }

    public T blockedGetOrThrow(Class<? extends RuntimeException> exception, long timeout, T defaultValue) {
        synchronized (this) {
            if (mValue != null) {
                return mValue;
            }
            try {
                this.wait(timeout);
            } catch (InterruptedException e) {
                try {
                    throw exception.newInstance();
                } catch (InstantiationException e1) {
                    throw new RuntimeException(e1);
                } catch (IllegalAccessException e1) {
                    throw new RuntimeException(e1);
                }
            }
            if (mValue == null) {
                return defaultValue;
            }
        }
        return mValue;
    }

    public void setAndNotify(T value) {
        synchronized (this) {
            mValue = value;
            notify();
        }
    }

}
