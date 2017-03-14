package com.stardust.scriptdroid;

import com.stardust.scriptdroid.record.inputevent.InputEventToJsConverter;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    // TODO: 2017/3/3 引擎兼容性 
    // TODO: 2017/3/3 更多的示例 
    // TODO: 2017/3/3 自定义函数 
    // TODO: 2017/3/3  


    @Test
    public void test() {
        int i = 0xffffffff;
        long l = i & 0xffffffffL;
        System.out.println(l);
    }
}