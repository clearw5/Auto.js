package com.stardust.scriptdroid.ui.settings;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.stardust.theme.ThemeColorManagerCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/5/10.
 */

public class PaintView extends View {

    private List<Path> mPaths = new ArrayList<>();
    private Path mCurrentPath;
    private Paint mPaint;

    public PaintView(Context context) {
        super(context);
        init();
    }

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PaintView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PaintView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(ThemeColorManagerCompat.getColorPrimary());
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(5);
        setWillNotDraw(false);
        setClickable(true);
        setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (Path path : mPaths) {
            canvas.drawPath(path, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getY();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onTouchDown(x, y);
                return true;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(x, y);
                return true;
            case MotionEvent.ACTION_UP:
                onTouchUp(x, y);
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void onTouchDown(float x, float y) {
        if (mCurrentPath != null) {
            mCurrentPath.close();
            mPaths.add(mCurrentPath);
            invalidate();
        }
        mCurrentPath = new Path();
        mCurrentPath.moveTo(x, y);
    }

    private void onTouchMove(float x, float y) {
        mCurrentPath.moveTo(x, y);
    }

    private void onTouchUp(float x, float y) {
        mCurrentPath.close();
        mPaths.add(mCurrentPath);
        mCurrentPath = null;
        invalidate();
    }
}
