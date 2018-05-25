package org.autojs.autojs.model.editor;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Stardust on 2018/2/21.
 */

public class TokenColorSettings {

    @SerializedName("foreground")
    private String mForeground;

    @SerializedName("fontStyle")
    private String mFontStyle;

    public TokenColorSettings() {
    }

    public String getForeground() {
        return mForeground;
    }

    public void setForeground(String foreground) {
        mForeground = foreground;
    }

    public String getFontStyle() {
        return mFontStyle;
    }

    public void setFontStyle(String fontStyle) {
        mFontStyle = fontStyle;
    }


}
