package com.stardust.scriptdroid.record.inputevent;

import android.preference.PreferenceManager;
import android.util.Log;

import com.stardust.pio.UncheckedIOException;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.autojs.Shell;
import com.stardust.scriptdroid.record.Recorder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import jackpal.androidterm.ShellTermSession;
import jackpal.androidterm.emulatorview.TermSession;
import jackpal.androidterm.util.TermSettings;

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
        mShell.setCallback(new Shell.Callback() {
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
