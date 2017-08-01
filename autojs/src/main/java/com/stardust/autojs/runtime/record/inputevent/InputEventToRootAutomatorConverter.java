package com.stardust.autojs.runtime.record.inputevent;

import android.support.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.stardust.util.ScreenMetrics.getDeviceScreenHeight;
import static com.stardust.util.ScreenMetrics.getDeviceScreenWidth;

/**
 * Created by Stardust on 2017/8/1.
 */

public class InputEventToRootAutomatorConverter extends InputEventConverter {


    private double mLastEventTime;
    private StringBuilder mCode = new StringBuilder();
    private int mTouchDevice = -1;
    private int mLastTouchX = -1;
    private int mLastTouchY = -1;

    public InputEventToRootAutomatorConverter() {
        mCode.append("var ra = new RootAutomator();\n")
                .append("ra.setScreenMetrics(").append(getDeviceScreenWidth()).append(", ")
                .append(getDeviceScreenHeight()).append(");\n");
    }


    @Override
    public void convertEvent(@NonNull Event event) {
        if (mLastEventTime == 0) {
            mLastEventTime = event.time;
        } else if (event.time - mLastEventTime > 0.005) {
            mCode.append("ra.sleep(").append((long) (1000L * (event.time - mLastEventTime))).append(");\n");
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
        if (device != mTouchDevice) {
            return;
        }
        if (type == 0 && code == 0 && value == 0) {
            mCode.append("ra.sendSync();\n");
            return;
        }
        mCode.append("ra.sendEvent(");
        mCode.append(type).append(", ")
                .append(code).append(", ")
                .append(value).append(");\n");
    }

    private void checkLastTouch() {
        if (mLastTouchX >= 0) {
            mCode.append("ra.touchX(").append(mLastTouchX).append(");\n");
            mLastTouchX = -1;
        }
        if (mLastTouchY >= 0) {
            mCode.append("ra.touchY(").append(mLastTouchY).append(");\n");
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
            mCode.append("ra.touch(")
                    .append(mLastTouchX).append(", ")
                    .append(value).append(");\n");
            mLastTouchX = -1;
        } else {
            mLastTouchY = value;
        }
    }

    private void setTouchDevice(int i) {
        mCode.append("ra.setInputDevice(").append(i).append(");\n");
        mTouchDevice = i;
    }

    @Override
    public String getGetEventCommand() {
        return "getevent -t";
    }

    public String getCode() {
        return mCode.toString();
    }

    @Override
    public void stop() {
        super.stop();
        mCode.append("log(ra.writeToDevice(context));");
    }


}
