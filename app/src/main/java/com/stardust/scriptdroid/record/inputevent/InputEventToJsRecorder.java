package com.stardust.scriptdroid.record.inputevent;

/**
 * Created by Stardust on 2017/3/7.
 */

public class InputEventToJsRecorder extends InputEventRecorder {


    public InputEventToJsRecorder() {
        super("getevent -t -l", new InputEventToJsConverter());
    }

    @Override
    protected void parseAndRecordEvent(String eventStr) {
        mInputEventConverter.parseAndAddEventIfFormatCorrect(eventStr);
    }

    @Override
    public String getCode() {
        return mInputEventConverter.getCode();
    }
}
