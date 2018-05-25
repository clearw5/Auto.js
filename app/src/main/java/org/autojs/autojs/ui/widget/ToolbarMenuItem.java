package org.autojs.autojs.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.autojs.autojs.R;

/**
 * Created by Stardust on 2017/1/29.
 */

public class ToolbarMenuItem extends LinearLayout {

    private static final int COLOR_DISABLED = 0X77e0e0e0;
    private ImageView mImageView;
    private TextView mTextView;
    private Drawable mEnabledDrawable, mDisabledDrawable;

    public ToolbarMenuItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ToolbarMenuItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ToolbarMenuItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        inflate(getContext(), R.layout.toolbar_menu_item, this);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ToolbarMenuItem);
        String text = a.getString(R.styleable.ToolbarMenuItem_text);
        int iconResId = a.getResourceId(R.styleable.ToolbarMenuItem_icon, 0);
        int iconColor = a.getColor(R.styleable.ToolbarMenuItem_icon_color, Color.TRANSPARENT);
        a.recycle();
        mImageView = (ImageView) findViewById(R.id.icon);
        mTextView = (TextView) findViewById(R.id.text);
        mTextView.setText(text);
        mImageView.setImageResource(iconResId);
        if (iconColor != Color.TRANSPARENT) {
            mImageView.setImageDrawable(convertDrawableToGrayScale(mImageView.getDrawable(), iconColor));
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled == isEnabled())
            return;
        super.setEnabled(enabled);
        ensureEnabledDrawable();
        ensureDisabledDrawable();
        mImageView.setImageDrawable(enabled ? mEnabledDrawable : mDisabledDrawable);
        mTextView.setTextColor(enabled ? Color.WHITE : COLOR_DISABLED);
    }

    private void ensureDisabledDrawable() {
        if (mDisabledDrawable == null) {
            mDisabledDrawable = convertDrawableToGrayScale(mEnabledDrawable, COLOR_DISABLED);
        }
    }

    private void ensureEnabledDrawable() {
        if (mEnabledDrawable == null) {
            mEnabledDrawable = mImageView.getDrawable();
        }
    }

    public static Drawable convertDrawableToGrayScale(Drawable drawable, int color) {
        if (drawable == null || drawable.getConstantState() == null)
            return null;
        Drawable res = drawable.getConstantState().newDrawable().mutate();
        res.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        return res;
    }
}
