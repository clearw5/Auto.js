package com.stardust.scriptdroid.record.inputevent;

import android.preference.PreferenceManager;
import android.util.Log;

import com.stardust.pio.UncheckedIOException;
import com.stardust.scriptdroid.App;
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

public  class InputEventRecorder extends Recorder.AbstractRecorder {

    private static final String TAG = "InputEventRecorder";
    private TermSession mTermSession;
    private String mGetEventCommand;
    protected InputEventConverter mInputEventConverter;

    protected InputEventRecorder(InputEventConverter inputEventConverter) {
        mGetEventCommand = inputEventConverter.getGetEventCommand();
        mInputEventConverter = inputEventConverter;
    }

    public void listen() {
        TermSettings settings = new TermSettings(App.getApp().getResources(), PreferenceManager.getDefaultSharedPreferences(App.getApp()));
        try {
            mTermSession = new MyShellTermSession(settings, "su");
            mTermSession.initializeEmulator(80, 40);
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
        mInputEventConverter.resume();
    }

    @Override
    protected void stopImpl() {
        mTermSession.finish();
        mInputEventConverter.stop();
    }

    @Override
    public String getCode() {
        return mInputEventConverter.getCode();
    }

    protected void convertEvent(String eventStr) {
        mInputEventConverter.convertEventIfFormatCorrect(eventStr);
    }

    private class MyShellTermSession extends ShellTermSession {

        private volatile boolean mGettingEvents = false;

        private BufferedReader mBufferedReader;
        private OutputStream mOutputStream;
        private Thread mReadingThread;

        public MyShellTermSession(TermSettings settings, String initialCommand) throws IOException {
            super(settings, initialCommand);
            PipedInputStream pipedInputStream = new PipedInputStream(8192);
            mBufferedReader = new BufferedReader(new InputStreamReader(pipedInputStream));
            mOutputStream = new PipedOutputStream(pipedInputStream);
            startReadingThread();
        }

        private void startReadingThread() {
            mReadingThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String line;
                    try {
                        while (!Thread.currentThread().isInterrupted()
                                && (line = mBufferedReader.readLine()) != null){
                            onNewLine(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            mReadingThread.start();
        }

        private void onNewLine(String line) {
            Log.d(TAG, line);
            if (!mGettingEvents && line.endsWith(" $ su")) {
                mTermSession.write(mGetEventCommand + "\r");
                mGettingEvents = true;
            } else if (mGettingEvents) {
                convertEvent(line);
            }
        }


        @Override
        protected void processInput(byte[] data, int offset, int count) {
            try {
                mOutputStream.write(data, offset, count);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public void finish() {
            super.finish();
            mReadingThread.interrupt();
            try {
                mBufferedReader.close();
                mOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
