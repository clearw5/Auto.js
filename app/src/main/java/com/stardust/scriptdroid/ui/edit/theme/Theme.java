package com.stardust.scriptdroid.ui.edit.theme;

import android.graphics.Color;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.google.gson.Gson;
import com.stardust.scriptdroid.model.editor.EditorTheme;
import com.stardust.scriptdroid.model.editor.TokenColor;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Token;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import static org.mozilla.javascript.Token.*;

/**
 * Created by Stardust on 2018/2/16.
 */

public class Theme {


    private int mBackgroundColor;
    private int mForegroundColor;
    private int mLineNumberColor;
    private SparseIntArray mTokenColors = new SparseIntArray();
    private int mImeBarBackgroundColor;
    private int mImeBarForegroundColor;

    public Theme(EditorTheme theme) {
        mBackgroundColor = Color.parseColor(theme.getEditorColors().getEditorBackground());
        mForegroundColor = Color.parseColor(theme.getEditorColors().getEditorForeground());
        mLineNumberColor = Color.parseColor(theme.getEditorColors().getEditorIndentGuideBackground());
        mImeBarBackgroundColor = Color.parseColor(theme.getEditorColors().getImeBackgroundColor());
        mImeBarForegroundColor = Color.parseColor(theme.getEditorColors().getImeForegroundColor());

        for (TokenColor tokenColor : theme.getTokenColors()) {
            String foregroundStr = tokenColor.getSettings().getForeground();
            if (foregroundStr == null)
                continue;
            int foreground = Color.parseColor(foregroundStr);
            for (String scope : tokenColor.getScope()) {
                setTokenColor(scope, foreground);
            }
        }
    }

    private void setTokenColor(String scope, int foreground) {
        for (int token : TokenMapping.getTokensForScope(scope)) {
            mTokenColors.put(token, foreground);
        }
    }

    public int getColorForToken(int token) {
        return mTokenColors.get(token, mForegroundColor);
    }

    public int getLineNumberColor() {
        return mLineNumberColor;
    }

    public static Theme getDefault(android.content.Context context) {
        return fromAssetsJson(context, "editor/theme/dark_plus.json");
    }

    public static Theme fromJson(String json) {
        EditorTheme theme = EditorTheme.fromJson(json);
        if (theme == null)
            return null;
        return new Theme(theme);
    }


    public static Theme fromJson(Reader reader) {
        EditorTheme theme = EditorTheme.fromJson(reader);
        if (theme == null)
            return null;
        return new Theme(theme);
    }

    public static Theme fromAssetsJson(android.content.Context context, String assetsPath) {
        try {
            return fromJson(new InputStreamReader(context.getAssets().open(assetsPath)));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public int getImeBarBackgroundColor() {
        return mImeBarBackgroundColor;
    }

    public void setImeBarBackgroundColor(int imeBarBackgroundColor) {
        mImeBarBackgroundColor = imeBarBackgroundColor;
    }

    public int getImeBarForegroundColor() {
        return mImeBarForegroundColor;
    }

    public void setImeBarForegroundColor(int imeBarForegroundColor) {
        mImeBarForegroundColor = imeBarForegroundColor;
    }
}
