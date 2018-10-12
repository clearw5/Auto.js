package org.autojs.autojs.model.explorer;

import android.content.Context;
import android.content.res.AssetManager;

import com.stardust.autojs.project.ProjectConfig;
import com.stardust.pio.PFile;
import com.stardust.pio.PFiles;

import org.autojs.autojs.Pref;
import org.autojs.autojs.model.script.ScriptFile;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class WorkspaceFileProvider extends ExplorerFileProvider {

    private static final String SAMPLE_PATH = "sample";

    private final PFile mSampleDir;
    private final AssetManager mAssetManager;
    private final Context mContext;

    public WorkspaceFileProvider(Context context, FileFilter fileFilter) {
        super(fileFilter);
        mContext = context;
        mAssetManager = context.getAssets();
        mSampleDir = new PFile(context.getFilesDir(), SAMPLE_PATH);
    }

    @Override
    public Single<? extends ExplorerPage> getExplorerPage(ExplorerPage page) {
        ExplorerPage parent = page.getParent();
        String path = page.getPath();
        return listFiles(new PFile(path))
                .collectInto(createExplorerPage(path, parent), (p, file) -> {
                    if (file.isDirectory()) {
                        ProjectConfig projectConfig = ProjectConfig.fromProjectDir(file.getPath());
                        if (projectConfig != null) {
                            p.addChild(new ExplorerProjectPage(file, parent, projectConfig));
                            return;
                        }
                        if (inSampleDir(file)) {
                            p.addChild(new ExplorerSamplePage(file, p));
                        } else {
                            p.addChild(new ExplorerDirPage(file, p));
                        }
                    } else {
                        if (file.getPath().startsWith(mSampleDir.getPath())) {
                            p.addChild(new ExplorerSampleItem(file, p));
                        } else {
                            p.addChild(new ExplorerFileItem(file, p));
                        }
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    private boolean inSampleDir(PFile file) {
        return file.getPath().startsWith(mSampleDir.getPath());
    }

    @Override
    protected Observable<PFile> listFiles(PFile directory) {
        if (inSampleDir(directory)) {
            return listSamples(directory);
        }
        return super.listFiles(directory);
    }

    private Observable<PFile> listSamples(PFile directory) {
        String pathOfSample;
        if (directory.getPath().length() <= mSampleDir.getPath().length() + 1) {
            pathOfSample = "";
        } else {
            pathOfSample = directory.getPath().substring(mSampleDir.getPath().length());
        }
        String pathOfAsset = SAMPLE_PATH + pathOfSample;
        return Observable.just(pathOfAsset)
                .flatMap(path -> Observable.fromArray(mAssetManager.list(path)))
                .map(child -> {
                    PFile file = new PFile(new File(directory, child).getPath());
                    if (file.exists()) {
                        return file;
                    }
                    try {
                        InputStream stream = mAssetManager.open(pathOfAsset + "/" + child);
                        PFiles.copyStream(stream, file.getPath());
                    } catch (FileNotFoundException e) {
                        file.mkdirs();
                    }
                    return file;
                });
    }

    public Observable<ScriptFile> resetSample(ScriptFile file) {
        if (file.getPath().length() <= mSampleDir.getPath().length() + 1) {
            return null;
        }
        String pathOfSample = file.getPath().substring(mSampleDir.getPath().length());
        String pathOfAsset = SAMPLE_PATH + pathOfSample;
        return Observable.fromCallable(() -> {
            InputStream stream = mAssetManager.open(pathOfAsset);
            PFiles.copyStream(stream, file.getPath());
            return file;
        })
                .subscribeOn(Schedulers.io());
    }

    @Override
    protected ExplorerDirPage createExplorerPage(String path, ExplorerPage parent) {
        ExplorerDirPage page = super.createExplorerPage(path, parent);
        if (new File(path).equals(new File(Pref.getScriptDirPath()))) {
            page.addChild(ExplorerSamplePage.createRoot(mSampleDir));
        }
        return page;
    }
}
