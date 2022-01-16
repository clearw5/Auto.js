package com.stardust.enhancedfloaty.util;

import android.os.Build;
import android.view.WindowManager;

public class WindowTypeCompat {


    public static int getWindowType() {
        return getWindowType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    }

    public static int getPhoneWindowType() {
        return getWindowType(WindowManager.LayoutParams.TYPE_PHONE);
    }

    public static int getWindowType(int type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            return type;
        }
    }

}
