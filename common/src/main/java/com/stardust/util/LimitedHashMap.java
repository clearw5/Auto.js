package com.stardust.util;

import org.junit.Test;

import java.util.LinkedHashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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


    public static class TestSuite {

        @org.testng.annotations.Test
        public void testAutoRemove() {
            LimitedHashMap<String, Integer> hashMap = new LimitedHashMap<>(5);
            hashMap.put("a", 1);
            hashMap.put("b", 2);
            hashMap.put("c", 3);
            hashMap.put("d", 4);
            hashMap.put("e", 5);
            hashMap.put("f", 6);
            assertFalse(hashMap.containsKey("a"));
        }

        @Test
        public void testAutoReorder() {
            LimitedHashMap<String, Integer> hashMap = new LimitedHashMap<>(5);
            hashMap.put("a", 1);
            hashMap.put("b", 2);
            hashMap.put("c", 3);
            hashMap.put("d", 4);
            hashMap.put("e", 5);
            hashMap.get("a");
            hashMap.put("f", 6);
            assertTrue(hashMap.containsKey("a"));
            assertFalse(hashMap.containsKey("b"));
        }

    }
}
