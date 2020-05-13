package com.tony.resolver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

public class DefaultGSONResolver implements JSONResolver {

    private String originString;
    private HashMap gsonMap;
    private HashMap newMap;

    @Override
    public String toJSONString(Object obj) {
        return new GsonBuilder().create().toJson(originString);
    }

    @Override
    public String getString(String jsonString, String name) {
        return String.valueOf(new GsonBuilder().create().fromJson(jsonString, HashMap.class).get(name));
    }

    @Override
    public Object getObject(String jsonString, String name) {
        return new GsonBuilder().create().fromJson(jsonString, HashMap.class).get(name);
    }

    @Override
    public JSONResolver setOrigin(String string) {
        originString = string;
        gsonMap = new Gson().fromJson(string, HashMap.class);
        return this;
    }

    @Override
    public String getString(String name) {
        return String.valueOf(gsonMap.get(name));
    }

    @Override
    public Object getObject(String name) {
        return gsonMap.get(name);
    }

    @Override
    public JSONResolver newObject() {
        newMap = new HashMap();
        return this;
    }

    @Override
    public JSONResolver put(String name, Object value) {
        newMap.put(name, value);
        return this;
    }

    @Override
    public String toJSONString() {
        return new Gson().toJson(newMap);
    }
}
