package org.autojs.autojs.ui.edit.editor;


import android.text.Editable;
import android.text.TextWatcher;
import android.util.TimingLogger;

import com.stardust.autojs.rhino.TokenStream;
import com.stardust.pio.UncheckedIOException;

import org.autojs.autojs.ui.edit.theme.Theme;
import org.autojs.autojs.ui.widget.SimpleTextWatcher;

import org.mozilla.javascript.Token;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class JavaScriptHighlighter implements SimpleTextWatcher.AfterTextChangedListener {


    public static class HighlightTokens {

        public final int[] colors;
        private String mText;
        private int mCount;
        private final int mId;

        public HighlightTokens(String text, int id) {
            colors = new int[text.length()];
            mText = text;
            mId = id;
        }

        public int getId() {
            return mId;
        }

        public void addToken(int tokenStart, int tokenEnd, int color) {
            if (mCount < tokenStart) {
                int c = mCount > 0 ? colors[mCount - 1] : color;
                for (int i = mCount; i < tokenStart; i++) {
                    colors[i] = c;
                }
            }
            for (int i = tokenStart; i < tokenEnd; i++) {
                colors[i] = color;
            }
            mCount = tokenEnd;
        }

        @Override
        public String toString() {
            return super.toString() + "{count = " + mCount + ", length = " + mText.length() + "}";
        }

        public int getCharCount() {
            return mCount;
        }

        public String getText() {
            return mText;
        }
    }

    private Theme mTheme;
    private CodeEditText mCodeEditText;
    private ThreadPoolExecutor mExecutorService = new ThreadPoolExecutor(3, 6,
            2L, TimeUnit.MINUTES, new LinkedBlockingQueue<>());
    private AtomicInteger mRunningHighlighterId = new AtomicInteger();
    private TimingLogger mLogger = new TimingLogger(CodeEditText.LOG_TAG, "highlight");
    private final TextWatcher mTextWatcher;

    public JavaScriptHighlighter(Theme theme, CodeEditText codeEditText) {
        mExecutorService.allowCoreThreadTimeOut(true);
        mTheme = theme;
        mCodeEditText = codeEditText;
        mTextWatcher = new SimpleTextWatcher(this);
        codeEditText.addTextChangedListener(mTextWatcher);
    }

    @Override
    public void afterTextChanged(Editable s) {
        updateTokens(s.toString());
    }


    public void setTheme(Theme theme) {
        mTheme = theme;
    }

    public void updateTokens(String sourceString) {
        if (mTheme == null) {
            return;
        }
        final int id = mRunningHighlighterId.incrementAndGet();
        if (mExecutorService.isShutdown() || mExecutorService.isTerminated() || mExecutorService.isTerminating()) {
            return;
        }
        mExecutorService.execute(() -> {
            try {
                mLogger.reset();
                updateTokens(sourceString, id);
                mLogger.addSplit("parse tokens");
                mLogger.dumpToLog();
            } catch (IOException neverHappen) {
                throw new UncheckedIOException(neverHappen);
            }

        });
    }

    private void updateTokens(String sourceString, int id) throws IOException {
        TokenStream ts = new TokenStream(null, sourceString, 0);
        HighlightTokens highlightTokens = new HighlightTokens(sourceString, id);
        int token;
        int color = mTheme.getColorForToken(Token.NAME);
        while ((token = ts.getToken()) != Token.EOF) {
            color = mTheme.getColorForToken(token);
            highlightTokens.addToken(ts.getTokenBeg(), ts.getTokenEnd(), color);
        }
        if (highlightTokens.getCharCount() < sourceString.length()) {
            highlightTokens.addToken(highlightTokens.getCharCount(), sourceString.length(), color);
        }
        mCodeEditText.updateHighlightTokens(highlightTokens);
    }

    public void shutdown() {
        mCodeEditText.removeTextChangedListener(mTextWatcher);
        mExecutorService.shutdownNow();
    }

}
