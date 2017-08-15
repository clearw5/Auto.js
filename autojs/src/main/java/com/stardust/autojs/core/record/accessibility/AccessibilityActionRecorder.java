package com.stardust.autojs.core.record.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

import com.stardust.autojs.core.record.Recorder;
import com.stardust.view.accessibility.AccessibilityDelegate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by Stardust on 2017/2/14.
 */

public class AccessibilityActionRecorder extends Recorder.AbstractRecorder implements AccessibilityDelegate {

    public static class AccessibilityActionRecordEvent {

        private final AccessibilityEvent mAccessibilityEvent;

        public AccessibilityActionRecordEvent(AccessibilityEvent event) {
            mAccessibilityEvent = event;
        }

        public AccessibilityEvent getAccessibilityEvent() {
            return mAccessibilityEvent;
        }
    }

    private static final Set<Integer> EVENT_TYPES = new HashSet<>(Arrays.asList(AccessibilityEvent.TYPE_VIEW_CLICKED, AccessibilityEvent.TYPE_VIEW_LONG_CLICKED, AccessibilityEvent.TYPE_VIEW_SCROLLED, AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED));

    private static final long RECORD_TIME_OUT = 10 * 60 * 1000;

    private boolean mShouldIgnoreFirstAction = false;


    private AccessibilityActionConverter mConverter;
    private long mRecordStartMillis;

    public AccessibilityActionRecorder() {
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

    @Override
    public Set<Integer> getEventTypes() {
        return EVENT_TYPES;
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

}
