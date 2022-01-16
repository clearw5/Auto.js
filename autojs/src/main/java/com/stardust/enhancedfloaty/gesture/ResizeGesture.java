package com.stardust.enhancedfloaty.gesture;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.stardust.enhancedfloaty.WindowBridge;

/**
 * Created by Stardust on 2017/4/18.
 */

public class ResizeGesture extends GestureDetector.SimpleOnGestureListener {

    public static ResizeGesture enableResize(View resizer, @Nullable View resizableView, WindowBridge windowBridge) {
        ResizeGesture resizeGesture = new ResizeGesture(windowBridge, resizer, resizableView);
        final GestureDetector detector = new GestureDetector(resizer.getContext(), resizeGesture);
        resizer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return true;
            }
        });
        return resizeGesture;
    }

    public static ResizeGesture enableResize(View resizer, WindowBridge windowBridge) {
        return enableResize(resizer, null, windowBridge);
    }

    private WindowBridge mWindowBridge;
    private float initialTouchX;
    private float initialTouchY;
    private int mInitialWidth, mInitialHeight;
    private View mResizerView;
    private int mMinHeight = 200, mMinWidth = 200;
    private final int mStatusBarHeight;
    private View mResizableView;


    public ResizeGesture(WindowBridge windowBridge, View resizerView, @Nullable View resizableView) {
        mWindowBridge = windowBridge;
        mResizerView = resizerView;
        mResizableView = resizableView;
        mStatusBarHeight = getStatusBarHeight(resizerView.getContext());
    }

    private int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void setMinHeight(int minHeight) {
        mMinHeight = minHeight;
    }

    public void setMinWidth(int minWidth) {
        mMinWidth = minWidth;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        initialTouchX = event.getRawX();
        initialTouchY = event.getRawY();
        mInitialWidth = mResizableView != null ? mResizableView.getWidth() : mWindowBridge.getWidth();
        mInitialHeight = mResizableView != null ? mResizableView.getHeight() : mWindowBridge.getHeight();
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, final MotionEvent e2, float distanceX, float distanceY) {
        int newWidth = mInitialWidth + (int) ((e2.getRawX() - initialTouchX));
        int newHeight = mInitialHeight + (int) ((e2.getRawY() - initialTouchY));
        newWidth = Math.max(mMinWidth, newWidth);
        newHeight = Math.max(mMinHeight, newHeight);
        newWidth = Math.min(mWindowBridge.getScreenWidth() - getX() - mResizerView.getWidth(), newWidth);
        newHeight = Math.min(mWindowBridge.getScreenHeight() - getY() - mResizerView.getHeight() - mStatusBarHeight, newHeight);
        updateMeasure(newWidth, newHeight);
        return true;
    }

    private void updateMeasure(int newWidth, int newHeight) {
        if (mResizableView == null) {
            mWindowBridge.updateMeasure(newWidth, newHeight);
        } else {
            ViewGroup.LayoutParams params = mResizableView.getLayoutParams();
            params.width = newWidth;
            params.height = newHeight;
            mResizableView.setLayoutParams(params);
        }
    }

    private int getY() {
        return mResizableView != null ? (int) mResizableView.getY() : mWindowBridge.getY();
    }

    private int getX() {
        return mResizableView != null ? (int) mResizableView.getX() : mWindowBridge.getX();
    }
}