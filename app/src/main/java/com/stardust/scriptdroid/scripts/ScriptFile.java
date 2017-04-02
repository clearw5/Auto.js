package com.stardust.scriptdroid.scripts;

import android.os.Environment;

import com.stardust.pio.PFile;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Created by Stardust on 2017/1/23.
 */

public class ScriptFile extends File {

    private String mSimplifyPath;
    private String mSimplifiedName;

    public ScriptFile(String path) {
        super(path);
        init();
    }

    private void init() {
        mSimplifiedName = PFile.getNameWithoutExtension(getPath());
        mSimplifyPath = getPath();
        if (mSimplifyPath.startsWith(Environment.getExternalStorageDirectory().getPath())) {
            mSimplifyPath = mSimplifyPath.substring(Environment.getExternalStorageDirectory().getPath().length());
        }
    }

    public ScriptFile(ScriptFile parent, String child) {
        super(parent, child);
        init();
    }

    public boolean renameTo(String newName) {
        return renameTo(new File(getParent(), newName));
    }

    public String getSimplifiedPath() {
        return mSimplifyPath;
    }

    @Override
    public ScriptFile getParentFile() {
        String p = this.getParent();
        if (p == null)
            return null;
        return new ScriptFile(p);
    }

    @Override
    public ScriptFile[] listFiles() {
        return listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() || (file.getName().endsWith(".js") && !file.getName().startsWith("."));
            }
        });
    }

    @Override
    public ScriptFile[] listFiles(FilenameFilter filter) {
        String ss[] = list();
        if (ss == null) return null;
        ArrayList<ScriptFile> files = new ArrayList<>();
        for (String s : ss)
            if ((filter == null) || filter.accept(this, s))
                files.add(new ScriptFile(this, s));
        return files.toArray(new ScriptFile[files.size()]);
    }

    @Override
    public ScriptFile[] listFiles(FileFilter filter) {
        String ss[] = list();
        if (ss == null) return null;
        ArrayList<ScriptFile> files = new ArrayList<>();
        for (String s : ss) {
            ScriptFile f = new ScriptFile(this, s);
            if ((filter == null) || filter.accept(f))
                files.add(f);
        }
        return files.toArray(new ScriptFile[files.size()]);

    }

    public String getSimplifiedName() {
        return mSimplifiedName;
    }

}
