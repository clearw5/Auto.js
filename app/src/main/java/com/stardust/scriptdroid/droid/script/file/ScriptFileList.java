package com.stardust.scriptdroid.droid.script.file;

import java.io.File;

/**
 * Created by Stardust on 2017/1/23.
 */

public abstract class ScriptFileList {

    public abstract void add(ScriptFile scriptFile);

    public abstract ScriptFile get(int i);

    public abstract void remove(int i);

    public abstract void rename(int position, String newName);

    public abstract int size();

    public boolean deleteFromFileSystem(int i) {
        File file = new File(get(i).path);
        remove(i);
        return file.delete();
    }


}
