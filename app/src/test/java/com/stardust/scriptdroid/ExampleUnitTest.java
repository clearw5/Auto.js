package com.stardust.scriptdroid;

import android.os.Bundle;

import org.junit.Test;

import java.io.Serializable;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void testSync() throws Exception {
        Bundle bundle = new Bundle();
        bundle.putSerializable("xxx", new XXX() {
            @Override
            void run() {
                System.out.println("xxx");
            }
        });
        ((XXX) bundle.getSerializable("xxx")).run();
    }

    private static abstract class XXX implements Serializable {

        abstract void run();
    }
}