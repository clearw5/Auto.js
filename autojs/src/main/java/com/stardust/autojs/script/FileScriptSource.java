package com.stardust.autojs.script;

import com.stardust.autojs.script.ScriptSource;
import com.stardust.pio.PFile;

import java.io.File;

/**
 * Created by Stardust on 2017/4/2.
 */

public class FileScriptSource extends ScriptSource {

    private File mFile;
    private String mScript;

    public FileScriptSource(File file) {
        super(PFile.getNameWithoutExtension(file.getName()));
        mFile = file;
    }

    public FileScriptSource(String path) {
        this(new File(path));
    }

    public FileScriptSource(String name, File file) {
        super(name);
        mFile = file;
    }

    @Override
    public String getScript() {
        if (mScript == null)
            mScript = PFile.read(mFile);
        return mScript;
    }

    @Override
    public String toString() {
        return mFile.toString();
    }
}
