package com.stardust.autojs.execution;

import java.io.Serializable;

/**
 * Created by Stardust on 2017/2/1.
 */
public class ExecutionConfig implements Serializable {

    private String[] mRequirePath = new String[0];
    private static final ExecutionConfig DEFAULT = new ExecutionConfig();

    public static ExecutionConfig getDefault() {
        return DEFAULT;
    }

    public boolean runInNewThread = true;

    public ExecutionConfig runInNewThread(boolean runInNewThread) {
        this.runInNewThread = runInNewThread;
        return this;
    }

    public ExecutionConfig requirePath(String... requirePath) {
        mRequirePath = requirePath;
        return this;
    }

    public String[] getRequirePath() {
        return mRequirePath;
    }
}
