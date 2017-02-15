package com.stardust.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.widget.TextView;

import com.zzhoujay.markdown.MarkDown;

/**
 * Created by Stardust on 2017/2/1.
 */

public class MarkdownView extends TextView {
    public MarkdownView(Context context) {
        super(context);
        init();
    }

    public MarkdownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MarkdownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MarkdownView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setMovementMethod(ScrollingMovementMethod.getInstance());
        setVerticalScrollBarEnabled(true);
        setTextIsSelectable(true);
        setClickable(true);
    }

    public void loadMarkdown(final String text, final Html.ImageGetter imageGetter) {
        post(new Runnable() {
            @Override
            public void run() {
                Spanned spanned = MarkDown.fromMarkdown(text, imageGetter, MarkdownView.this);
                setText(spanned);
            }
        });
    }

    public void loadMarkdown(final String text) {
        loadMarkdown(text, null);
    }

}
