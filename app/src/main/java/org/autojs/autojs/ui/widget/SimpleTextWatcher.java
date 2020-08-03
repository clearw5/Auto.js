package org.autojs.autojs.ui.widget;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by Stardust on 2018/2/16.
 */

public class SimpleTextWatcher implements TextWatcher {

    public interface AfterTextChangedListener {
        void afterTextChanged(Editable s);
    }

    public interface BeforeTextChangedListener {
        void beforeTextChanged(CharSequence s, int start, int count, int after);
    }

    public interface OnTextChangedListener {
        void onTextChanged(CharSequence s, int start, int before, int count);
    }

    private BeforeTextChangedListener mBeforeTextChangedListener;
    private OnTextChangedListener mOnTextChangedListener;
    private AfterTextChangedListener mAfterTextChangedListener;

    public SimpleTextWatcher(BeforeTextChangedListener beforeTextChangedListener, OnTextChangedListener onTextChangedListener, AfterTextChangedListener afterTextChangedListener) {
        mBeforeTextChangedListener = beforeTextChangedListener;
        mOnTextChangedListener = onTextChangedListener;
        mAfterTextChangedListener = afterTextChangedListener;
    }

    public SimpleTextWatcher(AfterTextChangedListener afterTextChangedListener) {
        mAfterTextChangedListener = afterTextChangedListener;
    }

    public SimpleTextWatcher(BeforeTextChangedListener beforeTextChangedListener) {
        mBeforeTextChangedListener = beforeTextChangedListener;
    }

    public SimpleTextWatcher(OnTextChangedListener onTextChangedListener) {
        mOnTextChangedListener = onTextChangedListener;
    }

    public SimpleTextWatcher() {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (mBeforeTextChangedListener != null) {
            mBeforeTextChangedListener.beforeTextChanged(s, start, count, after);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mOnTextChangedListener != null) {
            mOnTextChangedListener.onTextChanged(s, start, before, count);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mAfterTextChangedListener != null) {
            mAfterTextChangedListener.afterTextChanged(s);
        }
    }
}
