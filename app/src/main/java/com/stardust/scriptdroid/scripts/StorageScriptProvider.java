package com.stardust.scriptdroid.scripts;

import android.os.Environment;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;
import com.stardust.util.FileSorter;
import com.stardust.util.LimitedHashMap;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Stardust on 2017/3/31.
 */

public class StorageScriptProvider {


    public static class DirectoryChangeEvent {

        public ScriptFile directory;

        public DirectoryChangeEvent(ScriptFile directory) {
            this.directory = directory;
        }
    }

    public static final String DEFAULT_DIRECTORY_PATH = Environment.getExternalStorageDirectory() + App.getApp().getString(R.string.folder_name);
    public static final ScriptFile DEFAULT_DIRECTORY = new ScriptFile(DEFAULT_DIRECTORY_PATH);



    private static StorageScriptProvider instance = new StorageScriptProvider();

    public static StorageScriptProvider getDefault() {
        return instance;
    }

    private EventBus mDirectoryEventBus = new EventBus();
    private LimitedHashMap<String, ScriptFile[]> mScriptFileCache;
    private ScriptFile mInitialDirectory;
    private ScriptFile[] mInitialDirectoryScriptFiles;

    public StorageScriptProvider(String path, int cacheSize) {
        this(new ScriptFile(path), cacheSize);
    }

    public StorageScriptProvider(ScriptFile initialDirectory, int cacheSize) {
        mInitialDirectory = initialDirectory;
        mInitialDirectoryScriptFiles = getInitialDirectoryScriptFilesInner();
        mScriptFileCache = new LimitedHashMap<>(cacheSize);
    }

    public StorageScriptProvider() {
        this(DEFAULT_DIRECTORY, 10);
    }

    public void notifyDirectoryChanged(ScriptFile directory) {
        if (directory.equals(mInitialDirectory)) {
            mInitialDirectoryScriptFiles = getInitialDirectoryScriptFilesInner();
        } else {
            clearCache(directory);
        }
        mDirectoryEventBus.post(new DirectoryChangeEvent(directory));
    }

    public void notifyStoragePermissionGranted() {
        mScriptFileCache.clear();
        mInitialDirectoryScriptFiles = getInitialDirectoryScriptFilesInner();
        mDirectoryEventBus.post(new DirectoryChangeEvent(mInitialDirectory));
    }

    public ScriptFile getInitialDirectory() {
        return mInitialDirectory;
    }

    public ScriptFile[] getInitialDirectoryScriptFiles() {
        return mInitialDirectoryScriptFiles;
    }

    private ScriptFile[] getInitialDirectoryScriptFilesInner() {
        return listAndSortFiles(mInitialDirectory);
    }

    public ScriptFile[] getDirectoryScriptFiles(ScriptFile directory) {
        if (directory.equals(mInitialDirectory)) {
            return mInitialDirectoryScriptFiles;
        }
        ScriptFile[] scriptFiles = getScriptFilesFromCache(directory);
        if (scriptFiles == null) {
            scriptFiles = getScriptFiles(directory);
        }
        return scriptFiles;
    }

    private void clearCache(ScriptFile directory) {
        mScriptFileCache.remove(directory.getPath());
    }


    private ScriptFile[] getScriptFiles(ScriptFile directory) {
        ScriptFile[] scriptFiles = listAndSortFiles(directory);
        mScriptFileCache.put(directory.getPath(), scriptFiles);
        return scriptFiles;
    }

    private ScriptFile[] listAndSortFiles(ScriptFile directory) {
        ScriptFile[] scriptFiles = directory.listFiles();
        if (scriptFiles == null)
            scriptFiles = new ScriptFile[0];
        else
            FileSorter.sort(scriptFiles);
        return scriptFiles;
    }

    private ScriptFile[] getScriptFilesFromCache(ScriptFile directory) {
        return mScriptFileCache.get(directory.getPath());
    }


    public void registerDirectoryChangeListener(Object subscriber) {
        mDirectoryEventBus.register(subscriber);
    }

    public void unregisterDirectoryChangeListener(Object subscriber) {
        mDirectoryEventBus.unregister(subscriber);
    }

}
