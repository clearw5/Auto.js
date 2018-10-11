package org.autojs.autojs.model.script;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.stardust.app.GlobalAppContext;
import com.stardust.autojs.execution.ExecutionConfig;
import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.autojs.execution.ScriptExecutionListener;
import com.stardust.autojs.execution.SimpleScriptExecutionListener;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.autojs.script.ScriptSource;
import com.stardust.util.IntentUtil;

import org.autojs.autojs.Pref;
import org.autojs.autojs.R;
import org.autojs.autojs.autojs.AutoJs;
import org.autojs.autojs.external.ScriptIntents;
import org.autojs.autojs.external.fileprovider.AppFileProvider;
import org.autojs.autojs.external.shortcut.Shortcut;
import org.autojs.autojs.external.shortcut.ShortcutActivity;
import org.autojs.autojs.ui.edit.EditActivity;

import org.mozilla.javascript.RhinoException;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by Stardust on 2017/5/3.
 */

public class Scripts {

    public static final String ACTION_ON_EXECUTION_FINISHED = "ACTION_ON_EXECUTION_FINISHED";
    public static final String EXTRA_EXCEPTION_MESSAGE = "message";
    public static final String EXTRA_EXCEPTION_LINE_NUMBER = "lineNumber";
    public static final String EXTRA_EXCEPTION_COLUMN_NUMBER = "columnNumber";

    public static final FileFilter FILE_FILTER = file ->
            file.isDirectory() || file.getName().endsWith(".js") || file.getName().endsWith(".auto");

    private static final ScriptExecutionListener BROADCAST_SENDER_SCRIPT_EXECUTION_LISTENER = new SimpleScriptExecutionListener() {

        @Override
        public void onSuccess(ScriptExecution execution, Object result) {
            GlobalAppContext.get().sendBroadcast(new Intent(ACTION_ON_EXECUTION_FINISHED));
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
                GlobalAppContext.get().sendBroadcast(new Intent(ACTION_ON_EXECUTION_FINISHED)
                        .putExtra(EXTRA_EXCEPTION_LINE_NUMBER, line)
                        .putExtra(EXTRA_EXCEPTION_COLUMN_NUMBER, col));
            } else {
                GlobalAppContext.get().sendBroadcast(new Intent(ACTION_ON_EXECUTION_FINISHED)
                        .putExtra(EXTRA_EXCEPTION_MESSAGE, e.getMessage())
                        .putExtra(EXTRA_EXCEPTION_LINE_NUMBER, line)
                        .putExtra(EXTRA_EXCEPTION_COLUMN_NUMBER, col));
            }
        }

    };


    public static void openByOtherApps(String path) {
        IntentUtil.viewFile(GlobalAppContext.get(), path, "text/plain", AppFileProvider.AUTHORITY);
    }

    public static void openByOtherApps(File file) {
        openByOtherApps(file.getPath());
    }

    public static void createShortcut(ScriptFile scriptFile) {
        new Shortcut(GlobalAppContext.get()).name(scriptFile.getSimplifiedName())
                .targetClass(ShortcutActivity.class)
                .iconRes(R.drawable.ic_node_js_black)
                .extras(new Intent().putExtra(ScriptIntents.EXTRA_KEY_PATH, scriptFile.getPath()))
                .send();
    }


    public static void edit(ScriptFile file) {
        EditActivity.editFile(GlobalAppContext.get(), file.getSimplifiedName(), file.getPath());
    }

    public static void edit(String path) {
        edit(new ScriptFile(path));
    }

    public static ScriptExecution run(ScriptFile file) {
        try {
            return AutoJs.getInstance().getScriptEngineService().execute(file.toSource(), new ExecutionConfig()
                    .executePath(file.getParent()));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(GlobalAppContext.get(), e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }


    public static ScriptExecution run(ScriptSource source) {
        try {
            return AutoJs.getInstance().getScriptEngineService().execute(source, new ExecutionConfig()
                    .executePath(Pref.getScriptDirPath())
                    .requirePath(Pref.getScriptDirPath()));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(GlobalAppContext.get(), e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
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
        GlobalAppContext.get().startActivity(Intent.createChooser(new Intent(Intent.ACTION_SEND)
                        .setType("text/plain")
                        .putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file)),
                GlobalAppContext.getString(R.string.text_send)
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

    }
}
