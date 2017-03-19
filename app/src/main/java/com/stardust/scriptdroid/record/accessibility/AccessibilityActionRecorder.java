package com.stardust.scriptdroid.record.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

import com.stardust.scriptdroid.record.Recorder;
import com.stardust.view.accessibility.AccessibilityDelegate;


/**
 * Created by Stardust on 2017/2/14.
 */

public class AccessibilityActionRecorder extends Recorder.DefaultIMPL implements AccessibilityDelegate {

    public static class AccessibilityActionRecordEvent {

        private final AccessibilityEvent mAccessibilityEvent;

        public AccessibilityActionRecordEvent(AccessibilityEvent event) {
            mAccessibilityEvent = event;
        }

        public AccessibilityEvent getAccessibilityEvent() {
            return mAccessibilityEvent;
        }
    }

    private static final long RECORD_TIME_OUT = 10 * 60 * 1000;

    private static AccessibilityActionRecorder instance;
    private boolean mRecordMessageEnabled;

    private boolean mShouldIgnoreFirstAction = false;

    public static AccessibilityActionRecorder getInstance() {
        if (instance == null) {
            instance = new AccessibilityActionRecorder();
        }
        return instance;
    }

    private AccessibilityActionConverter mConverter;
    private long mRecordStartMillis;

    public AccessibilityActionRecorder(){
        super(true);
    }

    @Override
    protected void startImpl() {
        mConverter = new AccessibilityActionConverter(mShouldIgnoreFirstAction);
        mRecordStartMillis = System.currentTimeMillis();
    }

    @Override
    protected void stopImpl() {
        setState(STATE_NOT_START);
    }

    @Override
    protected void resumeImpl() {
        mConverter.onResume();
    }

    @Override
    public boolean onAccessibilityEvent(AccessibilityService service, AccessibilityEvent event) {
        if (getState() == STATE_RECORDING) {
            mConverter.record(service, event);
            checkTimeOut();
        }
        return false;
    }

    private void checkTimeOut() {
        if (System.currentTimeMillis() - mRecordStartMillis > RECORD_TIME_OUT) {
            stop();
        }
    }

    @Override
    public String getCode() {
        return mConverter.getScript();
    }

    public void setShouldIgnoreFirstAction(boolean shouldIgnoreFirstAction) {
        mShouldIgnoreFirstAction = shouldIgnoreFirstAction;
    }


    public void setRecordMessageEnabled(boolean recordMessageEnabled) {
        // TODO: 2017/3/16 添加录制提示
        mRecordMessageEnabled = recordMessageEnabled;
    }
}
