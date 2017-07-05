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
    //// TODO: 2017/7/4 恢复项目 一天 7.6
    // TODO: 2017/7/4  修复 一天 7.7
    // TODO: 2017/7/4 修复Bug，增加一点脚本，发布2.0.13Beta2 半天 7.8
    // TODO: 2017/7/2 底部导航栏 一天 7.9
    //// TODO: 2017/7/4 教程，服务器界面 二至三天 7.12
    // TODO: 2017/7/4  服务器初步开发 三天 7.15
    // TODO: 2017/7/4  细节 三天 7.18
    // TODO: 2017/7/4 写文档 两天 7.20
    // TODO: 2017/7/4 发布 3.0.0 Beta 7.22


    @Test
    public void test() {
        System.out.println("SOS".hashCode());
    }

    @Test
    public void testAutoReorder() {

    }


}