package org.autojs.autojs.ui.edit.editor;


import android.text.Editable;
import android.util.TimingLogger;

import com.stardust.autojs.rhino.TokenStream;
import com.stardust.pio.UncheckedIOException;
import org.autojs.autojs.ui.edit.theme.Theme;
import org.autojs.autojs.ui.widget.SimpleTextWatcher;

import org.mozilla.javascript.Token;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class JavaScriptHighlighter implements SimpleTextWatcher.AfterTextChangedListener {


    public static class HighlightTokens {

        public final int[] colors;
        private String mText;
        private int mCount;

        public HighlightTokens(String text) {
            colors = new int[text.length()];
            mText = text;
        }


        public void addToken(int tokenStart, int tokenEnd, int color) {
            for (int i = tokenStart; i < tokenEnd; i++) {
                colors[i] = color;
            }
            mCount = tokenEnd;
        }

        @Override
        public String toString() {
            return "HighlightTokens{" +
                    "colors=" + Arrays.toString(colors) +
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
    private TimingLogger mLogger = new TimingLogger(CodeEditText.LOG_TAG, "highlight");

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

    public void updateTokens(String sourceString) {
        final int id = mRunningHighlighterId.incrementAndGet();
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
        HighlightTokens highlightTokens = new HighlightTokens(sourceString);
        int token;
        int color = mTheme.getColorForToken(Token.NAME);
        while ((token = ts.getToken()) != Token.EOF) {
            if (mRunningHighlighterId.get() != id)
                return;
            color = mTheme.getColorForToken(token);
            highlightTokens.addToken(ts.getTokenBeg(), ts.getTokenEnd(), color);
        }
        if (highlightTokens.getCharCount() < sourceString.length()) {
            highlightTokens.addToken(highlightTokens.getCharCount(), sourceString.length(), color);
        }
        mCodeEditText.updateHighlightTokens(highlightTokens);
    }


}
