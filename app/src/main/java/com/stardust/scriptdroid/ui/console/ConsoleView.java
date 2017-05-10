package com.stardust.scriptdroid.ui.console;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.stardust.enhancedfloaty.ResizableExpandableFloatyWindow;
import com.stardust.scriptdroid.R;
import com.stardust.util.SparseArrayEntries;

/**
 * Created by Stardust on 2017/5/2.
 */

public class ConsoleView extends FrameLayout implements StardustConsole.LogListener {

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
    private ResizableExpandableFloatyWindow mWindow;
    private LinearLayout mInputContainer;
    private ScrollView mContentContainer;

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
        mTextView = (TextView) findViewById(R.id.content);
        mTextView.setMovementMethod(new ScrollingMovementMethod());
        mContentContainer = (ScrollView) findViewById(R.id.content_container);
        initEditText();
        initSubmitButton();
    }

    private void initSubmitButton() {
        final Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence input = mEditText.getText();
                submitInput(input);
            }
        });
    }

    private void submitInput(CharSequence input) {
        if (android.text.TextUtils.isEmpty(input)) {
            return;
        }
        if (mConsole.submitInput(input)) {
            mEditText.setText("");
        }
    }

    private void initEditText() {
        mEditText = (EditText) findViewById(R.id.input);
        mEditText.setFocusableInTouchMode(true);
        mInputContainer = (LinearLayout) findViewById(R.id.input_container);
        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWindow != null) {
                    mWindow.requestWindowFocus();
                    mEditText.requestFocus();
                }
            }
        };
        mEditText.setOnClickListener(listener);
        mInputContainer.setOnClickListener(listener);
    }

    public void setConsole(StardustConsole console) {
        mConsole = console;
        mConsole.setConsoleView(this);
    }

    @Override
    public void onNewLog(StardustConsole.Log log) {
        log(log.level, log.content);
    }

    private void log(int level, CharSequence log) {
        int color = getColorForLevel(level);
        final Spannable spannable = buildColorSpannable(log, color);
        post(new Runnable() {
            @Override
            public void run() {
                mTextView.append(spannable);
                mContentContainer.fullScroll(View.FOCUS_DOWN);
                mEditText.requestFocus();
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

    public void setWindow(ResizableExpandableFloatyWindow window) {
        mWindow = window;
    }

    public void showEditText() {
        post(new Runnable() {
            @Override
            public void run() {
                mWindow.requestWindowFocus();
                mInputContainer.setVisibility(VISIBLE);
                mEditText.requestFocus();
            }
        });
    }
}
