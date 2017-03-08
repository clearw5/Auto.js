package com.stardust.scriptdroid.record.inputevent;

/**
 * Created by Stardust on 2017/3/7.
 */

public class InputEventToSendEventRecorder extends InputEventRecorder {

    private InputEventToSendEventConverter mEventConverter;

    protected InputEventToSendEventRecorder() {
        super("getevent -t", new InputEventToSendEventConverter());
    }


    @Override
    protected void parseAndRecordEvent(String eventStr) {
        mEventConverter.parseAndAddEventIfFormatCorrect(eventStr);
    }

    @Override
    public String getCode() {
        return mEventConverter.getCode();
    }

}
