package com.stardust.autojs.core.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by Stardust on 2017/5/15.
 */

@SuppressLint("AppCompatCustomView")
public class JsTextView extends AppCompatTextView {

    public JsTextView(Context context) {
        super(context);
    }

    public JsTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public JsTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public String text() {
        return getText().toString();
    }

    public void text(CharSequence text) {
        setText(text);
    }
}
