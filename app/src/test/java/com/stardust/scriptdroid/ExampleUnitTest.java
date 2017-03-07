package com.stardust.scriptdroid;

import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stardust.scriptdroid.record.root.InputEventConverter;
import com.stardust.scriptdroid.ui.help.HelpCatalogueActivity;

import org.json.JSONObject;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        System.out.println(new SingleEvent("[  109722.085815] /dev/input/event1: EV_SYN       SYN_REPORT           00000000"));
    }
}