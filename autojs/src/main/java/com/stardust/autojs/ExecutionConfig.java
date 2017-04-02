package com.stardust.autojs;

import java.io.Serializable;

/**
 * Created by Stardust on 2017/2/1.
 */
public class ExecutionConfig implements Serializable {

    private static final ExecutionConfig RUNNING_CONFIG = new ExecutionConfig();
    public String path;
    public String prepareScript = "";

    public static ExecutionConfig getDefault() {
        return RUNNING_CONFIG;
    }

    public boolean runInNewThread = true;

    public ExecutionConfig runInNewThread(boolean runInNewThread) {
        this.runInNewThread = runInNewThread;
        return this;
    }

    public ExecutionConfig path(String path) {
        this.path = path;
        return this;
    }

    public ExecutionConfig prepareScript(String script) {
        this.prepareScript = script;
        return this;
    }
}
