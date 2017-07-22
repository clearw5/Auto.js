package com.stardust.scriptdroid.ui.edit.completion;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.stardust.scriptdroid.Pref;
import com.jecelyin.editor.v2.core.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        public int compareTo(@NonNull CodeCompletionItem o) {
            return mDisplayText.compareTo(o.mDisplayText);
        }
    }

    interface OnCodeCompletionChangeListener {
        void OnCodeCompletionChange(@NonNull Collection<CodeCompletionItem> list1, @NonNull Collection<CodeCompletionItem> list2);
    }

    private static final String TAG = "CodeCompletion";

    private OnCodeCompletionChangeListener mOnCodeCompletionChangeListener;
    private TextView mEditText;
    private static final List<String> KEYWORDS = Arrays.asList("arguments", "break", "case", "catch", "class", "continue", "default", "do", "else", "eval", "export", "false", "for", "function", "if", "import", "in", "int", "new", "null", "package", "return", "switch", "this", "throw", "throws", "true", "try", "typeof", "var", "volatile", "while", "with", "Array", "Date", "hasOwnProperty", "Infinity", "isFinite", "isNaN", "isPrototypeOf", "length", "Math", "NaN", "name", "Number", "Object", "prototype", "String", "toString", "undefined", "valueOf");
    private static final int KEY_WORD_LENGTH_MAX = 15;


    private List<String> mGlobal = Collections.emptyList();
    private Map<String, List<String>> mVariableProperties = new HashMap<>();


    public CodeCompletion(OnCodeCompletionChangeListener listener) {
        mOnCodeCompletionChangeListener = listener;
    }

    public void setEditText(TextView editText) {
        mEditText = editText;
        mEditText.addTextChangedListener(this);
    }

    public void setGlobal(List<String> functions) {
        mGlobal = functions;
    }

    public void setVariableProperties(Map<String, List<String>> variableProperties) {
        mVariableProperties = variableProperties;
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
        String[] str = parseWordBefore(s, position);
        if (str != null) {
            if (str[1] != null) {
                searchCodeCompletionForVariable(str[0], str[1]);
            } else {
                searchCodeCompletionForGlobal(str[0]);
            }
        } else {
            mOnCodeCompletionChangeListener.OnCodeCompletionChange(DEFAULT_CODE_COMPLETION_LIST, Collections.<CodeCompletionItem>emptyList());
        }
    }

    private String[] parseWordBefore(Editable s, int position) {
        int i;
        for (i = position - 1; i >= 0; i--) {
            if (position - i > KEY_WORD_LENGTH_MAX) {
                return null;
            }
            char c = s.charAt(i);
            if (c == '.') {
                return new String[]{parseWordBeforeDot(s, i), s.subSequence(i + 1, position).toString()};
            }
            if (!Character.isLetter(s.charAt(i))) {
                if (i < position - 1) {
                    return new String[]{s.subSequence(i + 1, position).toString(), null};
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    private String parseWordBeforeDot(Editable s, int position) {
        int i;
        for (i = position - 1; i >= 0; i--) {
            if (position - i > KEY_WORD_LENGTH_MAX) {
                return null;
            }
            if (!Character.isLetter(s.charAt(i))) {
                if (i < position - 1) {
                    return s.subSequence(i + 1, position).toString();
                } else {
                    return null;
                }
            }
        }
        return null;
    }


    private boolean searchCodeCompletionForGlobal(String str) {
        Collection<CodeCompletionItem> c = searchWordCompletion(str);
        c.addAll(searchCodeCompletion(str, mGlobal));
        c.addAll(searchKeyWordCompletion(str));
        if (c.size() > 0) {
            mOnCodeCompletionChangeListener.OnCodeCompletionChange(c, DEFAULT_CODE_COMPLETION_LIST);
            return true;
        } else {
            mOnCodeCompletionChangeListener.OnCodeCompletionChange(DEFAULT_CODE_COMPLETION_LIST, Collections.<CodeCompletionItem>emptyList());
            return false;
        }
    }

    private boolean searchCodeCompletionForVariable(String variable, String str) {
        List<String> properties = getPropertiesForVariable(variable);
        Collection<CodeCompletionItem> c = searchCodeCompletion(str, properties);
        if (c.isEmpty()) {
            mOnCodeCompletionChangeListener.OnCodeCompletionChange(DEFAULT_CODE_COMPLETION_LIST, Collections.<CodeCompletionItem>emptyList());
            return false;
        } else {
            mOnCodeCompletionChangeListener.OnCodeCompletionChange(c, Collections.<CodeCompletionItem>emptyList());
            return true;
        }
    }

    private List<String> getPropertiesForVariable(String str) {
        List<String> properties = mVariableProperties.get(str);
        if (properties == null)
            return Collections.emptyList();
        return properties;
    }


    private Collection<CodeCompletionItem> searchWordCompletion(String str) {
        if (mEditText.getEditableText().length() < Pref.getMaxTextLengthForCodeCompletion()) {
            return searchCodeCompletion(str, Arrays.asList(splitWord(mEditText.getEditableText().toString())));
        }
        return new TreeSet<>();
    }

    private Collection<CodeCompletionItem> searchCodeCompletion(String str, Iterable<String> words) {
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

    private static final String[] DEFAULT_CODE_COMPLETIONS = new String[]{"=", "(", ")", ";", "{", "}", "\"", "!", "[", "]", "\\", ">", "<", ".", ", "};
    private static final List<CodeCompletionItem> DEFAULT_CODE_COMPLETION_LIST = new ArrayList<>();

    static {
        for (String str : DEFAULT_CODE_COMPLETIONS) {
            DEFAULT_CODE_COMPLETION_LIST.add(new CodeCompletionItem(str));
        }
    }
}
