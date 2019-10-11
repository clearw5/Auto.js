package org.autojs.autojs.ui.edit.theme;

import android.graphics.Color;
import android.util.SparseIntArray;

import org.autojs.autojs.model.editor.EditorTheme;
import org.autojs.autojs.model.editor.TokenColor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by Stardust on 2018/2/16.
 */

public class Theme {


    private int mBackgroundColor = Color.WHITE;
    private int mForegroundColor = Color.BLACK;
    private int mLineNumberColor = Color.GRAY;
    private SparseIntArray mTokenColors = new SparseIntArray();
    private int mImeBarBackgroundColor = 0xDDFFFFFF;
    private int mImeBarForegroundColor = Color.WHITE;
    private EditorTheme mEditorTheme;
    private int mLineHighlightBackground;
    private int mBreakpointColor;
    private int mDebuggingLineBackground;

    public Theme(EditorTheme theme) {
        mEditorTheme = theme;
        mBackgroundColor = parseColor(theme.getEditorColors().getEditorBackground(), mBackgroundColor);
        mForegroundColor = parseColor(theme.getEditorColors().getEditorForeground(), mForegroundColor);
        mLineNumberColor = parseColor(theme.getEditorColors().getLineNumberForeground(), mLineNumberColor);
        mImeBarBackgroundColor = parseColor(theme.getEditorColors().getImeBackgroundColor(), mImeBarBackgroundColor);
        mImeBarForegroundColor = parseColor(theme.getEditorColors().getImeForegroundColor(), mImeBarForegroundColor);
        mLineHighlightBackground = parseColor(theme.getEditorColors().getLineHighlightBackground(), mLineHighlightBackground);
        mDebuggingLineBackground = parseColor(theme.getEditorColors().getDebuggingLineBackground(), mDebuggingLineBackground);
        mBreakpointColor = parseColor(theme.getEditorColors().getBreakpointForeground(), mBackgroundColor);

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

    private int parseColor(String color, int defaultValue) {
        if (color == null)
            return defaultValue;
        try {
            return Color.parseColor(color);
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

    public String getName() {
        return mEditorTheme.getName();
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
        return fromAssetsJson(context, "editor/theme/light_plus.json");
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

    public int getLineHighlightBackgroundColor() {
        return mLineHighlightBackground;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Theme theme = (Theme) o;

        return mEditorTheme.getName() != null ? mEditorTheme.getName().equals(theme.mEditorTheme.getName()) : theme.mEditorTheme.getName() == null;
    }

    public String toString() {
        return getName();
    }


    public int getBreakpointColor() {
        return mBreakpointColor;
    }

    public int getDebuggingLineBackgroundColor() {
        return mDebuggingLineBackground;
    }
}
