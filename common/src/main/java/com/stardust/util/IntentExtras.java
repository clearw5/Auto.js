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

    public static final String EXTRA_ID = "com.stardust.util.IntentExtras.id";

    private static AtomicInteger mMaxId = new AtomicInteger(-1);
    private static SparseArray<Map<String, Object>> extraStore = new SparseArray<>();

    private Map<String, Object> mMap;
    private int mId;

    private IntentExtras() {
        mMap = new HashMap<>();
        mId = mMaxId.incrementAndGet();
        extraStore.put(mId, mMap);
    }


    private IntentExtras(int id, Map<String, Object> map) {
        mId = id;
        mMap = map;
    }


    public static IntentExtras newExtras() {
        return new IntentExtras();
    }

    public static IntentExtras fromIntentAndRelease(Intent intent) {
        int id = intent.getIntExtra(EXTRA_ID, -1);
        if (id < 0) {
            return null;
        }
        return fromIdAndRelease(id);
    }

    public static IntentExtras fromIdAndRelease(int id) {
        Map<String, Object> map = extraStore.get(id);
        if (map == null) {
            return null;
        }
        extraStore.remove(id);
        return new IntentExtras(id, map);
    }

    public static IntentExtras fromId(int id) {
        Map<String, Object> map = extraStore.get(id);
        if (map == null) {
            return null;
        }
        return new IntentExtras(id, map);
    }


    public static IntentExtras fromIntent(Intent intent) {
        int id = intent.getIntExtra(EXTRA_ID, -1);
        if (id < 0) {
            return null;
        }
        return fromId(id);
    }


    public int getId() {
        return mId;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) mMap.get(key);
    }

    public IntentExtras put(String key, Object value) {
        mMap.put(key, value);
        return this;
    }

    public IntentExtras putAll(IntentExtras extras) {
        mMap.putAll(extras.mMap);
        return this;
    }

    public Intent putInIntent(Intent intent) {
        intent.putExtra(EXTRA_ID, mId);
        return intent;
    }

    public void release() {
        extraStore.remove(mId);
        mId = -1;
    }


}
