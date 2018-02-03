package com.stardust.autojs.execution;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Stardust on 2017/2/1.
 */
public class ExecutionConfig implements Serializable {

    private List<String> mRequirePath = Collections.emptyList();
    private String mExecutePath;
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
        mRequirePath = new ArrayList<>(Arrays.asList(requirePath));
        if (mExecutePath != null) {
            mRequirePath.add(mExecutePath);
        }
        return this;
    }

    public ExecutionConfig executePath(String executePath) {
        mExecutePath = executePath;
        return this;
    }

    public List<String> getRequirePath() {
        return mRequirePath;
    }

    public String getExecutePath() {
        return mExecutePath;
    }

    public ExecutionConfig loop(long delay, int loopTimes, long interval) {
        this.delay = delay;
        this.loopTimes = loopTimes;
        this.interval = interval;
        return this;
    }
}
