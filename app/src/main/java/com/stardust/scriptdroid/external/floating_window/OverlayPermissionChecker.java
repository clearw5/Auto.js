package com.stardust.scriptdroid.external.floating_window;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.enhancedfloaty.FloatyWindow;

/**
 * Created by Stardust on 2017/5/9.
 */

public class OverlayPermissionChecker {

    public interface Callback {
        void onCheckResult(boolean granted);
    }

    private OnePixelWindow mOnePixelWindow = new OnePixelWindow();
    private Callback mCallback;
    private Context mContext;
    private Handler mHandler;
    private Boolean mCheckResult = null;

    public OverlayPermissionChecker(Context context) {
        mContext = context;
        mHandler = new Handler(context.getMainLooper());
    }

    public void check() {
        mCheckResult = null;
        try {
            FloatyService.addWindow(mOnePixelWindow);
            mContext.startService(new Intent(mContext, FloatyService.class));
        } catch (WindowManager.BadTokenException e) {
            onCheckResult(false);
        }
    }

    public void check(int timeOut) {
        check();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mCheckResult == null) {
                    onCheckResult(false);
                }
            }
        }, timeOut);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    private void onCheckResult(boolean b) {
        mCheckResult = b;
        if (mCallback != null) {
            mCallback.onCheckResult(b);
        }
    }


    private class OnePixelWindow implements FloatyWindow {

        private View mOnePixelView;
        private WindowManager mWindowManager;

        @Override
        public void onCreate(FloatyService floatyService, WindowManager windowManager) {
            mWindowManager = windowManager;
            mOnePixelView = new View(floatyService) {
                @Override
                protected void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    notifyWindowVisible();
                }

                @Override
                protected void onWindowVisibilityChanged(int visibility) {
                    super.onWindowVisibilityChanged(visibility);
                }
            };
            mOnePixelView.setWillNotDraw(false);
            mOnePixelView.setBackgroundColor(Color.RED);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(10, 10,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.TOP | Gravity.START;
            windowManager.addView(mOnePixelView, params);
        }

        private void notifyWindowVisible() {
            onCheckResult(true);
            //close();
        }

        @Override
        public void onServiceDestroy(FloatyService floatyService) {
            close();
        }

        @Override
        public void close() {
            mWindowManager.removeView(mOnePixelView);
            FloatyService.removeWindow(this);
        }
    }

}
