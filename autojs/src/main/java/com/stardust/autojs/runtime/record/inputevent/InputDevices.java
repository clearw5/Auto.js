package com.stardust.autojs.runtime.record.inputevent;

import android.content.Context;
import android.hardware.input.InputManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.InputDevice;

/**
 * Created by Stardust on 2017/8/1.
 */

public class InputDevices {

    @Nullable
    public static String getTouchDeviceName() {
        for (int id : InputDevice.getDeviceIds()) {
            InputDevice device = InputDevice.getDevice(id);
            if (supportSource(device, InputDevice.SOURCE_TOUCHSCREEN) || supportSource(device, InputDevice.SOURCE_TOUCHPAD)) {
                return device.getName();
            }
        }
        return null;
    }

    private static boolean supportSource(InputDevice device, int source) {
        return (device.getSources() & source) == source;
    }


}
