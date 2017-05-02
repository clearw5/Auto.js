package com.stardust.automator.simple_action;

import com.stardust.automator.UiObject;

/**
 * Created by Stardust on 2017/1/27.
 */

public abstract class SimpleAction {

    private volatile boolean mValid = true;
    private volatile boolean mResult = false;

    public abstract boolean perform(UiObject root);

    public boolean getResult() {
        return mResult;
    }

    public void setResult(boolean result) {
        mResult = result;
    }

    public void setValid(boolean valid) {
        mValid = valid;
    }

    public boolean isValid() {
        return mValid;
    }

}
