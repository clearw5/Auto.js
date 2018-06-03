package org.autojs.autojs.model.editor;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.mozilla.javascript.Token;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Stardust on 2018/2/21.
 */

public class TokenColorDeserializer implements JsonDeserializer<TokenColor> {

    @Override
    public TokenColor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        TokenColor tokenColor = new TokenColor();
        if (object.has("name")) {
            tokenColor.setName(object.get("name").getAsString());
        }
        if (object.has("settings")) {
            tokenColor.setSettings(context.deserialize(object.get("settings"), TokenColorSettings.class));
        }
        if (object.has("scope")) {
            tokenColor.setScope(deserializeAsList(object, "scope"));
        }
        return tokenColor;
    }

    private List<String> deserializeAsList(JsonObject object, String key) {
        JsonElement scope = object.get(key);
        if (scope.isJsonArray()) {
            ArrayList<String> list = new ArrayList<>();
            for (JsonElement e : scope.getAsJsonArray()) {
                list.add(e.getAsString());
            }
            return list;
        } else {
            return Collections.singletonList(scope.getAsString());
        }
    }
}

