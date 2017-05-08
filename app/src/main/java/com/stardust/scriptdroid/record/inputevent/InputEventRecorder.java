package com.stardust.scriptdroid.record.inputevent;

import com.stardust.scriptdroid.autojs.api.Shell;
import com.stardust.scriptdroid.record.Recorder;

/**
 * Created by Stardust on 2017/3/6.
 */

public class InputEventRecorder extends Recorder.AbstractRecorder {

    private static final String TAG = "InputEventRecorder";
    private String mGetEventCommand;
    private Shell mShell;
    protected InputEventConverter mInputEventConverter;


    protected InputEventRecorder(InputEventConverter inputEventConverter) {
        mGetEventCommand = inputEventConverter.getGetEventCommand();
        mInputEventConverter = inputEventConverter;
    }

    public void listen() {
        mShell = new Shell(true);
        mShell.setCallback(new Shell.SimpleCallback() {
            @Override
            public void onNewLine(String str) {
                if (mShell.isInitialized()) {
                    convertEvent(str);
                }
            }

            @Override
            public void onInitialized() {
                mShell.exec(mGetEventCommand);
            }

            @Override
            public void onInterrupted(InterruptedException e) {
                stop();
            }
        });
    }

    @Override
    protected void startImpl() {
        mInputEventConverter.start();
    }

    @Override
    protected void pauseImpl() {
        mInputEventConverter.pause();
    }

    @Override
    protected void resumeImpl() {
        mInputEventConverter.resume();
    }

    @Override
    protected void stopImpl() {
        mShell.exit();
        mInputEventConverter.stop();
    }

    @Override
    public String getCode() {
        return mInputEventConverter.getCode();
    }

    protected void convertEvent(String eventStr) {
        mInputEventConverter.convertEventIfFormatCorrect(eventStr);
    }

}
