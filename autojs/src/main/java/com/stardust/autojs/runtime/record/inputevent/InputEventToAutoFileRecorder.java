package com.stardust.autojs.runtime.record.inputevent;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.stardust.autojs.engine.RootAutomatorEngine;
import com.stardust.autojs.runtime.api.RootAutomator;
import com.stardust.pio.UncheckedIOException;
import com.stardust.util.ScreenMetrics;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Stardust on 2017/8/2.
 */

public class InputEventToAutoFileRecorder extends InputEventRecorder {

    private static final String LOG_TAG = "InputEventToAutoFileRec";
    private double mLastEventTime;
    private int mTouchDevice = -1;
    private DataOutputStream mDataOutputStream;
    private File mTmpFile;

    public InputEventToAutoFileRecorder(Context context) {
        try {
            mTmpFile = new File(context.getCacheDir(), SimpleDateFormat.getDateTimeInstance().format(new Date()) + ".auto");
            mTmpFile.deleteOnExit();
            mDataOutputStream = new DataOutputStream(new FileOutputStream(mTmpFile));
            writeFileHeader();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void writeFileHeader() throws IOException {
        mDataOutputStream.writeInt(0x00B87B6D);
        mDataOutputStream.writeInt(RootAutomatorEngine.VERSION);
        mDataOutputStream.writeInt(ScreenMetrics.getDeviceScreenWidth());
        mDataOutputStream.writeInt(ScreenMetrics.getDeviceScreenHeight());
        for (int i = 0; i < 240; i++) {
            mDataOutputStream.writeByte(0);
        }
    }


    @Override
    public void recordInputEvent(@NonNull InputEventObserver.InputEvent event) {
        try {
            convertEventOrThrow(event);
            Log.d(LOG_TAG, "recordInputEvent: " + event);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void convertEventOrThrow(InputEventObserver.InputEvent event) throws IOException {
        if (mLastEventTime == 0) {
            mLastEventTime = event.time;
        } else if (event.time - mLastEventTime > 0.001) {
            mDataOutputStream.writeByte(RootAutomator.DATA_TYPE_SLEEP);
            int n = (int) (1000L * (event.time - mLastEventTime));
            mDataOutputStream.writeInt(n);
            mLastEventTime = event.time;
        }
        int device = parseDeviceNumber(event.device);
        short type = (short) Long.parseLong(event.type, 16);
        short code = (short) Long.parseLong(event.code, 16);
        int value = (int) Long.parseLong(event.value, 16);
        if (type == 3) {
            if (code == 53 || code == 54) {
                mTouchDevice = device;
                RootAutomatorEngine.setTouchDevice(device);
            }
        }
        if (device != mTouchDevice) {
            return;
        }
        mDataOutputStream.writeByte(RootAutomator.DATA_TYPE_EVENT);
        mDataOutputStream.writeShort(type);
        mDataOutputStream.writeShort(code);
        mDataOutputStream.writeInt(value);
    }

    public String getCode() {
        return mTmpFile.getAbsolutePath();
    }

    @Override
    public void stop() {
        super.stop();
        try {
            mDataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
