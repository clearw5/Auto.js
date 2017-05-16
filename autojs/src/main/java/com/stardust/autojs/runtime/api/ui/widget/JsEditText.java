package com.stardust.autojs.runtime.api.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Stardust on 2017/5/15.
 */

public class JsEditText extends android.support.v7.widget.AppCompatEditText {
    public JsEditText(Context context) {
        super(context);
    }

    public JsEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JsEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public String text() {
        return getText().toString();
    }

    public void text(CharSequence text) {
        setText(text);
    }

}
