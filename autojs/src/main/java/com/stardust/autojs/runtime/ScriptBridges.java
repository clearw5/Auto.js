package com.stardust.autojs.runtime;

import android.view.View;

import com.stardust.autojs.core.accessibility.UiCollection;

/**
 * Created by Stardust on 2017/7/21.
 */

public class ScriptBridges {


    public interface Bridges {

        Object[] NO_ARGUMENTS = new Object[0];

        Object call(Object func, Object target, Object arg);

        Object toArray(Iterable o);

        Object asArray(Object o);

        Object toString(Object obj);

    }

    private Bridges mBridges;

    public void setBridges(Bridges bridges) {
        mBridges = bridges;
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

    public Object asArray(Object o) {
        checkBridges();
        return mBridges.asArray(o);
    }


    public Object toString(Object obj) {
        checkBridges();
        return mBridges.toString(obj);
    }

}
