package com.stardust.scriptdroid.tool;

import com.stardust.scriptdroid.script.StorageFileProvider;

import java.io.File;
import java.io.IOException;

/**
 * Created by Stardust on 2017/5/22.
 */

public class Logcat {

    private static final String LOG_PATH = StorageFileProvider.DEFAULT_DIRECTORY_PATH + "log.txt";
    private static final String DISABLE_PATH = StorageFileProvider.DEFAULT_DIRECTORY_PATH + "disable_log.txt";
    private static Process sLogSavingProcess;

    public static void startLogSavingIfNeeded() {
        if (sLogSavingProcess != null)
            return;
        if (new File(DISABLE_PATH).exists())
            return;
        try {
            sLogSavingProcess = Runtime.getRuntime().exec("logcat -f " + LOG_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteLogFile() {
        new File(LOG_PATH).delete();
    }
}
