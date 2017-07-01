package com.stardust.scriptdroid.external.floatingwindow.menu.record.inputevent;

import android.content.Context;

import com.stardust.autojs.runtime.api.Shell;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.external.floatingwindow.menu.record.Recorder;

/**
 * Created by Stardust on 2017/3/6.
 */

public class InputEventRecorder extends Recorder.AbstractRecorder {

    private static final String TAG = "InputEventRecorder";
    private String mGetEventCommand;
    private Shell mShell;
    protected InputEventConverter mInputEventConverter;
    private Context mContext;


    protected InputEventRecorder(Context context, InputEventConverter inputEventConverter) {
        mGetEventCommand = inputEventConverter.getGetEventCommand();
        mInputEventConverter = inputEventConverter;
        mContext = context;
    }

    public void listen() {
        mShell = new Shell(mContext, true);
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
