package com.stardust.scriptdroid.script;

import java.io.File;

/**
 * Created by Stardust on 2017/1/23.
 */

public abstract class ScriptFileList {

    private static ScriptFileList impl;

    public static void setImpl(ScriptFileList list) {
        impl = list;
    }

    public static ScriptFileList getImpl() {
        return impl;
    }

    public abstract void add(ScriptFile scriptFile);

    public abstract ScriptFile get(int i);

    public abstract void remove(int i);

    public abstract void rename(int position, String newName, boolean renameFile);

    public abstract int size();

    public boolean deleteFromFileSystem(int i) {
        if (i < 0 || i >= size())
            return false;
        File file = get(i);
        remove(i);
        return file.delete();
    }

    public abstract boolean containsPath(String path);


}
