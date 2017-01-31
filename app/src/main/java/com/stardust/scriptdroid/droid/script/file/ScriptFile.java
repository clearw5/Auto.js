package com.stardust.scriptdroid.droid.script.file;

import android.os.Environment;

import com.stardust.scriptdroid.droid.Droid;

import java.io.File;

/**
 * Created by Stardust on 2017/1/23.
 */

public class ScriptFile {

    public static final String DEFAULT_FOLDER = Environment.getExternalStorageDirectory() + "/脚本/";
    public String name;

    public String path;

    public ScriptFile(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public void run() {
        Droid.getInstance().runScriptFile(toFile());
    }

    public File toFile() {
        return new File(path);
    }

    public void rename(String newName) {
        File file = toFile();
        file.renameTo(new File(file.getParent(), newName));
    }
}
