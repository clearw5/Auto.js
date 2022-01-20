package com.stardust.autojs.core.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * Android O (API26) 以下的无法使用AppCompatTextView 采用TextView
 *
 * Created by TonyJiangWJ on 2022/01/18.
 *
 */
@SuppressLint("AppCompatCustomView")
public class JsTextViewOld extends TextView {

    public JsTextViewOld(Context context) {
        super(context);
    }

    public JsTextViewOld(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public JsTextViewOld(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public String text() {
        return getText().toString();
    }

    public void text(CharSequence text) {
        setText(text);
    }

}
