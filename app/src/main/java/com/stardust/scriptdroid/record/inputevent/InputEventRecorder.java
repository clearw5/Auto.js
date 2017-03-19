package com.stardust.scriptdroid.record.inputevent;

import android.preference.PreferenceManager;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.record.Recorder;

import java.io.IOException;

import jackpal.androidterm.ShellTermSession;
import jackpal.androidterm.emulatorview.TermSession;
import jackpal.androidterm.util.TermSettings;

/**
 * Created by Stardust on 2017/3/6.
 */

public abstract class InputEventRecorder extends Recorder.DefaultIMPL {

    private TermSession mTermSession;
    private String mGetEventCommand;
    protected InputEventConverter mInputEventConverter;

    protected InputEventRecorder(String getEventCommand, InputEventConverter inputEventConverter) {
        mGetEventCommand = getEventCommand;
        mInputEventConverter = inputEventConverter;
    }

    public void listen() {
        TermSettings settings = new TermSettings(App.getApp().getResources(), PreferenceManager.getDefaultSharedPreferences(App.getApp()));
        try {
            mTermSession = new MyShellTermSession(settings, "su\r");
            mTermSession.initializeEmulator(80, 40);
            mTermSession.write("su\r");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
        mInputEventConverter.start();
    }

    @Override
    protected void stopImpl() {
        mTermSession.finish();
        mInputEventConverter.stop();
    }


    protected abstract void parseAndRecordEvent(String eventStr);

    private class MyShellTermSession extends ShellTermSession {

        private boolean mGettingEvents = false;

        public MyShellTermSession(TermSettings settings, String initialCommand) throws IOException {
            super(settings, initialCommand);
        }


        @Override
        protected void processInput(byte[] data, int offset, int count) {
            String[] lines = new String(data, offset, count).split("\n");
            for (String line : lines) {
                System.out.println(line);
                if (!mGettingEvents && line.endsWith("data # ")) {
                    mTermSession.write(mGetEventCommand + "\r");
                    mGettingEvents = true;
                } else if (mGettingEvents) {
                    parseAndRecordEvent(line);
                }
            }
            appendToEmulator(data, offset, count);
        }
    }
}
