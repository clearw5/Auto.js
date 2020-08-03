package org.autojs.autojs.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.autojs.autojs.R;

/**
 * Created by Stardust on 2017/1/29.
 */

public class ToolbarMenuItem extends LinearLayout {

    private final int mColorDisabled;
    private final int mColorEnabled;
    private ImageView mImageView;
    private TextView mTextView;
    private Drawable mEnabledDrawable, mDisabledDrawable;

    public ToolbarMenuItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mColorDisabled = ContextCompat.getColor(context, R.color.toolbar_disabled);
        mColorEnabled = ContextCompat.getColor(context, R.color.toolbar);
        init(attrs);
    }

    public ToolbarMenuItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mColorDisabled = ContextCompat.getColor(context, R.color.toolbar_disabled);
        mColorEnabled = ContextCompat.getColor(context, R.color.toolbar);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ToolbarMenuItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mColorDisabled = ContextCompat.getColor(context, R.color.toolbar_disabled);
        mColorEnabled = ContextCompat.getColor(context, R.color.toolbar);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        inflate(getContext(), R.layout.toolbar_menu_item, this);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ToolbarMenuItem);
        String text = a.getString(R.styleable.ToolbarMenuItem_text);
        int iconResId = a.getResourceId(R.styleable.ToolbarMenuItem_icon, 0);
        int iconColor = a.getColor(R.styleable.ToolbarMenuItem_icon_color, Color.TRANSPARENT);
        a.recycle();
        mImageView = findViewById(R.id.icon);
        mTextView = findViewById(R.id.text);
        mTextView.setText(text);
        mTextView.setTextColor(mColorEnabled);
        mImageView.setImageResource(iconResId);
        if (iconColor != Color.TRANSPARENT) {
            mImageView.setImageDrawable(tintDrawable(mImageView.getDrawable(), iconColor));
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
        mTextView.setTextColor(enabled ? mColorEnabled : mColorDisabled);
    }

    private void ensureDisabledDrawable() {
        if (mDisabledDrawable == null) {
            mDisabledDrawable = tintDrawable(mEnabledDrawable, mColorDisabled);
        }
    }

    private void ensureEnabledDrawable() {
        if (mEnabledDrawable == null) {
            mEnabledDrawable = mImageView.getDrawable();
        }
    }

    public static Drawable tintDrawable(Drawable drawable, int color) {
        if (drawable == null || drawable.getConstantState() == null)
            return null;
        Drawable res = drawable.getConstantState().newDrawable().mutate();
        res.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        return res;
    }
}
