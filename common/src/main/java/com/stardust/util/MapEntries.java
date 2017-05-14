package com.stardust.util;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Stardust on 2017/1/26.
 */

public class MapEntries<K, V> {

    private Map<K, V> mMap;

    public MapEntries() {
        this(new HashMap<K, V>());
    }

    public MapEntries(Map<K, V> map) {
        mMap = map;
    }

    public MapEntries<K, V> entry(K key, V value) {
        mMap.put(key, value);
        return this;
    }

    public Map<K, V> map() {
        return mMap;
    }

    public Map<K, V> putIn(Map<K, V> map) {
        map.putAll(mMap);
        return map;
    }


}
