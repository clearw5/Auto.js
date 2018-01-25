package com.stardust.scriptdroid.model.script;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.stardust.autojs.execution.ExecutionConfig;
import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.autojs.execution.ScriptExecutionListener;
import com.stardust.autojs.execution.SimpleScriptExecutionListener;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.autojs.script.JavaScriptFileSource;
import com.stardust.autojs.script.ScriptSource;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.autojs.AutoJs;
import com.stardust.scriptdroid.external.ScriptIntents;
import com.stardust.scriptdroid.external.shortcut.Shortcut;
import com.stardust.scriptdroid.external.shortcut.ShortcutActivity;
import com.stardust.scriptdroid.storage.file.StorageFileProvider;
import com.stardust.scriptdroid.ui.edit.EditActivity;

import org.mozilla.javascript.RhinoException;

import java.io.File;

/**
 * Created by Stardust on 2017/5/3.
 */

public class Scripts {

    public static final String ACTION_ON_EXECUTION_FINISHED = "Don't leave me alone...";
    public static final String EXTRA_EXCEPTION_MESSAGE = "Say something...Eating...17.5.3";
    public static final String EXTRA_EXCEPTION_LINE_NUMBER = "Can we fall in love with each other again...17.9.28";
    public static final String EXTRA_EXCEPTION_COLUMN_NUMBER = "I lost myself....";

    private static final ScriptExecutionListener BROADCAST_SENDER_SCRIPT_EXECUTION_LISTENER = new SimpleScriptExecutionListener() {

        @Override
        public void onSuccess(ScriptExecution execution, Object result) {
            App.getApp().sendBroadcast(new Intent(ACTION_ON_EXECUTION_FINISHED));
        }

        @Override
        public void onException(ScriptExecution execution, Exception e) {
            RhinoException rhinoException = getRhinoException(e);
            int line = -1, col = 0;
            if (rhinoException != null) {
                line = rhinoException.lineNumber();
                col = rhinoException.columnNumber();
            }
            if (ScriptInterruptedException.causedByInterrupted(e)) {
                App.getApp().sendBroadcast(new Intent(ACTION_ON_EXECUTION_FINISHED)
                        .putExtra(EXTRA_EXCEPTION_LINE_NUMBER, line)
                        .putExtra(EXTRA_EXCEPTION_COLUMN_NUMBER, col));
            } else {
                App.getApp().sendBroadcast(new Intent(ACTION_ON_EXECUTION_FINISHED)
                        .putExtra(EXTRA_EXCEPTION_MESSAGE, e.getMessage())
                        .putExtra(EXTRA_EXCEPTION_LINE_NUMBER, line)
                        .putExtra(EXTRA_EXCEPTION_COLUMN_NUMBER, col));
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
                .extras(new Intent().putExtra(ScriptIntents.EXTRA_KEY_PATH, scriptFile.getPath()))
                .send();
    }


    public static void edit(ScriptFile file) {
        EditActivity.editFile(App.getApp(), file.getSimplifiedName(), file.getPath());
    }

    public static void edit(String path) {
        edit(new ScriptFile(path));
    }

    public static ScriptExecution run(ScriptFile file) {
        return AutoJs.getInstance().getScriptEngineService().execute(file.toSource(), new ExecutionConfig()
                .executePath(file.getParent()));
    }


    public static ScriptExecution run(ScriptSource source) {
        return AutoJs.getInstance().getScriptEngineService().execute(source, new ExecutionConfig()
                .executePath(StorageFileProvider.DEFAULT_DIRECTORY_PATH));
    }

    public static ScriptExecution runWithBroadcastSender(File file) {
        return AutoJs.getInstance().getScriptEngineService().execute(new ScriptFile(file).toSource(), BROADCAST_SENDER_SCRIPT_EXECUTION_LISTENER,
                new ExecutionConfig().executePath(file.getParent()));
    }


    public static ScriptExecution runRepeatedly(ScriptFile scriptFile, int loopTimes, long delay, long interval) {
        ScriptSource source = scriptFile.toSource();
        String directoryPath = scriptFile.getParent();
        return AutoJs.getInstance().getScriptEngineService().execute(source, new ExecutionConfig()
                .executePath(directoryPath)
                .loop(delay, loopTimes, interval));
    }

    @Nullable
    public static RhinoException getRhinoException(Throwable e) {
        while (e != null) {
            if (e instanceof RhinoException) {
                return (RhinoException) e;
            }
            e = e.getCause();
        }
        return null;
    }

    public static void send(ScriptFile file) {
        App.getApp().startActivity(Intent.createChooser(new Intent(Intent.ACTION_SEND)
                        .setType("text/plain")
                        .putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file)),
                App.getApp().getString(R.string.text_send)
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

    }
}
