package com.stardust.autojs.runtime.action;

import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by Stardust on 2017/1/27.
 */

public abstract class Action {

    private boolean mValid = true;
    private Object mResult = false;

    public abstract boolean perform(AccessibilityNodeInfo root);

    public Object getResult() {
        return mResult;
    }

    public void setResult(Object result) {
        mResult = result;
    }

    public void setValid(boolean valid) {
        mValid = valid;
    }

    public boolean isValid() {
        return mValid;
    }
}
