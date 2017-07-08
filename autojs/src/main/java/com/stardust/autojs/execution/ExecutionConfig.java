package com.stardust.autojs.execution;

import java.io.Serializable;

/**
 * Created by Stardust on 2017/2/1.
 */
public class ExecutionConfig implements Serializable {

    private String[] mRequirePath = new String[0];
    private static final ExecutionConfig DEFAULT = new ExecutionConfig();
    public long delay = 0;
    public long interval = 0;
    public int loopTimes = 1;

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

    public ExecutionConfig loop(long delay, int loopTimes, long interval) {
        this.delay = delay;
        this.loopTimes = loopTimes;
        this.interval = interval;
        return this;
    }
}
