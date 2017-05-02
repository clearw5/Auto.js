package com.stardust.autojs.execution;

import java.io.Serializable;

/**
 * Created by Stardust on 2017/2/1.
 */
public class ExecutionConfig implements Serializable {

    private static final ExecutionConfig DEFAULT = new ExecutionConfig();

    public static ExecutionConfig getDefault() {
        return DEFAULT;
    }

    public boolean runInNewThread = true;

    public ExecutionConfig runInNewThread(boolean runInNewThread) {
        this.runInNewThread = runInNewThread;
        return this;
    }

}
