package com.stardust.autojs.util;

import android.util.Log;

import com.stardust.autojs.runtime.api.ProcessShell;

import java.lang.reflect.Field;

/**
 * Created by Stardust on 2017/8/3.
 */

public class ProcessUtils {


    private static final String LOG_TAG = "ProcessUtils";

    // FIXME: 2017/8/3
    public static void killProcessTree(Process process) {
        int pid = getProcessPid(process);
        if (pid >= 0)
            kill(pid);
        process.destroy();

    }

    private static int getProcessPid(Process process) {
        try {
            Field pid = process.getClass().getDeclaredField("pid");
            pid.setAccessible(true);
            return (int) pid.get(process);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void kill(int pid) {
        String cmd = "kill -TERM -- -" + pid;
        Log.d(LOG_TAG, cmd);
        ProcessShell.exec(cmd, true);
    }
}
