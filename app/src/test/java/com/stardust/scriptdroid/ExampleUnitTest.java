package com.stardust.scriptdroid;

import com.stardust.scriptdroid.droid.script.JavaScriptEngine;
import com.stardust.scriptdroid.droid.script.RhinoJavaScriptEngine;
import com.stardust.scriptdroid.droid.script.file.ScriptFile;
import com.stardust.scriptdroid.record.inputevent.InputEventToJsConverter;

import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.commonjs.module.RequireBuilder;
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.provider.UrlModuleSourceProvider;

import java.io.File;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    // TODO: 2017/3/19 exist函数
    // TODO: 2017/3/23 tasker插件
    // TODO: 2017/3/23 任务管理与控制台
    // TODO: 2017/3/23 悬浮窗加入控制台
    // TODO: 2017/3/24 文件读写api
    // TODO: 2017/3/24 网络读写api
    // TODO: 2017/3/24 常驻后台api
    // TODO: 2017/3/24 ui。E4x
    // TODO: 2017/3/24  编辑界面文档和自动补全
    // TODO: 2017/3/24 驻留模式


    // FIXME: 2017/3/23 死机重启问题
    // FIXME: 2017/3/23 卡顿问题


    @Test
    public void test() {

        List<URI> paths = Collections.singletonList(new File("D:/js/").toURI());
        Context ctx = Context.enter();
        ctx.setOptimizationLevel(-1);
        ctx.setLanguageVersion(Context.VERSION_1_7);
        ctx.setInstructionObserverThreshold(10000);
        ImporterTopLevel scope = new ImporterTopLevel();
        scope.initStandardObjects(ctx, false);

        new RequireBuilder()
                .setModuleScriptProvider(new SoftCachingModuleScriptProvider(
                        new UrlModuleSourceProvider(paths, null)))
                .setSandboxed(true)
                .createRequire(ctx, scope)
                .install(scope);


        ctx.evaluateString(scope, "require('test.js')()", "<test>", 1, null);

        Context.exit();
    }

    @Test
    public void test2(){
        Context context = Context.enter();
        context.setOptimizationLevel(-1);
        context.setLanguageVersion(Context.VERSION_1_7);
        context.setInstructionObserverThreshold(10000);
        ImporterTopLevel scope = new ImporterTopLevel();
        scope.initStandardObjects(context, false);
        List<URI> paths = Collections.singletonList(new File("D:/js/").toURI());
        new RequireBuilder()
                .setModuleScriptProvider(new SoftCachingModuleScriptProvider(
                        new UrlModuleSourceProvider(paths, null)))
                .setSandboxed(true)
                .createRequire(context, scope)
                .install(scope);
        context.evaluateString(scope, "require('test.js')()", "<test>", 1, null);

        Context.exit();
    }
}