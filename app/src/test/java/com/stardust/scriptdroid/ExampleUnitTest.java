package com.stardust.scriptdroid;

import com.stardust.scriptdroid.droid.script.JavaScriptEngine;
import com.stardust.scriptdroid.droid.script.RhinoJavaScriptEngine;
import com.stardust.scriptdroid.record.inputevent.InputEventToJsConverter;

import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    // TODO: 2017/3/3 自定义函数
    // TODO: 2017/3/19

    Counter counter = new Counter(5);
    private List<Integer> mIntegers = new ArrayList<>();

    @Test
    public void test() {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        executor.execute(new Task(1, 100));
        executor.execute(new Task(2, 1000));
        System.out.println(0);
        executor.execute(new Task(3, 100));
        executor.execute(new Task(4, 100));
        executor.execute(new Task(5, 100));
        System.out.println(100);
        counter.lock();
    }

    private class Task implements Runnable {

        public Task(int i, int delay) {
            this.i = i;
            this.delay = delay;
        }

        private final int i, delay;

        @Override
        public void run() {
            if (delay > 0)
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            System.out.println(i);
            synchronized (mIntegers) {
                mIntegers.add(i);
                for (Integer integer : mIntegers) {
                    System.out.println(i + ":" + integer);
                }
            }
            counter.minus();
        }
    }

    private static class Counter {

        private final Object lock = new Object();

        public Counter(int i) {
            this.i = i;
        }

        private volatile int i;

        void minus() {
            i--;
            if (i == 0) {
                synchronized (lock) {
                    lock.notify();
                }
            }
        }

        void lock() {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}