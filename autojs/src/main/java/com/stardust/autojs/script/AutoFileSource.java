package com.stardust.autojs.script;

import com.stardust.pio.PFiles;

import java.io.File;

/**
 * Created by Stardust on 2017/8/2.
 */

public class AutoFileSource extends ScriptSource {

    public static final String ENGINE = AutoFileSource.class.getName() + ".Engine";
    private File mFile;

    public AutoFileSource(File file) {
        super(PFiles.getNameWithoutExtension(file.getAbsolutePath()));
        mFile = file;
    }

    public AutoFileSource(String path) {
        this(new File(path));
    }


    @Override
    public String getEngineName() {
        return ENGINE;
    }

    public File getFile() {
        return mFile;
    }

    @Override
    public String toString() {
        return mFile.toString();
    }
}
