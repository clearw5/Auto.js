package com.stardust.scriptdroid.ui.console;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

import com.jraska.console.Console;
import com.stardust.scriptdroid.R;

/**
 * Created by Stardust on 2017/8/22.
 */

public class LogView extends Console {

    public LogView(Context context) {
        super(context);
        init();
    }

    public LogView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LogView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public LogView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        findViewById(R.id.console_scroll_view).setBackgroundColor(Color.WHITE);
        ((TextView) findViewById(R.id.console_text)).setTextIsSelectable(true);
    }
}
