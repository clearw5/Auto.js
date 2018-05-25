package org.autojs.autojs.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.autojs.autojs.R;

/**
 * Created by Stardust on 2017/3/10.
 */

public class LevelBeamView extends View {

    private static final String TAG = "LevelBeamView";

    private static final int[] colors = {
            0xff1abc9c,
            0xff3498db,
            0xffe67e22,
            0xff8e44ad,
            0xfff1c40f,
            0xff2ecc71,
    };

    private int mLevel;

    private int mPaddingLeft, mPaddingRight;
    private int mLinesWidth;
    private int mLinesOffset;
    private Paint mLinePaint;

    public LevelBeamView(Context context) {
        super(context);
        init();
    }

    public LevelBeamView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LevelBeamView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setLevel(int level) {
        mLevel = level;
        requestLayout();
    }

    private void init() {
        setWillNotDraw(false);
        mPaddingLeft = (int) getResources().getDimension(R.dimen.level_beam_view_padding_left);
        mPaddingRight = (int) getResources().getDimension(R.dimen.level_beam_view_padding_right);

        mLinesWidth = (int) getResources().getDimension(R.dimen.level_beam_view_line_width);
        mLinesOffset = (int) getResources().getDimension(R.dimen.level_beam_view_line_offset);

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(Color.RED);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setStrokeWidth(mLinesWidth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = mPaddingLeft + mPaddingRight + (mLevel + 1) * (mLinesWidth + mLinesOffset);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int lvl = 0; lvl <= mLevel; lvl++) {
            float LINE_X = mPaddingLeft + lvl * mLinesWidth;
            if (lvl >= 1) {
                LINE_X += lvl * mLinesOffset;
            }
            mLinePaint.setColor(getColorForLevel(lvl));
            canvas.drawLine(LINE_X, 0, LINE_X, canvas.getHeight(), mLinePaint);
        }
    }

    private int getColorForLevel(int level) {
        return colors[level % colors.length];
    }

}
