package com.stardust.scriptdroid.model.script;

import android.os.Environment;
import android.support.annotation.Nullable;

import com.stardust.autojs.script.AutoFileSource;
import com.stardust.autojs.script.JavaScriptFileSource;
import com.stardust.autojs.script.ScriptSource;
import com.stardust.pio.PFile;
import com.stardust.pio.PFiles;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Created by Stardust on 2017/1/23.
 */

public class ScriptFile extends PFile {

    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_AUTO = 1;
    public static final int TYPE_JAVA_SCRIPT = 2;

    private int mType = -1;

    public ScriptFile(String path) {
        super(path);
    }

    public ScriptFile(String parent, String name) {
        super(parent, name);
    }

    public ScriptFile(ScriptFile parent, String child) {
        super(parent, child);
    }

    public ScriptFile(File file) {
        super(file.getPath());
    }

    public int getType() {
        if (mType == -1) {
            mType = getName().endsWith(".js") ? TYPE_JAVA_SCRIPT :
                    getName().endsWith(".auto") ? TYPE_AUTO :
                            TYPE_UNKNOWN;
        }
        return mType;
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
        return listFiles(file -> (file.getName().endsWith(".js") || file.getName().endsWith(".auto") ||
                file.isDirectory()) && !file.getName().startsWith("."));
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

    public ScriptSource toSource() {
        if (getType() == TYPE_JAVA_SCRIPT) {
            return new JavaScriptFileSource(this);
        } else {
            return new AutoFileSource(this);
        }
    }
}
