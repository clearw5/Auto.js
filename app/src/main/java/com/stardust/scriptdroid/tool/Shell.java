package com.stardust.scriptdroid.tool;

import android.content.Context;
import android.preference.PreferenceManager;

import com.stardust.pio.UncheckedIOException;
import com.stardust.scriptdroid.App;

import java.io.IOException;

import jackpal.androidterm.ShellTermSession;
import jackpal.androidterm.emulatorview.TermSession;
import jackpal.androidterm.util.TermSettings;

/**
 * Created by Stardust on 2017/4/24.
 */

public class Shell {

    private TermSession mTermSession;
    private String mOutput;

    public Shell(boolean root) {
        this(App.getApp(), root ? "su\n" : "sh\n");
    }

    public Shell(Context context, String initialCommand) {
        TermSettings settings = new TermSettings(context.getResources(), PreferenceManager.getDefaultSharedPreferences(context));
        try {
            mTermSession = new MyShellTermSession(settings, initialCommand);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public String execAndWaitFor(String command) {
        mTermSession.write(command + "\n");
        mOutput = null;
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return mOutput;
    }

    public String execAndWaitFor(String command, int millis) {
        mTermSession.write(command + "\n");
        mOutput = null;
        synchronized (this) {
            try {
                wait(millis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return mOutput;
    }

    public void exec(String command) {
        mTermSession.write(command);
    }

    public void execute(String command) {
        mTermSession.write(command);
    }


    public void Tap(int x, int y) {
        execute("input tap " + x + " " + y);
    }

    public void Swipe(int x1, int y1, int x2, int y2) {
        execute("input swipe " + x1 + " " + y1 + " " + x2 + " " + y2);
    }

    public void Swipe(int x1, int y1, int x2, int y2, long duration) {
        execute("input swipe " + x1 + " " + y1 + " " + x2 + " " + y2 + " " + duration);
    }

    public void KeyCode(int keyCode) {
        execute("input keyevent " + keyCode);
    }

    public void KeyCode(String keyCode) {
        execute("input keyevent " + keyCode);
    }

    public void Home() {
        KeyCode(3);
    }

    public void Back() {
        KeyCode(4);
    }

    public void Power() {
        KeyCode(26);
    }

    public void Up() {
        KeyCode(19);
    }

    public void Down() {
        KeyCode(20);
    }

    public void Left() {
        KeyCode(21);
    }

    public void Right() {
        KeyCode(22);
    }

    public void OK() {
        KeyCode(23);
    }

    public void VolumeUp() {
        KeyCode(24);
    }

    public void VolumeDown() {
        KeyCode(25);
    }

    public void Menu() {
        KeyCode(1);
    }

    public void Camera() {
        KeyCode(27);
    }

    public void Text(String text) {
        execute("input text " + text);
    }

    private class MyShellTermSession extends ShellTermSession {

        public MyShellTermSession(TermSettings settings, String initialCommand) throws IOException {
            super(settings, initialCommand);
        }


        @Override
        protected void processInput(byte[] data, int offset, int count) {
            mOutput = new String(data, offset, count);
            synchronized (Shell.this) {
                Shell.this.notifyAll();
            }
            appendToEmulator(data, offset, count);
        }
    }

}
