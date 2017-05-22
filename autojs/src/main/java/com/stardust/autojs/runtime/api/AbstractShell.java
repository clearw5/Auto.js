package com.stardust.autojs.runtime.api;

import android.text.TextUtils;

import com.stardust.util.ScreenMetrics;

/**
 * Created by Stardust on 2017/4/24.
 */

public abstract class AbstractShell {


    public static class Result {
        public int code = -1;
        public String error;
        public String result;

        @Override
        public String toString() {
            return "ShellResult{" +
                    "code=" + code +
                    ", error='" + error + '\'' +
                    ", result='" + result + '\'' +
                    '}';
        }
    }

    static final String COMMAND_SU = "su";
    static final String COMMAND_SH = "sh";
    static final String COMMAND_EXIT = "exit\n";
    static final String COMMAND_LINE_END = "\n";


    private int mTouchDevice;
    private ScreenMetrics mScreenMetrics;

    private boolean mRoot;

    public AbstractShell() {
        this(false);
    }

    public AbstractShell(boolean root) {
        mRoot = root;
        init(root ? COMMAND_SU : COMMAND_SH);
    }

    public boolean isRoot() {
        return mRoot;
    }

    protected abstract void init(String initialCommand);

    public abstract void exec(String command);

    public abstract void exit();

    public void SetTouchDevice(int touchDevice) {
        mTouchDevice = touchDevice;
    }

    public void SendEvent(int type, int code, int value) {
        SendEvent(mTouchDevice, type, code, value);
    }

    public void SendEvent(int device, int type, int code, int value) {
        exec(TextUtils.join("", new Object[]{"sendevent /dev/input/event", device, " ", type, " ", code, " ", value}));
    }

    public void SetScreenMetrics(int width, int height) {
        if (mScreenMetrics == null) {
            mScreenMetrics = new ScreenMetrics();
        }
        mScreenMetrics.setScreenMetrics(width, height);
    }

    public void SetScreenMetrics(ScreenMetrics screenMetrics) {
        mScreenMetrics = screenMetrics;
    }


    public void Touch(int x, int y) {
        TouchX(x);
        TouchY(y);
    }

    public void TouchX(int x) {
        SendEvent(mTouchDevice, 3, 53, scaleX(x));
    }

    private int scaleX(int x) {
        return mScreenMetrics.scaleX(x);
    }

    public void TouchY(int y) {
        SendEvent(mTouchDevice, 3, 54, scaleY(y));
    }

    private int scaleY(int y) {
        return mScreenMetrics.scaleY(y);

    }

    public void Tap(int x, int y) {
        exec("input tap " + scaleX(x) + " " + scaleY(y));
    }

    public void Swipe(int x1, int y1, int x2, int y2) {
        exec(com.stardust.util.TextUtils.join(" ", "input", "tap", scaleX(x1), scaleY(y1), scaleX(x2), scaleY(y2)));
    }

    public void Swipe(int x1, int y1, int x2, int y2, int time) {
        exec(com.stardust.util.TextUtils.join(" ", "input", "tap", scaleX(x1), scaleY(y1), scaleX(x2), scaleY(y2), time));
    }

    public void KeyCode(int keyCode) {
        exec("input keyevent " + keyCode);
    }

    public void KeyCode(String keyCode) {
        exec("input keyevent " + keyCode);
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

    public void Input(String text) {
        exec("input text " + text);
    }

    public void Screencap(String path) {
        exec("screencap -p " + path);
    }

    public void Text(String text) {
        Input(text);
    }

    public abstract void exitAndWaitFor();
}
