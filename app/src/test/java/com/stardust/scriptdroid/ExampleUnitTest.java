package com.stardust.scriptdroid;

import com.stardust.scriptdroid.record.inputevent.InputEventToJsConverter;
import com.stardust.scriptdroid.record.inputevent.InputEventToSendEventConverter;

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
        InputEventToJsConverter converter = new InputEventToJsConverter();
        converter.parseAndAddEventIfFormatCorrect("[  140123.672100] /dev/input/event3: EV_ABS       ABS_MT_POSITION_X    0000005f");
        System.out.println(converter.getCode());
    }
}