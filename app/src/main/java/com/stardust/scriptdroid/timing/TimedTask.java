package com.stardust.scriptdroid.timing;

import android.content.Intent;


/**
 * Created by Stardust on 2017/11/27.
 */

public class TimedTask {

    int mId;

    boolean mDisposable;

    boolean mScheduled;

    public boolean isDisposable() {
        return mDisposable;
    }

    public void setDisposable(boolean disposable) {
        mDisposable = disposable;
    }

    public boolean isScheduled() {
        return mScheduled;
    }

    public void setScheduled(boolean scheduled) {
        mScheduled = scheduled;
    }

    public long getNextTime() {
        return 0;
    }

    public int getId() {
        return mId;
    }

    public Intent createIntent() {
        return null;
    }
}
