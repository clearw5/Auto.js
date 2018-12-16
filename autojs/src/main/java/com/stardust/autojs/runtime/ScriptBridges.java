package com.stardust.autojs.runtime;

/**
 * Created by Stardust on 2017/7/21.
 */

public class ScriptBridges {


    public interface Bridges {

        Object[] NO_ARGUMENTS = new Object[0];

        Object call(Object func, Object target, Object arg);

        Object toArray(Iterable o);

        Object toString(Object obj);

        Object asArray(Object obj);
    }

    private Bridges mBridges;

    public void setBridges(Bridges bridges) {
        mBridges = bridges;
    }

    public Bridges getBridges() {
        return mBridges;
    }

    public Object callFunction(Object func, Object target, Object args) {
        checkBridges();
        return mBridges.call(func, target, args);
    }

    private void checkBridges() {
        if (mBridges == null)
            throw new IllegalStateException("no bridges set");
    }


    public Object toArray(Iterable c) {
        checkBridges();
        return mBridges.toArray(c);
    }

    public Object toString(Object obj) {
        checkBridges();
        return mBridges.toString(obj);
    }

    public Object asArray(Object obj) {
        checkBridges();
        return mBridges.asArray(obj);
    }
}
