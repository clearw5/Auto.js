package org.autojs.autojs.ui.edit.editor;

import android.content.Context;
import android.graphics.Canvas;
import com.google.android.material.snackbar.Snackbar;
import android.text.Layout;
import android.util.AttributeSet;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.autojs.script.JsBeautifier;

import org.autojs.autojs.R;
import org.autojs.autojs.ui.edit.theme.Theme;

import com.stardust.util.ClipboardUtil;
import com.stardust.util.TextUtils;

import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import io.reactivex.Observable;

/**
 * Copyright 2018 WHO<980008027@qq.com>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Modified by project: https://github.com/980008027/JsDroidEditor
 */
public class CodeEditor extends HVScrollView {

    public static class CheckedPatternSyntaxException extends Exception {
        public CheckedPatternSyntaxException(PatternSyntaxException cause) {
            super(cause);
        }
    }

    public interface CursorChangeCallback {

        void onCursorChange(String line, int ch);

    }


    private CodeEditText mCodeEditText;
    private TextViewUndoRedo mTextViewRedoUndo;
    private JavaScriptHighlighter mJavaScriptHighlighter;
    private Theme mTheme;
    private JsBeautifier mJsBeautifier;
    private MaterialDialog mProcessDialog;

    private CharSequence mReplacement = "";
    private String mKeywords;
    private Matcher mMatcher;
    private int mFoundIndex = -1;

    public CodeEditor(Context context) {
        super(context);
        init();
    }

