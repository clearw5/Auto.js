package autojs;

/**
 * Created by Stardust on 2017/2/1.
 */
public class RunningConfig {

    private static final RunningConfig RUNNING_CONFIG = new RunningConfig();
    public String path;
    public String prepareScript = "";

    public static RunningConfig getDefault() {
        return RUNNING_CONFIG;
    }

    public boolean runInNewThread = true;

    public RunningConfig runInNewThread(boolean runInNewThread) {
        this.runInNewThread = runInNewThread;
        return this;
    }

    public RunningConfig path(String path) {
        this.path = path;
        return this;
    }

    public RunningConfig prepareScript(String script) {
        this.prepareScript = script;
        return this;
    }
}
