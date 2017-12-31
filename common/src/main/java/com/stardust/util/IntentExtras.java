package com.stardust.util;

import android.content.Intent;
import android.util.SparseArray;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Stardust on 2017/7/11.
 */

public class IntentExtras implements Serializable {

    private static AtomicInteger mMaxId = new AtomicInteger(-1);
    private static final String EXTRA_ID = "com.stardust.util.IntentExtras.id";
    private static SparseArray<Map<String, Object>> extraStore = new SparseArray<>();


    public static IntentExtras newExtras() {
        return new IntentExtras();
    }

    public static IntentExtras fromIntent(Intent intent) {
        int id = intent.getIntExtra(EXTRA_ID, -1);
        if (id < 0) {
            throw new IllegalArgumentException();
        }
        return new IntentExtras(id);
    }

    private Map<String, Object> mMap;
    private int mId;

    private IntentExtras() {
        mMap = new HashMap<>();
        mId = mMaxId.incrementAndGet();
        extraStore.put(mId, mMap);
    }


    private IntentExtras(int id) {
        mId = id;
        mMap = extraStore.get(id);
        if (mMap == null) {
            mMap = new HashMap<>();
        }
        extraStore.remove(id);
    }


    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) mMap.get(key);
    }

    public IntentExtras put(String key, Object value) {
        mMap.put(key, value);
        return this;
    }

    public Intent putInIntent(Intent intent) {
        intent.putExtra(EXTRA_ID, mId);
        return intent;
    }
}
