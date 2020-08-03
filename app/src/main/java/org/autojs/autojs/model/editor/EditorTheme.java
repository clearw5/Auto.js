
package org.autojs.autojs.model.editor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.Reader;
import java.util.Collections;
import java.util.List;

public class EditorTheme {

    private static Gson sGson;

    static {
        sGson = new GsonBuilder()
                .registerTypeAdapter(TokenColor.class, new TokenColorDeserializer())
                .create();
    }

    @SerializedName("colors")
    private EditorColors mEditorColors;

    @SerializedName("name")
    private String mName;

    @SerializedName("$schema")
    private String mSchema;

    @SerializedName("tokenColors")
    private List<TokenColor> mTokenColors = Collections.emptyList();

    public static EditorTheme fromJson(String json) {
        return sGson.fromJson(json, EditorTheme.class);
    }

    public static EditorTheme fromJson(Reader json) {
        return sGson.fromJson(json, EditorTheme.class);
    }

    public EditorColors getEditorColors() {
        return mEditorColors;
    }

    public void setEditorColors(EditorColors editorColors) {
        mEditorColors = editorColors;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getSchema() {
        return mSchema;
    }

    public void setSchema(String schema) {
        mSchema = schema;
    }

    public List<TokenColor> getTokenColors() {
        return mTokenColors;
    }

    public void setTokenColors(List<TokenColor> tokenColors) {
        mTokenColors = tokenColors;
    }

}
