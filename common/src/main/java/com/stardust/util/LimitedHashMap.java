package com.stardust.util;

import java.util.LinkedHashMap;

/**
 * Created by Stardust on 2017/3/31.
 */

public class LimitedHashMap<K, V> extends LinkedHashMap<K, V> {

    private int mMaxSize;

    public LimitedHashMap(int maxSize) {
        super(4, 0.75f, true);
        mMaxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Entry<K, V> eldest) {
        return size() > mMaxSize;
    }


}
