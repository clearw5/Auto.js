package com.stardust.autojs.core.ui.widget;

import android.content.Context;
import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class JsSpinner extends androidx.appcompat.widget.AppCompatSpinner {

    private float mTextSize = -1;
    private int mTextStyle = -1;
    private int mTextColor = 0;

    private float mEntryTextSize = -1;
    private int mEntryTextStyle = -1;
    private int mEntryTextColor = 0;

    public JsSpinner(Context context) {
        super(context);
    }

    public JsSpinner(Context context, int mode) {
        super(context, mode);
    }

    public JsSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JsSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public JsSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    public JsSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode, Resources.Theme popupTheme) {
        super(context, attrs, defStyleAttr, mode, popupTheme);
    }


    public float getTextSize() {
        return mTextSize;
    }

    public float getEntryTextSize() {
        return mEntryTextSize;
    }

    public void setEntryTextSize(float entryTextSize) {
        mEntryTextSize = entryTextSize;
    }

    public int getEntryTextStyle() {
        return mEntryTextStyle;
    }

    public void setEntryTextStyle(int entryTextStyle) {
        mEntryTextStyle = entryTextStyle;
    }

    public int getEntryTextColor() {
        return mEntryTextColor;
    }

    public void setEntryTextColor(int entryTextColor) {
        mEntryTextColor = entryTextColor;
    }

    public void setTextSize(float textSize) {
        mTextSize = textSize;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof TextView) {
                ((TextView) child).setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            }
        }
    }


    public int getTextStyle() {
        return mTextStyle;
    }

    public void setTextStyle(int textStyle) {
        mTextStyle = textStyle;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof TextView) {
                ((TextView) child).setTypeface(((TextView) child).getTypeface(), mTextStyle);
            }
        }
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof TextView) {
                ((TextView) child).setTextColor(mTextColor);
            }
        }
    }


    public class Adapter extends ArrayAdapter<String> {

        public Adapter(@NonNull Context context, int resource, @NonNull String[] objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            if (!(view instanceof TextView)) {
                return view;
            }
            TextView textView = (TextView) view;
            if (mTextColor != 0) {
                textView.setTextColor(mTextColor);
            }
            if (mTextSize != -1) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            }
            if (mTextStyle != -1) {
                textView.setTypeface(textView.getTypeface(), mTextStyle);
            }
            return textView;
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = super.getDropDownView(position, convertView, parent);
            if (!(view instanceof TextView)) {
                return view;
            }
            TextView textView = (TextView) view;
            if (mEntryTextColor != 0) {
                textView.setTextColor(mEntryTextColor);
            }
            if (mEntryTextSize != -1) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mEntryTextSize);
            }
            if (mEntryTextStyle != -1) {
                textView.setTypeface(textView.getTypeface(), mEntryTextStyle);
            }
            return textView;
        }
    }
}
