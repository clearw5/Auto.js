package com.stardust.scriptdroid.ui.edit.completion;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.stardust.scriptdroid.Pref;
import com.jecelyin.editor.v2.core.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Stardust on 2017/2/17.
 */

public class CodeCompletion implements TextWatcher {


    public static class CodeCompletionItem implements Comparable<CodeCompletionItem> {
        String mDisplayText, mAppendText;

        CodeCompletionItem(String displayText, String appendText) {
            mDisplayText = displayText;
            mAppendText = appendText;
        }

        CodeCompletionItem(String text) {
            this(text, text);
        }

        public String getAppendText() {
            return mAppendText;
        }

        public String getDisplayText() {
            return mDisplayText;
        }

        @Override
        public int compareTo(CodeCompletionItem o) {
            return mDisplayText.compareTo(o.mDisplayText);
        }
    }

    interface OnCodeCompletionChangeListener {
        void OnCodeCompletionChange(Collection<CodeCompletionItem>... list);
    }

    private static final String TAG = "CodeCompletion";

    private OnCodeCompletionChangeListener mOnCodeCompletionChangeListener;
    private TextView mEditText;

    public CodeCompletion(OnCodeCompletionChangeListener listener) {
        mOnCodeCompletionChangeListener = listener;
    }

    public void setEditText(TextView editText) {
        mEditText = editText;
        mEditText.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        int position = mEditText.getSelectionStart();
        String str = parseWordBefore(s, position);
        if (!TextUtils.isEmpty(str))
            searchCodeCompletion(str);
        else
            mOnCodeCompletionChangeListener.OnCodeCompletionChange(DEFAULT_CODE_COMPLETION_LIST);
    }

    private String parseWordBefore(Editable s, int position) {
        int i;
        for (i = position - 1; i >= 0; i--) {
            if (position - i > KEY_WORD_LENGTH_MAX) {
                return null;
            }
            if (!Character.isLetter(s.charAt(i))) {
                break;
            }
        }
        if (i < position - 1) {
            return s.subSequence(i + 1, position).toString();
        }
        return null;
    }


    private static final String[] KEYWORDS = {"arguments", "break", "case", "catch", "class", "continue", "default", "do", "else", "eval", "export", "false", "for", "function", "if", "import", "in", "int", "new", "null", "package", "return", "switch", "this", "throw", "throws", "true", "try", "typeof", "var", "volatile", "while", "with", "Array", "Date", "hasOwnProperty", "Infinity", "isFinite", "isNaN", "isPrototypeOf", "length", "Math", "NaN", "name", "Number", "Object", "prototype", "String", "toString", "undefined", "valueOf"};
    private static final int KEY_WORD_LENGTH_MAX = 15;

    public void setFunctions(String[] functions) {
        mFunctions = functions;
    }

    private String[] mFunctions = {"toast", "launchPackage", "launch", "launchApp", "click", "longClick", "scrollUp", "scrollDown", "select", "focus", "paste", "input", "sleep", "isStopped", "notStopped", "println", "err", "openConsole", "clearConsole", "shell", "getTexts", "getPackageName", "getActivityName", "setClip", "addAccessibilityDelegate"};

    private boolean searchCodeCompletion(String str) {
        Collection<CodeCompletionItem> c = searchWordCompletion(str);
        c.addAll(searchCodeCompletion(str, mFunctions));
        c.addAll(searchKeyWordCompletion(str));
        if (c.size() > 0) {
            mOnCodeCompletionChangeListener.OnCodeCompletionChange(c, DEFAULT_CODE_COMPLETION_LIST);
            return true;
        } else {
            mOnCodeCompletionChangeListener.OnCodeCompletionChange(DEFAULT_CODE_COMPLETION_LIST);
            return false;
        }
    }

    private Collection<CodeCompletionItem> searchWordCompletion(String str) {
        if (mEditText.getEditableText().length() < Pref.getMaxTextLengthForCodeCompletion()) {
            return searchCodeCompletion(str, splitWord(mEditText.getEditableText().toString()));
        }
        return new TreeSet<>();
    }

    private Collection<CodeCompletionItem> searchCodeCompletion(String str, String[] words) {
        Set<CodeCompletionItem> set = new TreeSet<>();
        for (String word : words) {
            // TODO: 2017/2/18 优化 字典树
            if (word.startsWith(str) && str.length() < word.length()) {
                set.add(new CodeCompletionItem(word, word.substring(str.length())));
            }
        }
        return set;
    }

    private String[] splitWord(String s) {
        // TODO: 2017/2/18 优化。利用上次结果
        return s.split("[\\W]");
    }

    private Collection<CodeCompletionItem> searchKeyWordCompletion(String str) {
        return searchCodeCompletion(str, KEYWORDS);
    }


    private static final String[] DEFAULT_CODE_COMPLETIONS = new String[]{"=", "(", ")", ";", "{", "}", "\"", "!", "[", "]", "\\", ".", ", "};
    private static final List<CodeCompletionItem> DEFAULT_CODE_COMPLETION_LIST = new ArrayList<>();

    static {
        for (String str : DEFAULT_CODE_COMPLETIONS) {
            DEFAULT_CODE_COMPLETION_LIST.add(new CodeCompletionItem(str));
        }
    }
}
