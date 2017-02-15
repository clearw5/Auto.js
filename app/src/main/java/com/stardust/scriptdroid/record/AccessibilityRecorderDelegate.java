package com.stardust.scriptdroid.record;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

import com.stardust.scriptdroid.service.AccessibilityDelegate;

import static com.stardust.scriptdroid.external.notification.record.ActionRecordSwitchView.PAUSED;
import static com.stardust.scriptdroid.external.notification.record.ActionRecordSwitchView.RECORDING;
import static com.stardust.scriptdroid.external.notification.record.ActionRecordSwitchView.STOPPED;

/**
 * Created by Stardust on 2017/2/14.
 */

public class AccessibilityRecorderDelegate implements AccessibilityDelegate {

    private static final int PRIORITY = 200;
    private static final long RECORD_TIME_OUT = 10 * 60 * 1000;

    private static AccessibilityRecorderDelegate instance;
    private int mState = STOPPED;

    public static AccessibilityRecorderDelegate getInstance() {
        if (instance == null) {
            instance = new AccessibilityRecorderDelegate();
        }
        return instance;
    }

    private ActionRecorder mRecorder = new ActionRecorder();
    private long mRecordStartMillis;

    public void startRecord() {
        if (mState != STOPPED) {
            throw new IllegalStateException("Recording");
        }
        mState = RECORDING;
        mRecorder = new ActionRecorder();
        mRecordStartMillis = System.currentTimeMillis();
    }


    public String stopRecord() {
        if (mState == STOPPED) {
            throw new IllegalStateException("Not recording");
        }
        mState = STOPPED;
        String script = mRecorder.getScript();
        mRecorder = null;
        return script;
    }


    public void pauseRecord() {
        if (mState != RECORDING) {
            throw new IllegalStateException("Not recording");
        }
        mState = PAUSED;
    }

    public void resumeRecord() {
        if (mState != PAUSED) {
            throw new IllegalStateException("Not paused");
        }
        mRecorder.onResume();
        mState = RECORDING;
    }

    public void stopRecordIfNeeded() {
        if (mRecorder != null) {
            mRecorder = null;
        }
    }

    public int getState() {
        return mState;
    }

    @Override
    public boolean onAccessibilityEvent(AccessibilityService service, AccessibilityEvent event) {
        if (mState == RECORDING) {
            mRecorder.record(event);
            checkTimeOut();
        }
        return false;
    }

    private void checkTimeOut() {
        if (System.currentTimeMillis() - mRecordStartMillis > RECORD_TIME_OUT) {
            stopRecord();
        }
    }

}
