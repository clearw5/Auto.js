package com.stardust.scriptdroid.record.inputevent;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Pair;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.tool.Shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jackpal.androidterm.ShellTermSession;
import jackpal.androidterm.emulatorview.TermSession;
import jackpal.androidterm.util.TermSettings;

/**
 * Created by Stardust on 2017/3/6.
 */

public abstract class InputEventRecorder {

    private Thread mRecordThread;
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
            mTermSession = new ShellTermSession(settings, "su\r") {
                boolean enter = false;

                @Override
                protected void processInput(byte[] data, int offset, int count) {
                    String[] lines = new String(data, offset, count).split("\n");
                    for (String line : lines) {
                        System.out.println(line);
                        if (!enter && line.endsWith("data # ")) {
                            mTermSession.write(mGetEventCommand + "\r");
                            enter = true;
                        } else if (enter) {
                            parseAndRecordEvent(line);
                        }
                    }
                    appendToEmulator(data, offset, count);
                }
            };
            mTermSession.initializeEmulator(80, 40);
            mTermSession.write("su\r");
            mRecordThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //readOutput(mTermSession);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            mRecordThread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void start() {
        mInputEventConverter.start();
    }

    public void pause() {
        mInputEventConverter.pause();
    }

    private void readOutput(TermSession shell) {
        boolean enter = false;
        String line;
        try {
            BufferedReader succeedReader = new BufferedReader(new InputStreamReader(mTermSession.getTermIn()));
            while ((line = succeedReader.readLine()) != null) {
                System.out.println(line);
                if (!enter && line.endsWith("data # ")) {
                    mTermSession.write(mGetEventCommand + "\r");
                    enter = true;
                } else if (enter) {
                    parseAndRecordEvent(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void parseAndRecordEvent(String eventStr);

    public abstract String getCode();

    public void stop() {
        mRecordThread.interrupt();
        mTermSession.finish();
        mInputEventConverter.stop();
    }

}
