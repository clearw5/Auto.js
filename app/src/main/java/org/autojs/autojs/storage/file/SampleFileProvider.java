package org.autojs.autojs.storage.file;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;

import com.stardust.pio.PFile;
import com.stardust.pio.PFiles;
import org.autojs.autojs.model.script.ScriptFile;

import java.io.File;

import io.reactivex.Observable;

/**
 * Created by Stardust on 2017/10/28.
 */

public class SampleFileProvider extends StorageFileProvider {

    private AssetManager mAssetManager;
    private Context mContext;

    public SampleFileProvider(String path, Context context) {
        super(path, 10);
        mContext = context;
        mAssetManager = context.getAssets();
    }

    public SampleFileProvider(Context context) {
        this("sample/", context);
    }

    @Override
    protected Observable<PFile> listFiles(PFile directory) {
        return Observable.just(directory)
                .flatMap(dir -> Observable.fromArray(mAssetManager.list(directory.getPath())))
                .map(path -> {
                    String absPath = new File(directory, path).getPath();
                    if (!absPath.endsWith(".js")) {
                        return new AssetDirectory(absPath);
                    }
                    PFile file = new PFile(mContext.getFilesDir(), absPath);
                    if (!file.exists()) {
                        copySample(mContext, absPath, file.getPath());
                    }
                    return file;
                });

    }

    public static boolean copySample(Context context, String samplePath, String pathTo) {
        PFiles.ensureDir(pathTo);
        return PFiles.copyAsset(context, samplePath, pathTo);
    }

    public static class AssetDirectory extends ScriptFile {

        public AssetDirectory(@NonNull String pathname) {
            super(pathname);
        }

        @Override
        public boolean isDirectory() {
            return true;
        }

        @Override
        public boolean isFile() {
            return false;
        }
    }

}
