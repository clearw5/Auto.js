package com.stardust.scriptdroid.scripts;

import com.stardust.scriptdroid.droid.script.file.ScriptFile;
import com.stardust.util.FileSorter;
import com.stardust.util.LimitedHashMap;

import org.greenrobot.eventbus.EventBus;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;

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

    private static StorageScriptProvider instance = new StorageScriptProvider();

    public static StorageScriptProvider getInstance() {
        return instance;
    }

    private EventBus mDirectoryEventBus = new EventBus();
    private LimitedHashMap<String, ScriptFile[]> mScriptFileCache = new LimitedHashMap<>(10);

    private ScriptFile[] mInitialDirectoryScriptFiles = listAndSortFiles(ScriptFile.DEFAULT_DIRECTORY);

    public void notifyDirectoryChanged(ScriptFile directory) {
        if (directory.equals(ScriptFile.DEFAULT_DIRECTORY)) {
            mInitialDirectoryScriptFiles = listAndSortFiles(ScriptFile.DEFAULT_DIRECTORY);
        } else {
            clearCache(directory);
        }
        mDirectoryEventBus.post(new DirectoryChangeEvent(directory));
    }

    public void notifyStoragePermissionGranted() {
        mScriptFileCache.clear();
        mInitialDirectoryScriptFiles = listAndSortFiles(ScriptFile.DEFAULT_DIRECTORY);
        mDirectoryEventBus.post(new DirectoryChangeEvent(ScriptFile.DEFAULT_DIRECTORY));
    }

    public ScriptFile[] getInitialDirectoryScriptFiles() {
        return mInitialDirectoryScriptFiles;
    }

    public ScriptFile[] getDirectoryScriptFiles(ScriptFile directory) {
        if (directory.equals(ScriptFile.DEFAULT_DIRECTORY)) {
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
        ScriptFile[] scriptFiles =listAndSortFiles(directory);
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
