package com.stardust.scriptdroid.record.inputevent;

import android.support.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.stardust.util.ScreenMetrics.getScreenHeight;
import static com.stardust.util.ScreenMetrics.getScreenWidth;

/**
 * Created by Stardust on 2017/5/3.
 */

public class InputEventToSendEventJsConverter extends InputEventConverter {

    private final static Pattern LAST_INT_PATTERN = Pattern.compile("[^0-9]+([0-9]+)$");
    private double mLastEventTime;
    private StringBuilder mCode = new StringBuilder();
    private int mTouchDevice = -1;
    private int mLastTouchX = -1;
    private int mLastTouchY = -1;

    public InputEventToSendEventJsConverter() {
        mCode.append("var sh = new Shell(true);\n")
                .append("sh.SetScreenScale(").append(getScreenWidth()).append(", ")
                .append(getScreenHeight()).append(");\n");
    }


    @Override
    public void convertEvent(@NonNull Event event) {
        if (mLastEventTime == 0) {
            mLastEventTime = event.time;
        } else if (event.time - mLastEventTime > 0.03) {
            mCode.append("sleep(").append((long) (1000 * (event.time - mLastEventTime))).append(");\n");
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


    private int parseDeviceNumber(String device) {
        Matcher matcher = LAST_INT_PATTERN.matcher(device);
        if (matcher.find()) {
            String someNumberStr = matcher.group(1);
            return Integer.parseInt(someNumberStr);
        }
        return -1;
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
        mCode.append("sh.exitAndWaitFor();");
    }

    private static String hex2dec(String hex) {
        try {
            return String.valueOf((int) Long.parseLong(hex, 16));
        } catch (NumberFormatException e) {
            throw new EventFormatException(e);
        }
    }
}
