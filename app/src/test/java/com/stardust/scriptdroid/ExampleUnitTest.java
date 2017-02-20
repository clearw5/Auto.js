package com.stardust.scriptdroid;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    private int i = 0;

    @Test
    public void testSync() throws Exception {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    synchronized (this) {
                        i++;
                    }
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    synchronized (this) {
                        System.out.println(i);
                    }
                }
            }
        }).start();
    }


}