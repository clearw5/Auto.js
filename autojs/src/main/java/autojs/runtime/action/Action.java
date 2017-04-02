package autojs.runtime.action;

import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by Stardust on 2017/1/27.
 */

public abstract class Action {

    private boolean mPerformUtilSucceed = false;
    private volatile Object mResult = false;

    public Action(boolean performUtilSucceed) {
        mPerformUtilSucceed = performUtilSucceed;
    }

    public Action() {
        this(false);
    }

    public abstract boolean perform(AccessibilityNodeInfo root);

    public boolean performUtilSucceed() {
        return mPerformUtilSucceed;
    }

    public Action setPerformUtilSucceed(boolean performUtilSucceed) {
        mPerformUtilSucceed = performUtilSucceed;
        return this;
    }

    public Object getResult() {
        return mResult;
    }

    public void setResult(Object result) {
        mResult = result;
    }
}
