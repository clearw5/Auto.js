package com.stardust.enhancedfloaty.gesture;


import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.GestureDetectorCompat;

import com.stardust.enhancedfloaty.WindowBridge;

/**
 * Created by Stardust on 2017/4/18.
 */

public class DragGesture extends GestureDetector.SimpleOnGestureListener {

    protected WindowBridge mWindowBridge;
    protected View mView;

    private float mKeepToSideHiddenWidthRadio = 0.5f;
    private int mInitialX;
    private int mInitialY;
    private float mInitialTouchX;
    private float mInitialTouchY;
    private View.OnClickListener mOnClickListener;
    private boolean mFlung = false;
    private boolean mKeepToSide;
    private float mPressedAlpha = 0.7f;
    private float mUnpressedAlpha = 1.0f;

    public DragGesture(WindowBridge windowBridge, View view) {
        mWindowBridge = windowBridge;
        mView = view;
        setupView();
    }

    private void setupView() {
        final GestureDetectorCompat gestureDetector = new GestureDetectorCompat(mView.getContext(), this);
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mView.setAlpha(mUnpressedAlpha);
                    if (!mFlung && isKeepToSide()) {
                        keepToSide();
                    }
                }
                return true;
            }
        });
    }

    public float getPressedAlpha() {
        return mPressedAlpha;
    }

    public void setPressedAlpha(float pressedAlpha) {
        mPressedAlpha = pressedAlpha;
    }

    public float getUnpressedAlpha() {
        return mUnpressedAlpha;
    }

    public void setUnpressedAlpha(float unpressedAlpha) {
        mUnpressedAlpha = unpressedAlpha;
    }

    public void setKeepToSide(boolean keepToSide) {
        mKeepToSide = keepToSide;
    }

    public boolean isKeepToSide() {
        return mKeepToSide;
    }

    public void setKeepToSideHiddenWidthRadio(float keepToSideHiddenWidthRadio) {
        mKeepToSideHiddenWidthRadio = keepToSideHiddenWidthRadio;
    }

    public float getKeepToSideHiddenWidthRadio() {
        return mKeepToSideHiddenWidthRadio;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        mInitialX = mWindowBridge.getX();
        mInitialY = mWindowBridge.getY();
        mInitialTouchX = event.getRawX();
        mInitialTouchY = event.getRawY();
        mFlung = false;
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        mWindowBridge.updatePosition(mInitialX + (int) ((e2.getRawX() - mInitialTouchX)),
                mInitialY + (int) ((e2.getRawY() - mInitialTouchY)));
        mView.setAlpha(mPressedAlpha);
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        mFlung = true;
        if (mKeepToSide)
            keepToSide();
        return false;
    }

    public void keepToSide() {
        int x = mWindowBridge.getX();
        int hiddenWidth = (int) (mKeepToSideHiddenWidthRadio * mView.getWidth());
        if (x > mWindowBridge.getScreenWidth() / 2)
            mWindowBridge.updatePosition(mWindowBridge.getScreenWidth() - mView.getWidth() + hiddenWidth, mWindowBridge.getY());
        else
            mWindowBridge.updatePosition(-hiddenWidth, mWindowBridge.getY());
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (mOnClickListener != null)
            mOnClickListener.onClick(mView);
        return super.onSingleTapConfirmed(e);
    }

    public void setOnDraggedViewClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }
}
