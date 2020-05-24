package com.stardust.autojs.core.record.inputevent;

import androidx.annotation.NonNull;

import com.stardust.autojs.core.inputevent.InputEventObserver;

import static com.stardust.util.ScreenMetrics.getDeviceScreenHeight;
import static com.stardust.util.ScreenMetrics.getDeviceScreenWidth;

/**
 * Created by Stardust on 2017/5/3.
 */

public class InputEventToSendEventJsRecorder extends InputEventRecorder {

    private double mLastEventTime;
    private StringBuilder mCode = new StringBuilder();
    private int mTouchDevice = -1;
    private int mLastTouchX = -1;
    private int mLastTouchY = -1;

    public InputEventToSendEventJsRecorder() {
        mCode.append("var sh = new Shell(true);\n")
                .append("sh.SetScreenMetrics(").append(getDeviceScreenWidth()).append(", ")
                .append(getDeviceScreenHeight()).append(");\n");
    }


    @Override
    public void recordInputEvent(@NonNull InputEventObserver.InputEvent event) {
        if (mLastEventTime == 0) {
            mLastEventTime = event.time;
        } else if (event.time - mLastEventTime > 0.03) {
            mCode.append("sh.usleep(").append((long) (1000000 * (event.time - mLastEventTime))).append(");\n");
            mLastEventTime = event.time;
        }
        int device = parseDeviceNumber(event.device);
        int type = (int) Long.parseLong(event.type, 16);
        int code = (int) Long.parseLong(event.code, 16);
        int value = (int) Long.parseLong(event.value, 16);
        if (type == 3) {
            if (code == 53) {
                onTouchX(device, value);
                return;
            }
            if (code == 54) {
                onTouchY(device, value);
                return;
            }
        }
        checkLastTouch();
        mCode.append("sh.SendEvent(");
        if (device != mTouchDevice) {
            mCode.append(device).append(", ");
        }
        mCode.append(type).append(", ")
                .append(code).append(", ")
                .append(value).append(");\n");
    }

    private void checkLastTouch() {
        if (mLastTouchX >= 0) {
            mCode.append("sh.TouchX(").append(mLastTouchX).append(");\n");
            mLastTouchX = -1;
        }
        if (mLastTouchY >= 0) {
            mCode.append("sh.TouchY(").append(mLastTouchY).append(");\n");
            mLastTouchY = -1;
        }
    }


    private void onTouchX(int device, int value) {
        if (mTouchDevice == -1) {
            setTouchDevice(device);
        }
        mLastTouchX = value;
    }

    private void onTouchY(int device, int value) {
        if (mTouchDevice == -1) {
            setTouchDevice(device);
        }
        if (mLastTouchX >= 0) {
            mCode.append("sh.Touch(")
                    .append(mLastTouchX).append(", ")
                    .append(value).append(");\n");
            mLastTouchX = -1;
        } else {
            mLastTouchY = value;
        }
    }

    private void setTouchDevice(int i) {
        mCode.append("sh.SetTouchDevice(").append(i).append(");\n");
        mTouchDevice = i;
    }

    public String getCode() {
        return mCode.toString();
    }

    @Override
    public void stop() {
        super.stop();
        mCode.append("sh.exitAndWaitFor();");
    }

}
