package com.stardust.scriptdroid.record.inputevent;

/**
 * Created by Stardust on 2017/3/7.
 */

public class InputEventToJsRecorder extends InputEventRecorder {

    private InputEventToJsConverter mInputEventToJsConverter;

    public InputEventToJsRecorder() {
        super("getevent -t -l", new InputEventToJsConverter());
        mInputEventToJsConverter = (InputEventToJsConverter) mInputEventConverter;
    }

    @Override
    protected void parseAndRecordEvent(String eventStr) {
        mInputEventConverter.parseAndAddEventIfFormatCorrect(eventStr);
    }

    @Override
    public String getCode() {
        return mInputEventConverter.getCode();
    }

    public void setStartTriggerKey(String startTriggerKey) {
        mInputEventToJsConverter.setStartTriggerKey(startTriggerKey);
    }

    public void setStopTriggerKey(String stopTriggerKey) {
        mInputEventToJsConverter.setStopTriggerKey(stopTriggerKey);
    }
}
