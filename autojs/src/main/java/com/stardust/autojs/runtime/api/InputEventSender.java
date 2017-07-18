package com.stardust.autojs.runtime.api;

import com.stardust.util.ScreenMetrics;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Stardust on 2017/7/16.
 */

public class InputEventSender {
    private DataOutputStream mDeviceFile;
    private ScreenMetrics mScreenMetrics;


    public InputEventSender(String devicePath) throws FileNotFoundException {
        mDeviceFile = new DataOutputStream(new FileOutputStream(devicePath));
    }

    public InputEventSender(int i) throws FileNotFoundException {
        this("/dev/input/event" + i);
    }

    public InputEventSender() {

    }


    public void sendEvent(int type, int code, int value) throws IOException {
        for (int i = 0; i < 16; i++)
            mDeviceFile.writeByte(0);
        mDeviceFile.writeShort(type);
        mDeviceFile.writeShort(code);
        mDeviceFile.writeInt(value);
        mDeviceFile.flush();
    }


    public void setInputDevice(int i) throws IOException {
        if (mDeviceFile != null) {
            mDeviceFile.close();
        }
        mDeviceFile = new DataOutputStream(new FileOutputStream("/dev/input/event" + i));
    }

    public void Touch(int x, int y) throws IOException {
        TouchX(x);
        TouchY(y);
    }

    public void setScreenMetrics(int width, int height) {
        if (mScreenMetrics == null) {
            mScreenMetrics = new ScreenMetrics();
        }
        mScreenMetrics.setScreenMetrics(width, height);
    }

    public void TouchX(int x) throws IOException {
        sendEvent(3, 53, scaleX(x));
    }

    private int scaleX(int x) {
        return mScreenMetrics.scaleX(x);
    }

    public void TouchY(int y) throws IOException {
        sendEvent(3, 54, scaleY(y));
    }

    private int scaleY(int y) {
        return mScreenMetrics.scaleY(y);

    }


    public void close() throws IOException {
        mDeviceFile.close();
    }

}

