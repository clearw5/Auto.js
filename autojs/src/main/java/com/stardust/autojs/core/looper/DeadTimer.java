package com.stardust.autojs.core.looper;

/**
 * 脚本引擎已结束 创建空的Timer被调用后不执行任何操作
 */
public class DeadTimer extends Timer {

    public DeadTimer() {
        super(null, null);
    }

    @Override
    public int setTimeout(Object callback, long delay, Object... args) {
        return -1;
    }

    @Override
    public boolean clearTimeout(int id) {
        return true;
    }

    @Override
    public int setInterval(Object listener, long interval, Object... args) {
        return -1;
    }

    @Override
    public void postDelayed(Runnable r, long interval) {

    }

    @Override
    public void post(Runnable r) {

    }

    @Override
    public boolean clearInterval(int id) {
        return true;
    }

    @Override
    public int setImmediate(Object listener, Object... args) {
        return -1;
    }

    @Override
    public boolean clearImmediate(int id) {
        return true;
    }

    @Override
    public boolean hasPendingCallbacks() {
        return false;
    }

    @Override
    public void removeAllCallbacks() {

    }
}
