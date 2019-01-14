package com.stardust.autojs.core.record.inputevent;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;

import com.stardust.autojs.core.inputevent.InputEventCodes;
import com.stardust.autojs.core.inputevent.InputEventObserver;
import com.stardust.autojs.core.inputevent.RootAutomator;
import com.stardust.autojs.engine.RootAutomatorEngine;
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
            writeSleep((int) (1000L * (event.time - mLastEventTime)));
            mLastEventTime = event.time;
        }
        int device = parseDeviceNumber(event.device);
        short type = (short) Long.parseLong(event.type, 16);
        short code = (short) Long.parseLong(event.code, 16);
        int value = (int) Long.parseLong(event.value, 16);
        if (type == InputEventCodes.EV_ABS) {
            if (code == InputEventCodes.ABS_MT_POSITION_X || code == InputEventCodes.ABS_MT_POSITION_Y) {
                mTouchDevice = device;
                RootAutomatorEngine.setTouchDevice(device);
                writeTouch(code, value);
                return;
            }
        }
        if (type == InputEventCodes.EV_SYN && code == InputEventCodes.SYN_REPORT && value == 0) {
            writeSyncReport();
            return;
        }
        if (device != mTouchDevice) {
            return;
        }
        mDataOutputStream.writeByte(RootAutomator.DATA_TYPE_EVENT);
        mDataOutputStream.writeShort(type);
        mDataOutputStream.writeShort(code);
        mDataOutputStream.writeInt(value);
        Log.d(LOG_TAG, "write event: " + event);
    }

    private void writeSleep(int millis) throws IOException {
        mDataOutputStream.writeByte(RootAutomator.DATA_TYPE_SLEEP);
        mDataOutputStream.writeInt(millis);
        Log.d(LOG_TAG, "write sleep: " + millis);
    }

    private void writeSyncReport() throws IOException {
        mDataOutputStream.writeByte(RootAutomator.DATA_TYPE_EVENT_SYNC_REPORT);
        Log.d(LOG_TAG, "write sync report");
    }

    private void writeTouch(short code, int value) throws IOException {
        if (code == InputEventCodes.ABS_MT_POSITION_X) {
            mDataOutputStream.writeByte(RootAutomator.DATA_TYPE_EVENT_TOUCH_X);
            Log.d(LOG_TAG, "write touch x: " + value);
        } else {
            mDataOutputStream.writeByte(RootAutomator.DATA_TYPE_EVENT_TOUCH_Y);
            Log.d(LOG_TAG, "write touch y: " + value);
        }
        mDataOutputStream.writeInt(value);
    }

    public String getCode() {
        return null;
    }

    @Override
    public String getPath() {
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
