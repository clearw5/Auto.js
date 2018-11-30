package com.stardust.autojs.core.inputevent;

import androidx.annotation.Nullable;
import android.util.Log;
import android.view.InputDevice;


/**
 * Created by Stardust on 2017/8/1.
 */

public class InputDevices {

    private static final String LOG_TAG = "InputDevices";

    @Nullable
    public static String getTouchDeviceName() {
        InputDevice device = getTouchDevice();
        return  device == null ? null : device.getName();
    }

    @Nullable
    public static InputDevice getTouchDevice() {
        for (int id : InputDevice.getDeviceIds()) {
            InputDevice device = InputDevice.getDevice(id);
            Log.d(LOG_TAG, "device: " + device);
            if (supportSource(device, InputDevice.SOURCE_TOUCHSCREEN) || supportSource(device, InputDevice.SOURCE_TOUCHPAD)) {
                return device;
            }
        }
        return null;
    }

    private static boolean supportSource(InputDevice device, int source) {
        return (device.getSources() & source) == source;
    }


}
