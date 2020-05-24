package com.stardust.autojs.core.record.inputevent;

import androidx.annotation.NonNull;

import com.stardust.autojs.core.inputevent.InputEventCodes;
import com.stardust.autojs.core.inputevent.InputEventObserver;
import com.stardust.autojs.engine.RootAutomatorEngine;

import static com.stardust.util.ScreenMetrics.getDeviceScreenHeight;
import static com.stardust.util.ScreenMetrics.getDeviceScreenWidth;

/**
 * Created by Stardust on 2017/8/1.
 */

public class InputEventToRootAutomatorRecorder extends InputEventRecorder {


    private double mLastEventTime;
    private StringBuilder mCode = new StringBuilder();
    private int mTouchDevice = -1;

    public InputEventToRootAutomatorRecorder() {
        mCode.append("var ra = new RootAutomator();\n")
                .append("ra.setScreenMetrics(").append(getDeviceScreenWidth()).append(", ")
                .append(getDeviceScreenHeight()).append(");\n");
    }

    @Override
    public void recordInputEvent(@NonNull InputEventObserver.InputEvent event) {
        if (mLastEventTime == 0) {
            mLastEventTime = event.time;
        } else if (event.time - mLastEventTime > 0.001) {
            mCode.append("sleep(").append((long) (1000L * (event.time - mLastEventTime))).append(");\n");
            mLastEventTime = event.time;
        }
        int device = parseDeviceNumber(event.device);
        int type = (int) Long.parseLong(event.type, 16);
        int code = (int) Long.parseLong(event.code, 16);
        int value = (int) Long.parseLong(event.value, 16);
        if (type == InputEventCodes.EV_ABS) {
            if (code == InputEventCodes.ABS_MT_POSITION_X || code == InputEventCodes.ABS_MT_POSITION_Y) {
                mTouchDevice = device;
                RootAutomatorEngine.setTouchDevice(device);
                onTouch(code, value);
                return;
            }
        }
        if (device != mTouchDevice) {
            return;
        }
        if (type == InputEventCodes.EV_SYN && code == InputEventCodes.SYN_REPORT && value == 0) {
            mCode.append("ra.sendSync();\n");
            return;
        }
        mCode.append("ra.sendEvent(");
        mCode.append(type).append(", ")
                .append(code).append(", ")
                .append(value).append(");\n");
    }

    private void onTouch(int code, int value) {
        if (code == InputEventCodes.ABS_MT_POSITION_X) {
            mCode.append("ra.touchX(").append(value).append(");\n");
        } else {
            mCode.append("ra.touchY(").append(value).append(");\n");
        }
    }

    public String getCode() {
        return mCode.toString();
    }

    @Override
    public void stop() {
        super.stop();
        mCode.append("ra.exit();");
    }


}
