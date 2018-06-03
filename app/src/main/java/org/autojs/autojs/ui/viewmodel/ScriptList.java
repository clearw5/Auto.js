package org.autojs.autojs.ui.viewmodel;

import android.content.SharedPreferences;

import org.autojs.autojs.model.script.ScriptFile;
import org.autojs.autojs.ui.main.scripts.ScriptListView;
import com.stardust.util.FileSorter;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Stardust on 2017/9/30.
 */

public class ScriptList {

    public static class SortConfig {

        private static final String CLASS_NAME = "org.autojs.autojs.ui.viewmodel.ScriptList.SortConfig";

        private int mDirSortType = SORT_TYPE_NAME;
        private boolean mDirSortedAscending;
        private boolean mFileSortedAscending;
        private int mFileSortType = SORT_TYPE_NAME;

        public int getDirSortType() {
            return mDirSortType;
        }

        public void setDirSortType(int dirSortType) {
            mDirSortType = dirSortType;
        }

        public boolean isDirSortedAscending() {
            return mDirSortedAscending;
        }

        public void setDirSortedAscending(boolean dirSortedAscending) {
            mDirSortedAscending = dirSortedAscending;
        }

        public boolean isFileSortedAscending() {
            return mFileSortedAscending;
        }

        public void setFileSortedAscending(boolean fileSortedAscending) {
            mFileSortedAscending = fileSortedAscending;
        }

        public int getFileSortType() {
            return mFileSortType;
        }

        public void setFileSortType(int fileSortType) {
            mFileSortType = fileSortType;
        }

        public void saveInto(SharedPreferences preferences) {
            preferences.edit()
                    .putInt(CLASS_NAME + "." + "file_sort_type", mFileSortType)
                    .putInt(CLASS_NAME + "." + "dir_sort_type", mDirSortType)
                    .putBoolean(CLASS_NAME + "." + "file_ascending", mFileSortedAscending)
                    .putBoolean(CLASS_NAME + "." + "dir_ascending", mDirSortedAscending)
                    .apply();

        }

        public static SortConfig from(SharedPreferences preferences) {
            SortConfig config = new SortConfig();
            config.setDirSortedAscending(preferences.getBoolean(CLASS_NAME + "." + "dir_ascending", false));
            config.setFileSortedAscending(preferences.getBoolean(CLASS_NAME + "." + "file_ascending", false));
            config.setDirSortType(preferences.getInt(CLASS_NAME + "." + "dir_sort_type", SORT_TYPE_NAME));
            config.setFileSortType(preferences.getInt(CLASS_NAME + "." + "file_sort_type", SORT_TYPE_NAME));
            return config;
        }
    }

    public static final int SORT_TYPE_NAME = 0x10;
    public static final int SORT_TYPE_TYPE = 0x20;
    public static final int SORT_TYPE_SIZE = 0x30;
    public static final int SORT_TYPE_DATE = 0x40;

    private SortConfig mSortConfig = new SortConfig();
    private ArrayList<ScriptFile> mScriptFiles = new ArrayList<>();
    private ArrayList<ScriptFile> mDirectories = new ArrayList<>();


    public boolean isDirSortedAscending() {
        return mSortConfig.mDirSortedAscending;
    }

    public boolean isFileSortedAscending() {
        return mSortConfig.mFileSortedAscending;
    }

    public int getDirSortType() {
        return mSortConfig.mDirSortType;
    }

    public int getFileSortType() {
        return mSortConfig.mFileSortType;
    }

    public void setDirSortedAscending(boolean dirSortedAscending) {
        mSortConfig.mDirSortedAscending = dirSortedAscending;
    }

    public void setFileSortedAscending(boolean fileSortedAscending) {
        mSortConfig.mFileSortedAscending = fileSortedAscending;
    }

    private Comparator<File> getComparator(int sortType) {
        switch (sortType) {
            case SORT_TYPE_NAME:
                return FileSorter.NAME;
            case SORT_TYPE_DATE:
                return FileSorter.DATE;
            case SORT_TYPE_SIZE:
                return FileSorter.SIZE;
            case SORT_TYPE_TYPE:
                return FileSorter.TYPE;
        }
        throw new IllegalArgumentException("unknown type " + sortType);
    }

    public int directoryCount() {
        return mDirectories.size();
    }

    public int fileCount() {
        return mScriptFiles.size();
    }

    public void clear() {
        mScriptFiles.clear();
        mDirectories.clear();
    }

    public void add(ScriptFile file) {
        if (file.isDirectory()) {
            mDirectories.add(file);
        } else {
            mScriptFiles.add(file);
        }
    }

    public ScriptFile getDir(int i) {
        return mDirectories.get(i);
    }

    public ScriptFile getFile(int i) {
        return mScriptFiles.get(i);
    }

    public int count() {
        return mScriptFiles.size() + mDirectories.size();
    }

    public void sortDir(int sortType) {
        mSortConfig.mDirSortType = sortType;
        FileSorter.sort(mDirectories, getComparator(sortType), mSortConfig.mDirSortedAscending);
    }

    public void sortFile(int sortType) {
        mSortConfig.mFileSortType = sortType;
        FileSorter.sort(mScriptFiles, getComparator(sortType), mSortConfig.mFileSortedAscending);
    }

    public void sort() {
        FileSorter.sort(mDirectories, getComparator(mSortConfig.mDirSortType), mSortConfig.mDirSortedAscending);
        FileSorter.sort(mScriptFiles, getComparator(mSortConfig.mFileSortType), mSortConfig.mFileSortedAscending);
    }

    public SortConfig getSortConfig() {
        return mSortConfig;
    }

    public void setSortConfig(SortConfig sortConfig) {
        mSortConfig = sortConfig;
    }

    public ScriptList cloneConfig() {
        ScriptList list = new ScriptList();
        list.mSortConfig = mSortConfig;
        return list;
    }
}
