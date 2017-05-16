package com.stardust.scriptdroid;

import com.jecelyin.common.http.Base64;
import com.stardust.util.LimitedHashMap;

import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.shell.ShellContextFactory;
import org.mozilla.javascript.xml.XMLLib;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Writer;
import java.nio.channels.Pipe;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    // TODO: 2017/3/3 自定义函数 ×
    // TODO: 2017/3/19 exist函数 √
    // TODO: 2017/3/23 tasker插件 √
    // TODO: 2017/3/23 任务管理与控制台 √
    // TODO: 2017/3/23 悬浮窗加入控制台 √
    // TODO: 2017/3/24 文件读写api ---
    // TODO: 2017/3/24 网络读写api
    // TODO: 2017/3/24 常驻后台api ×
    // TODO: 2017/3/24 ui。E4x ---
    // TODO: 2017/3/24  编辑界面文档和自动补全 ×
    // TODO: 2017/3/24 驻留模式 ×
    //// TODO: 2017/3/26 NODEJS ×
    // TODO: 2017/3/31 自定义快捷方式图标


    // FIXME: 2017/3/23 死机重启问题


    @Test
    public void test() {
        Matcher matcher = Pattern.compile("\\S+").matcher("001   华为    6800");
        while (matcher.find()){
            System.out.println(matcher.group());
        }
    }

    @Test
    public void testAutoReorder() {
        Context context = Context.enter();
        Scriptable scriptable = context.initStandardObjects();
        context.setOptimizationLevel(-1);
        Object o = context.evaluateString(scriptable, " (<xml id=\"foo\"></xml>).attributes()[0].name()", "<e4x>", 1, null);
        System.out.println(o);
        Context.exit();
    }


}