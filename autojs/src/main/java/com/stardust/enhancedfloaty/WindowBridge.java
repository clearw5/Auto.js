package com.stardust.enhancedfloaty;

import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by Stardust on 2017/4/18.
 */

public interface WindowBridge {
    int getX();

    int getY();

    void updatePosition(int x, int y);

    int getWidth();

    int getHeight();

    void updateMeasure(int width, int height);

    int getScreenWidth();

    int getScreenHeight();

    class DefaultImpl implements WindowBridge {

        DisplayMetrics mDisplayMetrics;
        private WindowManager.LayoutParams mWindowLayoutParams;
        private WindowManager mWindowManager;
        private View mWindowView;

        public DefaultImpl(WindowManager.LayoutParams windowLayoutParams, WindowManager windowManager, View windowView) {
            mWindowLayoutParams = windowLayoutParams;
            mWindowManager = windowManager;
            mWindowView = windowView;
        }

        @Override
        public int getX() {
            return mWindowLayoutParams.x;
        }

        @Override
        public int getY() {
            return mWindowLayoutParams.y;
        }

        @Override
        public void updatePosition(int x, int y) {
            mWindowLayoutParams.x = x;
            mWindowLayoutParams.y = y;
            mWindowManager.updateViewLayout(mWindowView, mWindowLayoutParams);
        }

        @Override
        public int getWidth() {
            return mWindowView.getWidth();
        }

        @Override
        public int getHeight() {
            return mWindowView.getHeight();
        }

        @Override
        public void updateMeasure(int width, int height) {
            mWindowLayoutParams.width = width;
            mWindowLayoutParams.height = height;
            mWindowManager.updateViewLayout(mWindowView, mWindowLayoutParams);
        }

        @Override
        public int getScreenWidth() {
            ensureDisplayMetrics();
            return mDisplayMetrics.widthPixels;
        }

        @Override
        public int getScreenHeight() {
            ensureDisplayMetrics();
            return mDisplayMetrics.heightPixels;
        }

        private void ensureDisplayMetrics() {
            if (mDisplayMetrics == null) {
                mDisplayMetrics = new DisplayMetrics();
                mWindowManager.getDefaultDisplay().getMetrics(mDisplayMetrics);
            }
        }
    }
}