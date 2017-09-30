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
    //// TODO: 2017/7/4 恢复项目 一天 7.6
    // TODO: 2017/7/4  修复 一天 7.7
    // TODO: 2017/7/4 修复Bug，增加一点脚本，发布2.0.13Beta2 半天 7.8
    // TODO: 2017/7/2 底部导航栏 一天 7.9
    //// TODO: 2017/7/4 教程，服务器界面 二至三天 7.12
    // TODO: 2017/7/4  服务器初步开发 三天 7.15
    // TODO: 2017/7/4  细节 三天 7.18
    // TODO: 2017/7/4 写文档 两天 7.20
    // TODO: 2017/7/4 发布 3.0.0 Beta 7.22
    // TODO: 2017/9/30 想象真是美好。


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