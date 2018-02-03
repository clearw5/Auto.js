package com.stardust.autojs.core.ui.inflater.util;

import android.view.InflateException;

import java.util.HashMap;

/**
 * Created by Stardust on 2017/11/3.
 */

public class ValueMapper<V> {

    private HashMap<String, V> mHashMap = new HashMap<>();
    private String mAttrName;

    public ValueMapper(String attrName) {
        mAttrName = attrName;
    }

    public ValueMapper<V> map(String key, V value) {
        mHashMap.put(key, value);
        return this;
    }

    public V get(String key) {
        V v = mHashMap.get(key);
        if (v == null) {
            throw new InflateException(String.format("unknown value for %s: %s", mAttrName, key));
        }
        return v;
    }

    public int split(String str){
        int r = 0;
        for(String s : str.split("\\|")){
            r |= (Integer) get(s);
        }
        return r;
    }

}
