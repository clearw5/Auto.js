package org.autojs.autojs.model.editor;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

/**
 * Created by Stardust on 2018/2/21.
 */

public class TokenColor {

    @SerializedName("name")
    private String mName;

    @SerializedName("scope")
    private List<String> mScope = Collections.emptyList();

    @SerializedName("settings")
    private TokenColorSettings mSettings;

    public TokenColor() {
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public List<String> getScope() {
        return mScope;
    }

    public void setScope(List<String> scope) {
        mScope = scope;
    }

    public TokenColorSettings getSettings() {
        return mSettings;
    }

    public void setSettings(TokenColorSettings settings) {
        mSettings = settings;
    }

    @Override
    public String toString() {
        return "TokenColor{" +
                "scope=" + mScope +
                ", settings=" + mSettings.getForeground() +
                '}';
    }
}
