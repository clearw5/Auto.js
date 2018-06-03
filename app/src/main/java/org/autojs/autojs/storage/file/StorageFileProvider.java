package org.autojs.autojs.storage.file;

import android.os.Environment;

import com.stardust.app.GlobalAppContext;
import com.stardust.pio.PFile;

import org.autojs.autojs.Pref;
import org.autojs.autojs.R;

import com.stardust.util.LimitedHashMap;

import org.greenrobot.eventbus.EventBus;

import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

/**
 * Created by Stardust on 2017/3/31.
 */

public class StorageFileProvider {

    public static final int REMOVE = 0;
    public static final int CREATE = 1;
    public static final int CHANGE = 2;
    public static final int ALL = 3;

    public static final FileFilter SCRIPT_FILTER = file ->
            file.isDirectory() || file.getName().endsWith(".js") || file.getName().endsWith(".auto");

    public static class DirectoryChangeEvent {


        private final PFile mDir;
        private final int mChange;
        private final PFile mFile;
        private final PFile mNewFile;

        public DirectoryChangeEvent(PFile dir, int change, PFile file) {
            this(dir, change, file, file);
        }

        public DirectoryChangeEvent(PFile directory) {
            this(directory, ALL, null);
        }

        public DirectoryChangeEvent(PFile dir, int change, PFile oldFile, PFile newFile) {
            mDir = dir;
            mChange = change;
            mFile = oldFile;
            mNewFile = newFile;
        }

        public PFile getDir() {
            return mDir;
        }

        public int getChange() {
            return mChange;
        }

        public PFile getFile() {
            return mFile;
        }

        public PFile getNewFile() {
            return mNewFile;
        }
    }


    private static final PFile INITIAL_DIRECTORY_DEFAULT = new PFile("");

    private static StorageFileProvider externalStorageProvider;
    private static final StorageFileProvider DEFAULT_PROVIDER = new StorageFileProvider(INITIAL_DIRECTORY_DEFAULT, 10, SCRIPT_FILTER);

    private EventBus mDirectoryEventBus = new EventBus();
    private LimitedHashMap<String, List<PFile>> mPFileCache;
    private PFile mInitialDirectory;
    private FileFilter mFileFilter;

    public StorageFileProvider(PFile initialDirectory, int cacheSize, FileFilter fileFilter) {
        mInitialDirectory = initialDirectory;
        mFileFilter = fileFilter;
        mPFileCache = new LimitedHashMap<>(cacheSize);
    }


    public StorageFileProvider(String path, int cacheSize) {
        this(new PFile(path), cacheSize);
    }

    public StorageFileProvider(PFile initialDirectory, int cacheSize) {
        mInitialDirectory = initialDirectory;
        mPFileCache = new LimitedHashMap<>(cacheSize);
    }

    public StorageFileProvider() {
        this(getDefaultDirectory(), 10);
    }

    public static String getDefaultDirectoryPath() {
        return getDefaultDirectory().getPath();
    }

    public static PFile getDefaultDirectory() {
        return new PFile(Environment.getExternalStorageDirectory(), Pref.getScriptDirPath());
    }

    public static StorageFileProvider getDefault() {
        return DEFAULT_PROVIDER;
    }

    public static StorageFileProvider getExternalStorageProvider() {
        if (externalStorageProvider == null) {
            externalStorageProvider = new StorageFileProvider(Environment.getExternalStorageDirectory().getPath(), 5);
        }
        return externalStorageProvider;
    }

    public void notifyDirectoryChanged(PFile directory) {
        clearCache(directory);
        mDirectoryEventBus.post(new DirectoryChangeEvent(directory));
    }

    public void notifyFileChanged(PFile dir, PFile oldFile, PFile newFile) {
        List<PFile> files = getFilesFromCache(dir);
        if (files == null)
            return;
        int i = files.indexOf(oldFile);
        if (i >= 0) {
            files.set(i, newFile);
            mDirectoryEventBus.post(new DirectoryChangeEvent(dir, CHANGE, oldFile, newFile));
        }
    }


    public void notifyFileRemoved(PFile dir, PFile file) {
        List<PFile> files = getFilesFromCache(dir);
        if (files == null)
            return;
        if (files.remove(file)) {
            mDirectoryEventBus.post(new DirectoryChangeEvent(dir, REMOVE, file));
        }
    }

    public void notifyFileCreated(PFile dir, PFile file) {
        List<PFile> files = getFilesFromCache(dir);
        if (files == null)
            return;
        files.add(0, file);
        mDirectoryEventBus.post(new DirectoryChangeEvent(dir, CREATE, file));
    }

    public void notifyStoragePermissionGranted() {
        mPFileCache.clear();
        mDirectoryEventBus.post(new DirectoryChangeEvent(getInitialDirectory()));
    }

    @SuppressWarnings("unchecked")
    public void refreshAll() {
        Map<String, PFile> files = (Map<String, PFile>) mPFileCache.clone();
        mPFileCache.clear();
        mDirectoryEventBus.post(new DirectoryChangeEvent(getInitialDirectory()));
        for (Map.Entry<String, PFile> file : files.entrySet()) {
            mDirectoryEventBus.post(new DirectoryChangeEvent(new PFile(file.getKey())));
        }
    }

    public PFile getInitialDirectory() {
        if (mInitialDirectory == INITIAL_DIRECTORY_DEFAULT) {
            return getDefaultDirectory();
        }
        return mInitialDirectory;
    }

    public Observable<PFile> getInitialDirectoryFiles() {
        return getDirectoryFiles(getInitialDirectory());
    }

    public Observable<PFile> getDirectoryFiles(PFile directory) {
        List<PFile> PFiles = getFilesFromCache(directory);
        if (PFiles == null) {
            return listFiles(directory);
        }
        return Observable.fromIterable(PFiles);
    }

    private void clearCache(PFile directory) {
        mPFileCache.remove(directory.getPath());
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
                    mPFileCache.put(dir.getPath(), new ArrayList<>(Arrays.asList(files)));
                    return Observable.fromArray(files);
                });
    }

    private List<PFile> getFilesFromCache(PFile directory) {
        return mPFileCache.get(directory.getPath());
    }


    public void registerDirectoryChangeListener(Object subscriber) {
        if (!mDirectoryEventBus.isRegistered(subscriber))
            mDirectoryEventBus.register(subscriber);
    }

    public void unregisterDirectoryChangeListener(Object subscriber) {
        mDirectoryEventBus.unregister(subscriber);
    }

}
