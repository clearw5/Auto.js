package com.stardust.autojs.core.ui.widget;

import android.content.Context;
import androidx.core.widget.ImageViewCompat;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.makeramen.roundedimageview.RoundedImageView;

import com.stardust.autojs.core.image.ImageWrapper;
import com.stardust.autojs.core.ui.inflater.util.Drawables;

/**
 * Created by Stardust on 2017/11/30.
 */

public class JsImageView extends RoundedImageView {

    private boolean mCircle;
    private Drawables mDrawables;

    public JsImageView(Context context) {
        super(context);
    }


    public JsImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JsImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setCircle(boolean circle) {
        mCircle = circle;
        if (circle && getWidth() != 0) {
            setCornerRadius(Math.min(getWidth(), getHeight()) / 2);
        }
    }

    public boolean isCircle() {
        return mCircle;
    }

    public void setSource(String uri) {
        getDrawables().setupWithImage(this, uri);
    }

    public void setSource(ImageWrapper image) {
        setImageBitmap(image.getBitmap());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
        if (mCircle) {
            setCornerRadius(getMeasuredWidth() / 2);
        }
    }

    public void setDrawables(Drawables drawables) {
        mDrawables = drawables;
    }

    public Drawables getDrawables() {
        return mDrawables;
    }
}
