package com.stardust.autojs.runtime.api;

import android.content.Context;

import com.stardust.autojs.engine.RootAutomatorEngine;
import com.stardust.pio.UncheckedIOException;
import com.stardust.util.ScreenMetrics;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.stardust.autojs.core.inputevent.InputEventCodes.*;

/**
 * Created by Stardust on 2017/7/16.
 */

public class RootAutomator {


    public static final byte DATA_TYPE_SLEEP = 0;
    public static final byte DATA_TYPE_EVENT = 1;
    public static final byte DATA_TYPE_EVENT_SYNC_REPORT = 2;
    public static final byte DATA_TYPE_EVENT_TOUCH_X = 3;
    public static final byte DATA_TYPE_EVENT_TOUCH_Y = 4;

    private DataOutputStream mTmpFileOutputStream;
    private File mEventTmpFile;
    private ScreenMetrics mScreenMetrics;
    private String mDevicePath;

    public RootAutomator() {
        try {
            mEventTmpFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".auto");
            mEventTmpFile.deleteOnExit();
            mTmpFileOutputStream = new DataOutputStream(new FileOutputStream(mEventTmpFile));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    public void sendEvent(int type, int code, int value) throws IOException {
        mTmpFileOutputStream.writeByte(DATA_TYPE_EVENT);
        mTmpFileOutputStream.writeShort(type);
        mTmpFileOutputStream.writeShort(code);
        mTmpFileOutputStream.writeInt(value);
    }

    public void setInputDevice(int i) throws IOException {
        mDevicePath = "/dev/input/event" + i;
    }

    public void touch(int x, int y) throws IOException {
        touchX(x);
        touchY(y);
    }

    public void setScreenMetrics(int width, int height) {
        if (mScreenMetrics == null) {
            mScreenMetrics = new ScreenMetrics();
        }
        mScreenMetrics.setScreenMetrics(width, height);
    }

    public void touchX(int x) throws IOException {
        sendEvent(3, 53, scaleX(x));
    }

    private int scaleX(int x) {
        return mScreenMetrics.scaleX(x);
    }

    public void touchY(int y) throws IOException {
        sendEvent(3, 54, scaleY(y));
    }

    public void sendSync() throws IOException {
        sendEvent(0, 0, 0);
    }

    private int scaleY(int y) {
        return mScreenMetrics.scaleY(y);

    }

    public void sleep(int n) throws IOException {
        mTmpFileOutputStream.writeByte(DATA_TYPE_SLEEP);
        mTmpFileOutputStream.writeInt(n);
    }

    public void tap(int x, int y) throws IOException {
        //sendEvent(EV_ABS, ABS_MT_TRACKING_ID, 0x0000398c);
        sendEvent(EV_KEY, BTN_TOUCH, 0x00000001);
        sendEvent(EV_KEY, BTN_TOOL_FINGER, 0x00000001);
        sendEvent(EV_ABS, ABS_MT_POSITION_X, x);
        sendEvent(EV_ABS, ABS_MT_POSITION_Y, y);
        sendEvent(EV_SYN, SYN_REPORT, 0x00000000);
        //sendEvent(EV_ABS, ABS_MT_TRACKING_ID, 0xffffffff);
        sendEvent(EV_KEY, BTN_TOUCH, 0x00000000);
        sendEvent(EV_KEY, BTN_TOOL_FINGER, 0x00000000);
        sendEvent(EV_SYN, SYN_REPORT, 0x00000000);
    }

    public void swipe(int x, int y, int duration){

    }

    public AbstractShell.Result writeToDevice(Context context) {
        if (mDevicePath == null) {
            return new RootAutomatorEngine(context).execute(mEventTmpFile.getAbsolutePath());
        } else {
            return new RootAutomatorEngine(context, mDevicePath).execute(mEventTmpFile.getAbsolutePath());
        }
    }

}

