package org.autojs.autojs.model.explorer;

import com.stardust.pio.PFile;

import java.io.FileFilter;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class ExplorerFileProvider implements ExplorerProvider {

    private final FileFilter mFileFilter;

    public ExplorerFileProvider(FileFilter fileFilter) {
        mFileFilter = fileFilter;
    }

    public ExplorerFileProvider() {
        this(null);
    }

    @Override
    public Single<? extends ExplorerPage> getExplorerPage(ExplorerPage page) {
        ExplorerPage parent = page.getParent();
        String path = page.getPath();
        return listFiles(new PFile(path))
                .collectInto(createExplorerPage(path, parent), (p, file) -> {
                    if(file.isDirectory()){
                        p.addChild(new ExplorerDirPage(file, p));
                    }else {
                        p.addChild(new ExplorerFileItem(file, p));
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    protected ExplorerDirPage createExplorerPage(String path, ExplorerPage parent) {
        return new ExplorerDirPage(path, parent);
    }

    protected Observable<PFile> listFiles(PFile directory) {
        return Observable.just(directory)
                .flatMap(dir -> {
                    PFile[] files;
                    if (mFileFilter == null) {
                        files = dir.listFiles();
                    } else {
                        files = dir.listFiles(mFileFilter);
                    }
                    if (files == null) {
                        return Observable.empty();
                    }
                    return Observable.fromArray(files);
                });
    }
}
