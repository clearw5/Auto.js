package com.stardust.autojs.execution;

import com.stardust.autojs.ScriptEngineService;
import com.stardust.autojs.engine.ScriptEngine;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.api.AbstractShell;
import com.stardust.autojs.runtime.api.ProcessShell;
import com.stardust.util.IntentExtras;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stardust on 2017/7/16.
 */

public class RootedScriptExecution extends RunnableScriptExecution {

    private static int count = 0;
    private static Map<String, ScriptExecutionTask> arguments = new HashMap<>();

    public RootedScriptExecution(ScriptEngineService service, ScriptExecutionTask task) {
        super(service, task);
    }

    @Override
    public void run() {
    }


    public static void main(String[] args) {
    }

}
