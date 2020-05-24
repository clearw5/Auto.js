package com.stardust.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stardust on 2017/1/26.
 */

public class MapBuilder<K, V> {

    private Map<K, V> mMap;

    public MapBuilder() {
        this(new HashMap<K, V>());
    }

    public MapBuilder(Map<K, V> map) {
        mMap = map;
    }

    public MapBuilder<K, V> put(K key, V value) {
        mMap.put(key, value);
        return this;
    }

    public Map<K, V> build() {
        return mMap;
    }

    public Map<K, V> putIn(Map<K, V> map) {
        map.putAll(mMap);
        return map;
    }


}
