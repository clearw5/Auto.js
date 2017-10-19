package com.stardust.scriptdroid.script;

import android.os.Environment;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;
import com.stardust.util.LimitedHashMap;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by Stardust on 2017/3/31.
 */

public class StorageFileProvider {

    public static final int REMOVE = 0;
    public static final int CREATE = 1;
    public static final int CHANGE = 2;
    public static final int ALL = 3;

    public static class DirectoryChangeEvent {


        private final ScriptFile mDir;
        private final int mChange;
        private final ScriptFile mFile;
        private final ScriptFile mNewFile;

        public DirectoryChangeEvent(ScriptFile dir, int change, ScriptFile file) {
            this(dir, change, file, file);
        }

        public DirectoryChangeEvent(ScriptFile directory) {
            this(directory, ALL, null);
        }

        public DirectoryChangeEvent(ScriptFile dir, int change, ScriptFile oldFile, ScriptFile newFile) {
            mDir = dir;
            mChange = change;
            mFile = oldFile;
            mNewFile = newFile;
        }

        public ScriptFile getDir() {
            return mDir;
        }

        public int getChange() {
            return mChange;
        }

        public ScriptFile getFile() {
            return mFile;
        }

        public ScriptFile getNewFile() {
            return mNewFile;
        }
    }

    public static final String DEFAULT_DIRECTORY_PATH = Environment.getExternalStorageDirectory() + App.getApp().getString(R.string.folder_name);
    public static final ScriptFile DEFAULT_DIRECTORY = new ScriptFile(DEFAULT_DIRECTORY_PATH);


    private static StorageFileProvider defaultProvider = new StorageFileProvider();

    public static StorageFileProvider getDefault() {
        return defaultProvider;
    }

    private static StorageFileProvider externalStorageProvider;

    public static StorageFileProvider getExternalStorageProvider() {
        if (externalStorageProvider == null) {
            externalStorageProvider = new StorageFileProvider(Environment.getExternalStorageDirectory().getPath(), 5);
        }
        return externalStorageProvider;
    }

    private EventBus mDirectoryEventBus = new EventBus();
    private LimitedHashMap<String, List<ScriptFile>> mScriptFileCache;
    private ScriptFile mInitialDirectory;
    private ScriptFile[] mInitialDirectoryScriptFiles;

    public StorageFileProvider(String path, int cacheSize) {
        this(new ScriptFile(path), cacheSize);
    }

    public StorageFileProvider(ScriptFile initialDirectory, int cacheSize) {
        mInitialDirectory = initialDirectory;
        mScriptFileCache = new LimitedHashMap<>(cacheSize);
    }

    public StorageFileProvider() {
        this(DEFAULT_DIRECTORY, 10);
    }

    public void notifyDirectoryChanged(ScriptFile directory) {
        clearCache(directory);
        mDirectoryEventBus.post(new DirectoryChangeEvent(directory));
    }

    public void notifyFileChanged(ScriptFile dir, ScriptFile oldFile, ScriptFile newFile) {
        List<ScriptFile> files = getScriptFilesFromCache(dir);
        if (files == null)
            return;
        int i = files.indexOf(oldFile);
        if (i >= 0) {
            files.set(i, newFile);
            mDirectoryEventBus.post(new DirectoryChangeEvent(dir, CHANGE, oldFile, newFile));
        }
    }


    public void notifyFileRemoved(ScriptFile dir, ScriptFile file) {
        List<ScriptFile> files = getScriptFilesFromCache(dir);
        if (files == null)
            return;
        if (files.remove(file)) {
            mDirectoryEventBus.post(new DirectoryChangeEvent(dir, REMOVE, file));
        }
    }

    public void notifyFileCreated(ScriptFile dir, ScriptFile file) {
        List<ScriptFile> files = getScriptFilesFromCache(dir);
        if (files == null)
            return;
        files.add(0, file);
        mDirectoryEventBus.post(new DirectoryChangeEvent(dir, CREATE, file));
    }

    public void notifyStoragePermissionGranted() {
        mScriptFileCache.clear();
        mInitialDirectoryScriptFiles = null;
        mDirectoryEventBus.post(new DirectoryChangeEvent(mInitialDirectory));
    }

    @SuppressWarnings("unchecked")
    public void refreshAll() {
        Map<String, ScriptFile> files = (Map<String, ScriptFile>) mScriptFileCache.clone();
        mScriptFileCache.clear();
        mInitialDirectoryScriptFiles = null;
        mDirectoryEventBus.post(new DirectoryChangeEvent(mInitialDirectory));
        for (Map.Entry<String, ScriptFile> file : files.entrySet()) {
            mDirectoryEventBus.post(new DirectoryChangeEvent(new ScriptFile(file.getKey())));
        }
    }

    public ScriptFile getInitialDirectory() {
        return mInitialDirectory;
    }

    public Observable<ScriptFile> getInitialDirectoryScriptFiles() {
        return getDirectoryScriptFiles(mInitialDirectory);
    }

    public Observable<ScriptFile> getDirectoryScriptFiles(ScriptFile directory) {
        List<ScriptFile> scriptFiles = getScriptFilesFromCache(directory);
        if (scriptFiles == null) {
            return listFiles(directory);
        }
        return Observable.fromIterable(scriptFiles);
    }

    private void clearCache(ScriptFile directory) {
        mScriptFileCache.remove(directory.getPath());
    }


    private Observable<ScriptFile> listFiles(ScriptFile directory) {
        return Observable.just(directory)
                .flatMap(new Function<ScriptFile, ObservableSource<ScriptFile>>() {
                    @Override
                    public ObservableSource<ScriptFile> apply(@NonNull ScriptFile dir) throws Exception {
                        ScriptFile[] scriptFiles = dir.listFiles();
                        if (scriptFiles == null) {
                            return Observable.empty();
                        }
                        mScriptFileCache.put(dir.getPath(), new ArrayList<>(Arrays.asList(scriptFiles)));
                        return Observable.fromArray(scriptFiles);
                    }
                });
    }

    private List<ScriptFile> getScriptFilesFromCache(ScriptFile directory) {
        return mScriptFileCache.get(directory.getPath());
    }


    public void registerDirectoryChangeListener(Object subscriber) {
        if (!mDirectoryEventBus.isRegistered(subscriber))
            mDirectoryEventBus.register(subscriber);
    }

    public void unregisterDirectoryChangeListener(Object subscriber) {
        mDirectoryEventBus.unregister(subscriber);
    }

}
