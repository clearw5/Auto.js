package com.stardust.autojs.runtime.api.ui.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Stardust on 2017/5/15.
 */

public class JsButton extends Button {
    public JsButton(Context context) {
        super(context);
    }

    public JsButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JsButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public JsButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public String text() {
        return getText().toString();
    }
}
