package org.autojs.autojs.model.autocomplete;

/**
 * Created by Stardust on 2018/2/3.
 */

public class CodeCompletion {

    private final String mHint;
    private final String mUrl;
    private final String mInsertText;
    private final int mInsertPos;

    public CodeCompletion(String hint, String url, int insertPos) {
        mHint = hint;
        mUrl = url;
        mInsertPos = insertPos;
        mInsertText = null;
    }

    public CodeCompletion(String hint, String url, String insertText) {
        mHint = hint;
        mUrl = url;
        mInsertText = insertText;
        mInsertPos = -1;
    }

    public String getHint() {
        return mHint;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getInsertText() {
        if (mInsertText != null)
            return mInsertText;
        if (mInsertPos == 0) {
            return mHint;
        }
        return mHint.substring(mInsertPos);
    }
}
