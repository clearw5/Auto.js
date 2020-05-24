package com.stardust.autojs.core.record.inputevent;

import androidx.annotation.NonNull;

import com.stardust.autojs.core.inputevent.InputEventObserver;
import com.stardust.autojs.core.record.Recorder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Stardust on 2017/3/7.
 */

public abstract class InputEventRecorder extends Recorder.AbstractRecorder implements InputEventObserver.InputEventListener {


    private final static Pattern LAST_INT_PATTERN = Pattern.compile("[^0-9]+([0-9]+)$");


    protected boolean mRecording = false;

    protected void startImpl() {
        mRecording = true;
    }

    protected void resumeImpl() {
        mRecording = true;
    }

    protected void pauseImpl() {
        mRecording = false;
    }

    protected void stopImpl() {
        mRecording = false;
    }

    public abstract String getCode();

    public static int parseDeviceNumber(String device) {
        Matcher matcher = LAST_INT_PATTERN.matcher(device);
        if (matcher.find()) {
            String someNumberStr = matcher.group(1);
            return Integer.parseInt(someNumberStr);
        }
        return -1;
    }


    @Override
    public void onInputEvent(@NonNull InputEventObserver.InputEvent e) {
        if (!mRecording) {
            return;
        }
        recordInputEvent(e);
    }

    protected abstract void recordInputEvent(InputEventObserver.InputEvent e);
}
