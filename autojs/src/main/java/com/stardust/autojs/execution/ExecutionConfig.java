package com.stardust.autojs.execution;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Stardust on 2017/2/1.
 */
public class ExecutionConfig implements Serializable {

    public static final String TAG = "execution.config";
    private List<String> mRequirePath = Collections.emptyList();
    private String mExecutePath;
    public long delay = 0;
    public long interval = 0;
    public int loopTimes = 1;
    private Map<String, Object> mArguments = new HashMap<>();
    private int mIntentFlags = 0;

    public static ExecutionConfig getDefault() {
        return new ExecutionConfig();
    }

    public boolean runInNewThread = true;

    public ExecutionConfig runInNewThread(boolean runInNewThread) {
        this.runInNewThread = runInNewThread;
        return this;
    }

    public int getIntentFlags() {
        return mIntentFlags;
    }

    public ExecutionConfig setIntentFlags(int intentFlags) {
        mIntentFlags = intentFlags;
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

    public void setArgument(String key, Object object){
        mArguments.put(key, object);
    }

    public Object getArgument(String key){
        return mArguments.get(key);
    }

    public Map<String, Object> getArguments() {
        return mArguments;
    }

    public ExecutionConfig loop(long delay, int loopTimes, long interval) {
        this.delay = delay;
        this.loopTimes = loopTimes;
        this.interval = interval;
        return this;
    }
}
