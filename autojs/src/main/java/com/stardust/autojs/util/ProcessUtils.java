package com.stardust.autojs.util;

import android.util.Log;

import com.stardust.autojs.core.util.ProcessShell;

import java.lang.reflect.Field;

/**
 * Created by Stardust on 2017/8/3.
 */

public class ProcessUtils {


    private static final String LOG_TAG = "ProcessUtils";

    public static int getProcessPid(Process process) {
        try {
            Field pid = process.getClass().getDeclaredField("pid");
            pid.setAccessible(true);
            return (int) pid.get(process);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

}
