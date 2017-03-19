package com.stardust.scriptdroid;

import com.stardust.scriptdroid.droid.script.JavaScriptEngine;
import com.stardust.scriptdroid.droid.script.RhinoJavaScriptEngine;
import com.stardust.scriptdroid.record.inputevent.InputEventToJsConverter;

import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    // TODO: 2017/3/3 自定义函数


    @Test
    public void test() {
        Context context = Context.enter();
        context.setOptimizationLevel(-1);
        context.setLanguageVersion(Context.VERSION_1_7);
        context.setInstructionObserverThreshold(10000);
        ImporterTopLevel importerTopLevel = new ImporterTopLevel();
        importerTopLevel.initStandardObjects(context, false);
        context.evaluateString(importerTopLevel, "var a = 1;var b = function(){};", "<init>", 1, null);
        String[] ids = (String[]) importerTopLevel.getIds();
        System.out.println(ids[0].getClass());
        System.out.println(Arrays.toString(ids));
        for(Object id : ids){
            System.out.println(importerTopLevel.get(id));
        }
    }
}