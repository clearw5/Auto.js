package com.stardust.scriptdroid.ui.console;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stardust.scriptdroid.R;
import com.stardust.util.MapEntries;
import com.stardust.util.SparseArrayEntries;

import java.io.Closeable;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stardust on 2017/5/2.
 */

public class ConsoleView extends LinearLayout implements StardustConsole.LogListener {

    private static final SparseArray<Integer> COLORS = new SparseArrayEntries<Integer>()
            .entry(Log.VERBOSE, 0xdfc0c0c0)
            .entry(Log.DEBUG, 0xdfffffff)
            .entry(Log.INFO, 0xff64dd17)
            .entry(Log.WARN, 0xff2962ff)
            .entry(Log.ERROR, 0xffd50000)
            .entry(Log.ASSERT, 0xffff534e)
            .sparseArray();

    private StardustConsole mConsole;
    private TextView mTextView;
    private EditText mEditText;

    public ConsoleView(Context context) {
        super(context);
        init();
    }

    public ConsoleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ConsoleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.console_view, this);
        mTextView = (TextView) findViewById(R.id.textView);
    }

    private void initEditText() {
        //mEditText = (EditText) findViewById(R.id.ediText);
        mEditText.setFocusableInTouchMode(true);
        mTextView.setMovementMethod(new ScrollingMovementMethod());
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });
    }

    public void setConsole(StardustConsole console) {
        mConsole = console;
        mConsole.setLogListener(this);
    }

    @Override
    public void onNewLog(StardustConsole.Log log) {
        log(log.level, log.content);
        log(log.level, "\n");
    }

    private void log(int level, CharSequence log) {
        int color = getColorForLevel(level);
        final Spannable spannable = buildColorSpannable(log, color);
        post(new Runnable() {
            @Override
            public void run() {
                mTextView.append(spannable);
            }

        });
    }

    private void scrollToBottom() {
        mTextView.post(new Runnable() {
            @Override
            public void run() {
                mTextView.scrollTo(0, mTextView.getLayout().getLineTop(mTextView.getLineCount()));
            }
        });
    }

    private Spannable buildColorSpannable(CharSequence log, int color) {
        Spannable spannable = new SpannableString(log);
        spannable.setSpan(new ForegroundColorSpan(color), 0, log.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    private int getColorForLevel(int level) {
        return COLORS.get(level);
    }

    @Override
    public void onLogClear() {
        mTextView.setText("");
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            refreshLog();
        }
    }

    private void refreshLog() {
        if (mConsole != null) {
            mTextView.setText("");
            for (StardustConsole.Log log : mConsole.getLogs()) {
                onNewLog(log);
            }
        }
    }
}
