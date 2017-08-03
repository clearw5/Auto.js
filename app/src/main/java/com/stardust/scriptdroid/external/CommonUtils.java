package com.stardust.scriptdroid.external;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.stardust.autojs.script.JavaScriptFileSource;
import com.stardust.autojs.script.ScriptSource;
import com.stardust.autojs.script.SequenceScriptSource;
import com.stardust.autojs.script.StringScriptSource;
import com.stardust.scriptdroid.script.PathChecker;
import com.stardust.scriptdroid.script.Scripts;

import java.io.File;

/**
 * Created by Stardust on 2017/4/1.
 */

public class CommonUtils {

    public static final String EXTRA_KEY_PATH = "path";

    public static final String EXTRA_KEY_PRE_EXECUTE_SCRIPT = "script";

    public static boolean isTaskerBundleValid(Bundle bundle) {
        return bundle.containsKey(CommonUtils.EXTRA_KEY_PATH) || bundle.containsKey(EXTRA_KEY_PRE_EXECUTE_SCRIPT);
    }

    public static void handleIntent(Context context, Intent intent) {
        String path = getPath(intent);
        String directoryPath = null;
        String script = intent.getStringExtra(CommonUtils.EXTRA_KEY_PRE_EXECUTE_SCRIPT);
        ScriptSource source = null;
        if (path == null && script != null) {
            source = new StringScriptSource(script);
        } else if (path != null && new PathChecker(context).checkAndToastError(path)) {
            JavaScriptFileSource fileScriptSource = new JavaScriptFileSource(path);
            if (script != null) {
                source = new SequenceScriptSource(fileScriptSource.getName(), new StringScriptSource(script), fileScriptSource);
            } else {
                source = fileScriptSource;
            }
            directoryPath = new File(path).getParent();
        }
        if (source != null) {
            if (directoryPath == null)
                Scripts.run(source);
            else
                Scripts.run(source, directoryPath);
        }
    }

    private static String getPath(Intent intent) {
        if (intent.getData() != null && intent.getData().getPath() != null)
            return intent.getData().getPath();
        return intent.getStringExtra(CommonUtils.EXTRA_KEY_PATH);
    }
}
