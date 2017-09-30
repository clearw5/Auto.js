package com.stardust.scriptdroid.script;

import android.os.Environment;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;
import com.stardust.util.FileSorter;
import com.stardust.util.LimitedHashMap;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Stardust on 2017/3/31.
 */

public class StorageFileProvider {


    public static class DirectoryChangeEvent {

        public ScriptFile directory;

        public DirectoryChangeEvent(ScriptFile directory) {
            this.directory = directory;
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
    private LimitedHashMap<String, ScriptFile[]> mScriptFileCache;
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
        ScriptFile[] scriptFiles = getScriptFilesFromCache(directory);
        if (scriptFiles == null) {
            return listAndSortFiles(directory);
        }
        return Observable.fromArray(scriptFiles);
    }

    private void clearCache(ScriptFile directory) {
        mScriptFileCache.remove(directory.getPath());
    }


    private Observable<ScriptFile> listAndSortFiles(ScriptFile directory) {
        return Observable.just(directory)
                .observeOn(Schedulers.computation())
                .flatMap(new Function<ScriptFile, Observable<ScriptFile>>() {


                    @Override
                    public Observable<ScriptFile> apply(@NonNull ScriptFile dir) throws Exception {
                        ScriptFile[] scriptFiles = dir.listFiles();
                        if (scriptFiles == null) {
                            return Observable.empty();
                        } else {
                            FileSorter.sort(scriptFiles, FileSorter.NAME);
                            mScriptFileCache.put(dir.getPath(), scriptFiles);
                            return Observable.fromArray(scriptFiles);
                        }
                    }
                });
    }

    private ScriptFile[] getScriptFilesFromCache(ScriptFile directory) {
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
