package com.stardust.autojs.core.ui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;

import com.makeramen.roundedimageview.RoundedImageView;

import org.autojs.dynamiclayoutinflater.util.Drawables;

/**
 * Created by Stardust on 2017/11/30.
 */

public class JsImageView extends RoundedImageView {

    private boolean mCircle;

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
        Drawables.loadInto(this, Uri.parse(uri));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mCircle) {
            setCornerRadius(Math.min(getWidth(), getHeight()) / 2);
        }
    }
}
