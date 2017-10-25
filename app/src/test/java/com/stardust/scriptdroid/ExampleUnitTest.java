package com.stardust.scriptdroid;

import org.junit.Test;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void test() {
        Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println(Thread.currentThread());
                return "";
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        System.out.println(Thread.currentThread());
                    }
                })
                .observeOn(Schedulers.newThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        System.out.println(Thread.currentThread());
                    }
                });
    }

    @Test
    public void testAutoReorder() {

    }


}