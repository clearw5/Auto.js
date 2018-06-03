package org.autojs.autojs.pluginclient;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stardust on 2017/5/11.
 */

public class Router implements Handler {

    private Map<String, Handler> mHandlerMap = new HashMap<>();
    private String mKey;

    public Router(String key) {
        mKey = key;
    }

    public Router handler(String value, Handler handler) {
        mHandlerMap.put(value, handler);
        return this;
    }

    @Override
    public boolean handle(JsonObject data) {
        Handler handler = mHandlerMap.get(data.get(mKey).getAsString());
        return handler != null && handler.handle(data);
    }

}
