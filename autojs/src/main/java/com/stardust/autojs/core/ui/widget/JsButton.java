package com.stardust.autojs.core.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by Stardust on 2017/5/15.
 */

public class JsButton extends android.support.v7.widget.AppCompatButton {
    public JsButton(Context context) {
        super(context);
    }

    public JsButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JsButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public String text() {
        return getText().toString();
    }

    public void text(CharSequence text) {
        setText(text);
    }
}
