package com.stardust.autojs.script;

import androidx.annotation.NonNull;

import com.stardust.pio.PFiles;
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
    private boolean mCustomsName = false;

    public JavaScriptFileSource(File file) {
        super(PFiles.getNameWithoutExtension(file.getName()));
        mFile = file;
    }

    public JavaScriptFileSource(String path) {
        this(new File(path));
    }

    public JavaScriptFileSource(String name, File file) {
        super(name);
        mCustomsName = true;
        mFile = file;
    }

    @NonNull
    @Override
    public String getScript() {
        if (mScript == null)
            mScript = PFiles.read(mFile);
        return mScript;
    }

    @Override
    protected int parseExecutionMode() {
        short flags = EncryptedScriptFileHeader.INSTANCE.getHeaderFlags(mFile);
        if (flags == EncryptedScriptFileHeader.FLAG_INVALID_FILE) {
            return super.parseExecutionMode();
        }
        return flags;
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
        if (mCustomsName) {
            return super.toString();
        }
        return mFile.toString();
    }
}
