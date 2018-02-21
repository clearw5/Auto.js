package com.stardust.scriptdroid.ui.edit.editor;


import android.text.Editable;
import android.util.Log;

import com.stardust.autojs.rhino.TokenStream;
import com.stardust.pio.UncheckedIOException;
import com.stardust.scriptdroid.ui.edit.theme.Theme;
import com.stardust.scriptdroid.ui.widget.SimpleTextWatcher;

import org.mozilla.javascript.Token;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class JavaScriptHighlighter implements SimpleTextWatcher.AfterTextChangedListener {


    public static class HighlightTokens {

        private int[] mColors;
        private String mText;
        private int mCount;

        public HighlightTokens(String text) {
            mColors = new int[text.length()];
            mText = text;
        }


        public int getCharColor(int i) {
            return mColors[i];
        }


        public void addToken(int tokenStart, int tokenEnd, int color) {
            for (int i = tokenStart; i < tokenEnd; i++) {
                mColors[i] = color;
            }
            mCount = tokenEnd;
        }

        @Override
        public String toString() {
            return "HighlightTokens{" +
                    "mColors=" + Arrays.toString(mColors) +
                    '}';
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
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    private AtomicInteger mRunningHighlighterId = new AtomicInteger();

    public JavaScriptHighlighter(Theme theme, CodeEditText codeEditText) {
        mTheme = theme;
        mCodeEditText = codeEditText;
        codeEditText.addTextChangedListener(new SimpleTextWatcher(this));
    }

    @Override
    public void afterTextChanged(Editable s) {
        updateTokens(s.toString());
    }


    public void setTheme(Theme theme) {
        mTheme = theme;
    }

    private void updateTokens(String sourceString) {
        final int id = mRunningHighlighterId.incrementAndGet();
        mExecutorService.execute(() -> {
            try {
                updateTokens(sourceString, id);
            } catch (IOException neverHappen) {
                throw new UncheckedIOException(neverHappen);
            }

        });
    }

    private void updateTokens(String sourceString, int id) throws IOException {
        TokenStream ts = new TokenStream(null, sourceString, 0);
        HighlightTokens highlightTokens = new HighlightTokens(sourceString);
        int token;
        while ((token = ts.getToken()) != Token.EOF) {
            if (mRunningHighlighterId.get() != id)
                return;
            int color = mTheme.getColorForToken(token);
            highlightTokens.addToken(ts.getTokenBeg(), ts.getTokenEnd(), color);
        }
        if (highlightTokens.getCharCount() < sourceString.length()) {
            highlightTokens.addToken(highlightTokens.getCharCount(), sourceString.length(), mTheme.getColorForToken(Token.NAME));
        }
        mCodeEditText.updateHighlightTokens(highlightTokens);
    }


}
