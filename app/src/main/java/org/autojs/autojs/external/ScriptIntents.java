package org.autojs.autojs.external;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.stardust.autojs.execution.ExecutionConfig;
import com.stardust.autojs.script.JavaScriptFileSource;
import com.stardust.autojs.script.ScriptSource;
import com.stardust.autojs.script.SequenceScriptSource;
import com.stardust.autojs.script.StringScriptSource;

import org.autojs.autojs.Pref;
import org.autojs.autojs.autojs.AutoJs;
import org.autojs.autojs.model.script.PathChecker;

import java.io.File;

/**
 * Created by Stardust on 2017/4/1.
 */

public class ScriptIntents {

    public static final String EXTRA_KEY_PATH = "path";
    public static final String EXTRA_KEY_PRE_EXECUTE_SCRIPT = "script";
    public static final String EXTRA_KEY_LOOP_TIMES = "loop";
    public static final String EXTRA_KEY_LOOP_INTERVAL = "interval";
    public static final String EXTRA_KEY_DELAY = "delay";

    public static boolean isTaskerBundleValid(Bundle bundle) {
        return bundle.containsKey(ScriptIntents.EXTRA_KEY_PATH) || bundle.containsKey(EXTRA_KEY_PRE_EXECUTE_SCRIPT);
    }

    public static boolean handleIntent(Context context, Intent intent) {
        String path = getPath(intent);
        String script = intent.getStringExtra(ScriptIntents.EXTRA_KEY_PRE_EXECUTE_SCRIPT);
        int loopTimes = intent.getIntExtra(EXTRA_KEY_LOOP_TIMES, 1);
        long delay = intent.getLongExtra(EXTRA_KEY_DELAY, 0);
        long interval = intent.getLongExtra(EXTRA_KEY_LOOP_INTERVAL, 0);
        ScriptSource source = null;
        ExecutionConfig config = new ExecutionConfig();
        config.setDelay(delay);
        config.setLoopTimes(loopTimes);
        config.setInterval(interval);
        config.setArgument("intent", intent);
        if (path == null && script != null) {
            source = new StringScriptSource(script);
        } else if (path != null && new PathChecker(context).checkAndToastError(path)) {
            JavaScriptFileSource fileScriptSource = new JavaScriptFileSource(path);
            if (script != null) {
                source = new SequenceScriptSource(fileScriptSource.getName(), new StringScriptSource(script), fileScriptSource);
            } else {
                source = fileScriptSource;
            }
            config.setWorkingDirectory(new File(path).getParent());
        } else {
            config.setWorkingDirectory(Pref.getScriptDirPath());
        }
        if (source == null) {
            return false;
        }
        AutoJs.getInstance().getScriptEngineService().execute(source, config);
        return true;
    }

    private static String getPath(Intent intent) {
        if (intent.getData() != null && intent.getData().getPath() != null)
            return intent.getData().getPath();
        return intent.getStringExtra(ScriptIntents.EXTRA_KEY_PATH);
    }
}
