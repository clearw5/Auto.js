package com.stardust.scriptdroid.external.floating_window.console;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

import com.jraska.console.Console;
import com.stardust.scriptdroid.R;

/**
 * Created by Stardust on 2017/4/30.
 */

public class FloatingConsoleView extends Console {
    public FloatingConsoleView(Context context) {
        super(context);
        init();
    }

    public FloatingConsoleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FloatingConsoleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public FloatingConsoleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        findViewById(R.id.console_scroll_view).setBackground(getBackground());
        ((TextView) findViewById(R.id.console_text)).setTextIsSelectable(true);
    }
}
