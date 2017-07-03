package com.stardust.hover;

import android.graphics.PixelFormat;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by Stardust on 2017/7/2.
 */

public class WindowViewController extends io.mattcarroll.hover.defaulthovermenu.window.WindowViewController {

    private WindowManager mWindowManager;

    public WindowViewController(@NonNull WindowManager windowManager) {
        super(windowManager);
        mWindowManager = windowManager;
    }

    @Override
    public void addView(int width, int height, boolean isTouchable, @NonNull View view) {
        int touchableFlag = isTouchable ? 0 : WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                width,
                height,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | touchableFlag,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 0;

        mWindowManager.addView(view, params);
    }
}
