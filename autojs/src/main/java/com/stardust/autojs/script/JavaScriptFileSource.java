package com.stardust.autojs.script;

import android.support.annotation.NonNull;

import com.stardust.pio.PFile;
import com.stardust.pio.UncheckedIOException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

/**
 * Created by Stardust on 2017/4/2.
 */

public class JavaScriptFileSource extends JavaScriptSource {

    private File mFile;
    private String mScript;

    public JavaScriptFileSource(File file) {
        super(PFile.getNameWithoutExtension(file.getName()));
        mFile = file;
    }

    public JavaScriptFileSource(String path) {
        this(new File(path));
    }

    public JavaScriptFileSource(String name, File file) {
        super(name);
        mFile = file;
    }

    @NonNull
    @Override
    public String getScript() {
        if (mScript == null)
            mScript = PFile.read(mFile);
        return mScript;
    }

    @Override
    public Reader getScriptReader() {
        try {
            return new FileReader(mFile);
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    public File getFile() {
        return mFile;
    }

    @Override
    public String toString() {
        return mFile.toString();
    }
}
