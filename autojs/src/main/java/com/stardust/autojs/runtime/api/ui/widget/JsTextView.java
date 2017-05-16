package com.stardust.autojs.runtime.api.ui.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Stardust on 2017/5/15.
 */

public class JsTextView extends android.support.v7.widget.AppCompatTextView {
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
