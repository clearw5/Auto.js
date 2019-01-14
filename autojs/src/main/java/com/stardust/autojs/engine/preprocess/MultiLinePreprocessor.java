package com.stardust.autojs.engine.preprocess;

import androidx.annotation.VisibleForTesting;

import java.io.Reader;
import java.io.StringReader;

/**
 * Created by Stardust on 2017/5/15.
 */

public class MultiLinePreprocessor extends AbstractProcessor {

    private static final int STATE_SINGLE_QUOTE_LITERAL = 0x00000001;
    private static final int STATE_DOUBLE_QUOTE_LITERAL = 0x00000010;
    private static final int STATE_MULTI_LINE = 0x00001100;

    private int mState = 0;
    private int mStateBeforeLiteral = 0;
    private StringBuilder mNewScript;
    private int mLastReturnCharPosition;
    private int i;

    @Override
    protected void handleChar(int ch) {
        boolean shouldAppend = true;
        switch (ch) {
            case '"':
                if (mState == STATE_DOUBLE_QUOTE_LITERAL) {
                    mState = mStateBeforeLiteral;
                } else if (mState != STATE_SINGLE_QUOTE_LITERAL) {
                    mStateBeforeLiteral = mState;
                    mState = STATE_DOUBLE_QUOTE_LITERAL;
                }
                break;
            case '\'':
                if (mState == STATE_SINGLE_QUOTE_LITERAL) {
                    if ((mStateBeforeLiteral & STATE_MULTI_LINE) != 0) {
                        mNewScript.append('\\');
                    }
                    mState = mStateBeforeLiteral;
                } else if (mState != STATE_DOUBLE_QUOTE_LITERAL) {
                    mStateBeforeLiteral = mState;
                    mState = STATE_SINGLE_QUOTE_LITERAL;
                    if ((mStateBeforeLiteral & STATE_MULTI_LINE) != 0) {
                        mNewScript.append('\\');
                    }
                }
                break;
            case '`':
                if (mState == 0) {
                    mState = STATE_MULTI_LINE;
                    mNewScript.append("'");
                    shouldAppend = false;
                } else if (mState == STATE_MULTI_LINE) {
                    mState = 0;
                    mNewScript.append("'");
                    shouldAppend = false;
                }
                break;
            case '\r':
            case '\n':
                if (ch == '\n' && mLastReturnCharPosition == i - 1) {
                    shouldAppend = false;
                    break;
                }
                if (ch == '\r')
                    mLastReturnCharPosition = i;
                if (mState == STATE_MULTI_LINE) {
                    mNewScript.append("\\n'+\n'");
                    shouldAppend = false;
                }
                break;
        }
        if (shouldAppend) {
            mNewScript.append((char) ch);
        }
        i++;
    }

    @Override
    public void reset() {
        mState = 0;
        mStateBeforeLiteral = 0;
        mNewScript = new StringBuilder();
        mLastReturnCharPosition = -2;
        i = 0;
    }

    @Override
    public Reader getReaderAndClear() {
        Reader reader = new StringReader(mNewScript.toString());
        mNewScript = null;
        return reader;
    }

}