    public CodeEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        //setFillViewport(true);
        inflate(getContext(), R.layout.code_editor, this);
        mCodeEditText = findViewById(R.id.code_edit_text);
        mCodeEditText.addTextChangedListener(new AutoIndent(mCodeEditText));
        mTextViewRedoUndo = new TextViewUndoRedo(mCodeEditText);
        mJavaScriptHighlighter = new JavaScriptHighlighter(mTheme, mCodeEditText);
        mJsBeautifier = new JsBeautifier(this, "js/js-beautify");

    }

    public Observable<Integer> getLineCount() {
        return Observable.just(mCodeEditText.getLayout().getLineCount());
    }

    public void copyLine() {
        int line = LayoutHelper.getLineOfChar(mCodeEditText.getLayout(), mCodeEditText.getSelectionStart());
        if (line < 0 || line >= mCodeEditText.getLayout().getLineCount())
            return;
        CharSequence lineText = mCodeEditText.getText().subSequence(mCodeEditText.getLayout().getLineStart(line),
                mCodeEditText.getLayout().getLineEnd(line));
        ClipboardUtil.setClip(getContext(), lineText);
        Snackbar.make(this, R.string.text_already_copy_to_clip, Snackbar.LENGTH_SHORT).show();
    }


    public void deleteLine() {
        int line = LayoutHelper.getLineOfChar(mCodeEditText.getLayout(), mCodeEditText.getSelectionStart());
        if (line < 0 || line >= mCodeEditText.getLayout().getLineCount())
            return;
        mCodeEditText.getText().replace(mCodeEditText.getLayout().getLineStart(line),
                mCodeEditText.getLayout().getLineEnd(line), "");
    }

    public void jumpToStart() {
        mCodeEditText.setSelection(0);
    }

    public void jumpToEnd() {
        mCodeEditText.setSelection(mCodeEditText.getText().length());
    }

    public void jumpToLineStart() {
        int line = LayoutHelper.getLineOfChar(mCodeEditText.getLayout(), mCodeEditText.getSelectionStart());
        if (line < 0 || line >= mCodeEditText.getLayout().getLineCount())
            return;
        mCodeEditText.setSelection(mCodeEditText.getLayout().getLineStart(line));
    }

    public void jumpToLineEnd() {
        int line = LayoutHelper.getLineOfChar(mCodeEditText.getLayout(), mCodeEditText.getSelectionStart());
        if (line < 0 || line >= mCodeEditText.getLayout().getLineCount())
            return;
        mCodeEditText.setSelection(mCodeEditText.getLayout().getLineEnd(line) - 1);

    }

    public void setTheme(Theme theme) {
        mTheme = theme;
        setBackgroundColor(mTheme.getBackgroundColor());
        mJavaScriptHighlighter.setTheme(theme);
        mJavaScriptHighlighter.updateTokens(mCodeEditText.getText().toString());
        mCodeEditText.setTheme(mTheme);
        invalidate();
    }

    public boolean isTextChanged() {
        return mTextViewRedoUndo.isTextChanged();
    }

    public boolean canUndo() {
        return mTextViewRedoUndo.canUndo();
    }

    public boolean canRedo() {
        return mTextViewRedoUndo.canRedo();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        mCodeEditText.postInvalidate();
    }

    public CodeEditText getCodeEditText() {
        return mCodeEditText;
    }

    public void setInitialText(String text) {
        mCodeEditText.setText(text);
        mTextViewRedoUndo.setDefaultText(text);
    }

    public void jumpTo(int line, int col) {
        Layout layout = mCodeEditText.getLayout();
        if (line < 0 || (layout != null && line >= layout.getLineCount())) {
            return;
        }
        mCodeEditText.setSelection(mCodeEditText.getLayout().getLineStart(line) + col);
    }

    public void setReadOnly(boolean readOnly) {
        mCodeEditText.setEnabled(!readOnly);
    }

    public void setRedoUndoEnabled(boolean enabled) {
        mTextViewRedoUndo.setEnabled(enabled);
    }

    public void setProgress(boolean progress) {
        if (progress) {
            if (mProcessDialog != null) {
                mProcessDialog.dismiss();
            }
            mProcessDialog = new MaterialDialog.Builder(getContext())
                    .content(R.string.text_processing)
                    .progress(true, 0)
                    .cancelable(false)
                    .show();
        } else {
            if (mProcessDialog != null) {
                mProcessDialog.dismiss();
                mProcessDialog = null;
            }
        }

    }

    public void setText(String text) {
        mCodeEditText.setText(text);
    }

    public void addCursorChangeCallback(CursorChangeCallback callback) {
        mCodeEditText.addCursorChangeCallback(callback);
    }

    public boolean removeCursorChangeCallback(CursorChangeCallback callback) {
        return mCodeEditText.removeCursorChangeCallback(callback);
    }


    public void undo() {
        mTextViewRedoUndo.undo();
    }

    public void redo() {
        mTextViewRedoUndo.redo();
    }

    public void find(String keywords, boolean usingRegex) throws CheckedPatternSyntaxException {
        if (usingRegex) {
            try {
                mMatcher = Pattern.compile(keywords).matcher(mCodeEditText.getText());
            }catch (PatternSyntaxException e){
                throw new CheckedPatternSyntaxException(e);
            }
            mKeywords = null;
        } else {
            mKeywords = keywords;
            mMatcher = null;
        }
        findNext();
    }

    public void replace(String keywords, String replacement, boolean usingRegex) throws CheckedPatternSyntaxException {
        mReplacement = replacement == null ? "" : replacement;
        find(keywords, usingRegex);
    }

    public void replaceAll(String keywords, String replacement, boolean usingRegex) throws CheckedPatternSyntaxException {
        if (!usingRegex) {
            keywords = Pattern.quote(keywords);
        }
        String text = mCodeEditText.getText().toString();
        try {
            text = text.replaceAll(keywords, replacement);
        }catch (PatternSyntaxException e){
            throw new CheckedPatternSyntaxException(e);
        }
        setText(text);
    }

    public void findNext() {
        int foundIndex;
        if (mMatcher == null) {
            if (mKeywords == null)
                return;
            foundIndex = TextUtils.indexOf(mCodeEditText.getText(), mKeywords, mFoundIndex + 1);
            if (foundIndex >= 0)
                mCodeEditText.setSelection(foundIndex, foundIndex + mKeywords.length());
        } else if (mMatcher.find(mFoundIndex + 1)) {
            foundIndex = mMatcher.start();
            mCodeEditText.setSelection(foundIndex, foundIndex + mMatcher.group().length());
        } else {
            foundIndex = -1;
        }
        if (foundIndex < 0 && mFoundIndex >= 0) {
            mFoundIndex = -1;
            findNext();
        } else {
            mFoundIndex = foundIndex;
        }
    }

    public void findPrev() {
        if (mMatcher != null) {
            Toast.makeText(getContext(), R.string.error_regex_find_prev, Toast.LENGTH_SHORT).show();
            return;
        }
        int len = mCodeEditText.getText().length();
        if (mFoundIndex <= 0) {
            mFoundIndex = len;
        }
        int index = mCodeEditText.getText().toString().lastIndexOf(mKeywords, mFoundIndex - 1);
        if (index < 0) {
            if (mFoundIndex != len) {
                mFoundIndex = len;
                findPrev();
            }
            return;
        }
        mFoundIndex = index;
        mCodeEditText.setSelection(index, index + mKeywords.length());
    }

    public void replaceSelection() {
        mCodeEditText.getText().replace(mCodeEditText.getSelectionStart(), mCodeEditText.getSelectionEnd(), mReplacement);
    }

    public void beautifyCode() {
        setProgress(true);
        mJsBeautifier.beautify(mCodeEditText.getText().toString(), new JsBeautifier.Callback() {
            @Override
            public void onSuccess(String beautifiedCode) {
                setProgress(false);
                mCodeEditText.setText(beautifiedCode);
            }

            @Override
            public void onException(Exception e) {
                setProgress(false);
                e.printStackTrace();
            }
        });
    }


    public void insert(String insertText) {
        int selection = Math.max(mCodeEditText.getSelectionStart(), 0);
        mCodeEditText.getText().insert(selection, insertText);
    }

    public void insert(int line, String insertText) {
        int selection = mCodeEditText.getLayout().getLineStart(line);
        mCodeEditText.getText().insert(selection, insertText);
    }

    public void moveCursor(int dCh) {
        mCodeEditText.setSelection(mCodeEditText.getSelectionStart() + dCh);
    }

    public String getText() {
        return mCodeEditText.getText().toString();
    }

    public Observable<String> getSelection() {
        int s = mCodeEditText.getSelectionStart();
        int e = mCodeEditText.getSelectionEnd();
        if (s == e) {
            return Observable.just("");
        }
        return Observable.just(mCodeEditText.getText().toString().substring(s, e));
    }


    public void markTextAsSaved() {
        mTextViewRedoUndo.markTextAsUnchanged();
    }

    public LinkedHashMap<Integer, Breakpoint> getBreakpoints() {
        return mCodeEditText.getBreakpoints();
    }

    public void setDebuggingLine(int line) {
        mCodeEditText.setDebuggingLine(line);
    }

    public void setBreakpointChangeListener(BreakpointChangeListener listener) {
        mCodeEditText.setBreakpointChangeListener(listener);
    }

    public void addOrRemoveBreakpoint(int line) {
        if (!mCodeEditText.removeBreakpoint(line)) {
            mCodeEditText.addBreakpoint(line);
        }
    }

    public void addOrRemoveBreakpointAtCurrentLine() {
        int line = LayoutHelper.getLineOfChar(mCodeEditText.getLayout(), mCodeEditText.getSelectionStart());
        if (line < 0 || line >= mCodeEditText.getLayout().getLineCount())
            return;
        addOrRemoveBreakpoint(line);
    }

    public void removeAllBreakpoints() {
        mCodeEditText.removeAllBreakpoints();
    }

    public void destroy(){
        mJavaScriptHighlighter.shutdown();
        mJsBeautifier.shutdown();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int codeWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int codeHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        if (mCodeEditText.getMinWidth() != codeWidth || mCodeEditText.getMinWidth() != codeWidth) {
            mCodeEditText.setMinWidth(codeWidth);
            mCodeEditText.setMinHeight(codeHeight);
            invalidate();
        }
        super.onDraw(canvas);
    }

    public static class Breakpoint {

        public int line;
        public boolean enabled = true;

        public Breakpoint(int line) {
            this.line = line;
        }
    }

    public interface BreakpointChangeListener {
        void onBreakpointChange(int line, boolean enabled);

        void onAllBreakpointRemoved(int count);
    }
}
