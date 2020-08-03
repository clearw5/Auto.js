package org.autojs.autojs.model.sample;

import android.content.res.AssetManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stardust.autojs.script.JavaScriptSource;
import com.stardust.autojs.script.ScriptSource;
import com.stardust.pio.PFiles;
import com.stardust.pio.UncheckedIOException;
import org.autojs.autojs.model.script.ScriptFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by Stardust on 2017/10/28.
 */

public class SampleFile extends ScriptFile {

    private long mLength;
    private AssetManager mAssetManager;

    public SampleFile(@NonNull String pathname, AssetManager assetManager) {
        super(pathname);
        mAssetManager = assetManager;
        init();
    }

    private void init() {
        if (isDirectory()) {
            mLength = 0;
            return;
        }
        try {
            InputStream inputStream = openInputStream();
            mLength = inputStream.available();
            inputStream.close();
        } catch (IOException e) {
            mLength = 0;
        }
    }

    public SampleFile(String parent, @NonNull String child, AssetManager assetManager) {
        super(parent, child);
        mAssetManager = assetManager;
        init();
    }

    public SampleFile(File parent, @NonNull String child, AssetManager assetManager) {
        super(parent, child);
        mAssetManager = assetManager;
        init();
    }

    @Override
    public boolean isFile() {
        return getName().endsWith(".js");
    }

    @Override
    public boolean isDirectory() {
        return !isFile();
    }

    @Override
    public long length() {
        return mLength;
    }

    @Override
    public String[] list() {
        try {
            return mAssetManager.list(getPath());
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public ScriptSource toSource() {
        return new JavaScriptSource(getSimplifiedName()) {
            @NonNull
            @Override
            public String getScript() {
                try {
                    return PFiles.read(openInputStream());
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @Nullable
            @Override
            public Reader getScriptReader() {
                try {
                    return new InputStreamReader(openInputStream());
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        };
    }

    public InputStream openInputStream() throws IOException {
        return mAssetManager.open(getPath());
    }
}
