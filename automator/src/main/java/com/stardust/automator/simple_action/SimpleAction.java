package com.stardust.automator.simple_action;

import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by Stardust on 2017/1/27.
 */

public abstract class SimpleAction {

    private boolean mValid = true;
    private Object mResult = false;

    public abstract boolean perform(AccessibilityNodeInfo root);

    public synchronized Object getResult() {
        return mResult;
    }

    public synchronized void setResult(Object result) {
        mResult = result;
    }

    public synchronized void setValid(boolean valid) {
        mValid = valid;
    }

    public synchronized boolean isValid() {
        return mValid;
    }
}
