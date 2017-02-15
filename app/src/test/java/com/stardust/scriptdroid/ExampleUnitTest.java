package com.stardust.scriptdroid;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void testSync() throws Exception {
        final Sync sync = new Sync();
        //sync.print(11);
        add(sync, 11, 123);
        //Thread.sleep(1);
        sync.print(11);
        add(sync, 11, 456);
        sync.print(11);
    }

    private void add(final Sync sync, final int key, final int i) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sync.add(key, i);
            }
        }).start();
    }

    private static class Sync {

        private final Map<Integer, List<Integer>> mMap = new TreeMap<>();

        Sync() {
            for (int i = 0; i < 10; i++) {
                List<Integer> list = new ArrayList<>();
                for (int j = 0; j < 10; j++) {
                    list.add(j * i);
                }
                mMap.put(i, list);
            }
        }

        public Sync add(int key, int i) {
            synchronized (mMap) {
                List<Integer> list = ensureList(key);
                list.add(i);
                System.out.println("add:" + key + " " + i);
            }
            return this;
        }

        private List<Integer> ensureList(int key) {
            List<Integer> list = mMap.get(key);
            if (list == null) {
                list = new ArrayList<>();
                mMap.put(key, list);
            }
            return list;
        }

        public void print(int key) {
            synchronized (mMap) {
                List<Integer> list = mMap.get(key);
                System.out.print(key + ":");
                if (list == null) {
                    System.out.print("null");
                } else {
                    for (Integer i : list) {
                        System.out.print(i + " ");
                    }
                }
                System.out.println();
            }
        }
    }
}