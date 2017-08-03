package com.stardust.autojs.runtime.api;

import android.content.Context;

import com.stardust.autojs.engine.RootAutomatorEngine;
import com.stardust.pio.UncheckedIOException;
import com.stardust.util.ScreenMetrics;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Stardust on 2017/7/16.
 */

public class RootAutomator {


    public static final byte DATA_TYPE_SLEEP = 0;
    public static final byte DATA_TYPE_EVENT = 1;

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

    public AbstractShell.Result writeToDevice(Context context) {
        if (mDevicePath == null) {
            return new RootAutomatorEngine(context).execute(mEventTmpFile.getAbsolutePath());
        } else {
            return new RootAutomatorEngine(context, mDevicePath).execute(mEventTmpFile.getAbsolutePath());
        }
    }

}

