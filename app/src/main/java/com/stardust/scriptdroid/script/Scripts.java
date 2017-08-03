package com.stardust.scriptdroid.script;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.stardust.autojs.execution.ExecutionConfig;
import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.autojs.execution.ScriptExecutionListener;
import com.stardust.autojs.execution.SimpleScriptExecutionListener;
import com.stardust.autojs.runtime.ScriptInterruptedException;
import com.stardust.autojs.script.JavaScriptFileSource;
import com.stardust.autojs.script.ScriptSource;
import com.stardust.autojs.script.StringScriptSource;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.autojs.AutoJs;
import com.stardust.scriptdroid.external.CommonUtils;
import com.stardust.scriptdroid.external.shortcut.Shortcut;
import com.stardust.scriptdroid.external.shortcut.ShortcutActivity;
import com.stardust.scriptdroid.script.sample.Sample;
import com.stardust.scriptdroid.ui.edit.EditActivity;
import com.stardust.util.AssetsCache;

import java.io.File;

/**
 * Created by Stardust on 2017/5/3.
 */

public class Scripts {

    public static final String ACTION_ON_EXECUTION_FINISHED = "Don't leave me alone...";
    public static final String EXTRA_EXCEPTION_MESSAGE = "Say something...Eating...17.5.3";

    private static final ScriptExecutionListener BROADCAST_SENDER_SCRIPT_EXECUTION_LISTENER = new SimpleScriptExecutionListener() {

        @Override
        public void onSuccess(ScriptExecution execution, Object result) {
            App.getApp().sendBroadcast(new Intent(ACTION_ON_EXECUTION_FINISHED));
        }

        @Override
        public void onException(ScriptExecution execution, Exception e) {
            if (ScriptInterruptedException.causedByInterrupted(e)) {
                App.getApp().sendBroadcast(new Intent(ACTION_ON_EXECUTION_FINISHED));
            } else {
                App.getApp().sendBroadcast(new Intent(ACTION_ON_EXECUTION_FINISHED)
                        .putExtra(EXTRA_EXCEPTION_MESSAGE, e.getMessage()));
            }
        }

    };


    public static void openByOtherApps(String path) {
        Uri uri = Uri.parse("file://" + path);
        App.getApp().startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(uri, "text/plain").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static void openByOtherApps(File file) {
        openByOtherApps(file.getPath());
    }

    public static void createShortcut(ScriptFile scriptFile) {
        new Shortcut(App.getApp()).name(scriptFile.getSimplifiedName())
                .targetClass(ShortcutActivity.class)
                .iconRes(R.drawable.ic_node_js_black)
                .extras(new Intent().putExtra(CommonUtils.EXTRA_KEY_PATH, scriptFile.getPath()))
                .send();
    }


    public static void edit(ScriptFile file) {
        EditActivity.editFile(App.getApp(), file.getSimplifiedName(), file.getPath());
    }

    public static void edit(String path) {
        edit(new ScriptFile(path));
    }

    public static ScriptExecution run(ScriptFile file) {
        return run(file.toSource(), file.getParent());
    }

    public static ScriptExecution run(ScriptSource source, String directoryPath) {
        return AutoJs.getInstance().getScriptEngineService().execute(source, new ExecutionConfig()
                .requirePath(directoryPath, StorageScriptProvider.DEFAULT_DIRECTORY_PATH));
    }

    public static ScriptExecution run(ScriptSource source) {
        return AutoJs.getInstance().getScriptEngineService().execute(source, new ExecutionConfig()
                .requirePath(StorageScriptProvider.DEFAULT_DIRECTORY_PATH));
    }

    public static ScriptExecution runWithBroadcastSender(ScriptSource scriptSource, String directoryPath) {
        return AutoJs.getInstance().getScriptEngineService().execute(scriptSource, BROADCAST_SENDER_SCRIPT_EXECUTION_LISTENER,
                new ExecutionConfig().requirePath(directoryPath, StorageScriptProvider.DEFAULT_DIRECTORY_PATH));
    }

    public static ScriptExecution run(Context context, Sample file) {
        ScriptSource source = new StringScriptSource(file.name, AssetsCache.get(context.getAssets(), file.path));
        return AutoJs.getInstance().getScriptEngineService().execute(source);
    }

    public static ScriptExecution runWithBroadcastSender(ScriptSource source) {
        return AutoJs.getInstance().getScriptEngineService().execute(source, BROADCAST_SENDER_SCRIPT_EXECUTION_LISTENER,
                new ExecutionConfig().requirePath(StorageScriptProvider.DEFAULT_DIRECTORY_PATH));
    }

    public static ScriptExecution runRepeatedly(ScriptFile scriptFile, int loopTimes, long delay, long interval) {
        ScriptSource source = scriptFile.toSource();
        String directoryPath = scriptFile.getParent();
        return AutoJs.getInstance().getScriptEngineService().execute(source, new ExecutionConfig()
                .requirePath(directoryPath, StorageScriptProvider.DEFAULT_DIRECTORY_PATH)
                .loop(delay, loopTimes, interval));
    }
}
