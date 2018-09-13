package com.stardust.autojs.core.inputevent;

import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.ViewConfiguration;

import com.stardust.autojs.core.inputevent.InputDevices;
import com.stardust.autojs.core.util.ProcessShell;
import com.stardust.autojs.engine.RootAutomatorEngine;
import com.stardust.autojs.runtime.api.AbstractShell;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.util.ScreenMetrics;

import java.io.IOException;

import static com.stardust.autojs.core.inputevent.InputEventCodes.ABS_MT_POSITION_X;
import static com.stardust.autojs.core.inputevent.InputEventCodes.ABS_MT_POSITION_Y;
import static com.stardust.autojs.core.inputevent.InputEventCodes.ABS_MT_TOUCH_MAJOR;
import static com.stardust.autojs.core.inputevent.InputEventCodes.ABS_MT_TRACKING_ID;
import static com.stardust.autojs.core.inputevent.InputEventCodes.BTN_TOOL_FINGER;
import static com.stardust.autojs.core.inputevent.InputEventCodes.BTN_TOUCH;
import static com.stardust.autojs.core.inputevent.InputEventCodes.EV_ABS;
import static com.stardust.autojs.core.inputevent.InputEventCodes.EV_KEY;
import static com.stardust.autojs.core.inputevent.InputEventCodes.EV_SYN;
import static com.stardust.autojs.core.inputevent.InputEventCodes.SYN_MT_REPORT;
import static com.stardust.autojs.core.inputevent.InputEventCodes.SYN_REPORT;

/**
 * Created by Stardust on 2017/7/16.
 */

public class RootAutomator {

    private static final String LOG_TAG = "RootAutomator";

    public static final byte DATA_TYPE_SLEEP = 0;
    public static final byte DATA_TYPE_EVENT = 1;
    public static final byte DATA_TYPE_EVENT_SYNC_REPORT = 2;
    public static final byte DATA_TYPE_EVENT_TOUCH_X = 3;
    public static final byte DATA_TYPE_EVENT_TOUCH_Y = 4;

    @Nullable
    private ScreenMetrics mScreenMetrics;
    private AbstractShell mShell;
    private int mDefaultId = 1;

    public RootAutomator(Context context) {
        mShell = new ProcessShell(true);
        String path = RootAutomatorEngine.getExecutablePath(context);
        String deviceNameOrPath = RootAutomatorEngine.getDeviceNameOrPath(context, InputDevices.getTouchDeviceName());
        mShell.exec("chmod 777 " + path);
        mShell.exec(path + " -d " + deviceNameOrPath);
    }


    public void sendEvent(int type, int code, int value) throws IOException {
        mShell.exec(type + " " + code + " " + value);
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
        if (mScreenMetrics == null)
            return x;
        return mScreenMetrics.scaleX(x);
    }

    public void touchY(int y) throws IOException {
        sendEvent(3, 54, scaleY(y));
    }

    public void sendSync() throws IOException {
        sendEvent(EV_SYN, SYN_REPORT, 0);
    }

    public void sendMtSync() throws IOException {
        sendEvent(EV_SYN, SYN_MT_REPORT, 0);
    }

    private int scaleY(int y) {
        if (mScreenMetrics == null)
            return y;
        return mScreenMetrics.scaleY(y);

    }

    public void tap(int x, int y, int id) throws IOException {
        touchDown(x, y, id);
        touchUp(id);
    }

    public void tap(int x, int y) throws IOException {
        sendEvent(x, y, mDefaultId);
    }

    public void swipe(int x1, int y1, int x2, int y2, int duration, int id) throws IOException {
        long now = SystemClock.uptimeMillis();
        touchDown(x1, y1, id);
        long startTime = now;
        long endTime = startTime + duration;
        while (now < endTime) {
            long elapsedTime = now - startTime;
            float alpha = (float) elapsedTime / duration;
            touchMove((int) lerp(x1, x2, alpha), (int) lerp(y1, y2, alpha), id);
            now = SystemClock.uptimeMillis();
        }
        touchUp(id);
    }

    public void swipe(int x1, int y1, int x2, int y2, int duration) throws IOException {
        swipe(x1, y1, x2, y2, duration, mDefaultId);
    }

    public void swipe(int x1, int y1, int x2, int y2) throws IOException {
        swipe(x1, y1, x2, y2, 300, mDefaultId);
    }

    public void press(int x, int y, int duration, int id) throws IOException {
        touchDown(x, y, id);
        sleep(duration);
        touchUp(id);
    }

    public void press(int x, int y, int duration) throws IOException {
        press(x, y, duration, getDefaultId());
    }

    public void longPress(int x, int y, int id) throws IOException {
        press(x, y, ViewConfiguration.getLongPressTimeout() + 200, id);
    }

    public void longPress(int x, int y) throws IOException {
        press(x, y, ViewConfiguration.getLongPressTimeout() + 200, getDefaultId());
    }

    public void touchDown(int x, int y, int id) throws IOException {
        sendEvent(EV_ABS, ABS_MT_TRACKING_ID, id);
        sendEvent(EV_KEY, BTN_TOUCH, 0x00000001);
        sendEvent(EV_KEY, BTN_TOOL_FINGER, 0x00000001);
        sendEvent(EV_ABS, ABS_MT_POSITION_X, scaleX(x));
        sendEvent(EV_ABS, ABS_MT_POSITION_Y, scaleY(y));
        sendEvent(EV_ABS, ABS_MT_TOUCH_MAJOR, 5);
        sendEvent(EV_SYN, SYN_REPORT, 0x00000000);
    }

    public void touchDown(int x, int y) throws IOException {
        touchDown(x, y, mDefaultId);
    }

    public void touchUp(int id) throws IOException {
        sendEvent(EV_ABS, ABS_MT_TRACKING_ID, id);
        sendEvent(EV_KEY, BTN_TOUCH, 0x00000000);
        sendEvent(EV_KEY, BTN_TOOL_FINGER, 0x00000000);
        sendEvent(EV_SYN, SYN_REPORT, 0x00000000);
    }

    public void touchUp() throws IOException {
        touchUp(mDefaultId);
    }

    public void touchMove(int x, int y, int id) throws IOException {
        sendEvent(EV_ABS, ABS_MT_TRACKING_ID, id);
        sendEvent(EV_ABS, ABS_MT_POSITION_X, scaleX(x));
        sendEvent(EV_ABS, ABS_MT_POSITION_Y, scaleY(y));
        sendEvent(EV_SYN, SYN_REPORT, 0x00000000);
    }

    public void touchMove(int x, int y) throws IOException {
        touchMove(x, y, mDefaultId);
    }

    public int getDefaultId() {
        return mDefaultId;
    }

    public void setDefaultId(int defaultId) {
        mDefaultId = defaultId;
    }

    private void sleep(long duration) throws IOException {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            exit();
            throw new ScriptInterruptedException();
        }
    }

    private static float lerp(float a, float b, float alpha) {
        return (b - a) * alpha + a;
    }

    public void exit() throws IOException {
        sendEvent(0xffff, 0xffff, 0xefefefef);
        mShell.exec("exit");
        mShell.exec("exit");
        mShell.exec("exit");
        mShell.exec("exit");
        mShell.exec("exit");
        mShell.exec("exit");
        mShell.exit();
    }

}

