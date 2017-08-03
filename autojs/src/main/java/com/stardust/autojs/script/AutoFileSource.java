package com.stardust.autojs.script;

import android.support.annotation.NonNull;

import com.stardust.pio.PFile;

import java.io.File;
import java.io.Reader;

/**
 * Created by Stardust on 2017/8/2.
 */

public class AutoFileSource extends ScriptSource {

    public static final String ENGINE = AutoFileSource.class.getName() + ".Engine";
    private File mFile;

    public AutoFileSource(File file) {
        super(PFile.getNameWithoutExtension(file.getAbsolutePath()));
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

}
