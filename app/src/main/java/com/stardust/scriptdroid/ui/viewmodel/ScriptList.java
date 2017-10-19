package com.stardust.scriptdroid.ui.viewmodel;

import com.stardust.scriptdroid.script.ScriptFile;
import com.stardust.util.FileSorter;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Stardust on 2017/9/30.
 */

public class ScriptList {

    public static final int SORT_TYPE_NAME = 0x10;
    public static final int SORT_TYPE_TYPE = 0x20;
    public static final int SORT_TYPE_SIZE = 0x30;
    public static final int SORT_TYPE_DATE = 0x40;

    private ArrayList<ScriptFile> mScriptFiles = new ArrayList<>();
    private ArrayList<ScriptFile> mDirectories = new ArrayList<>();
    private int mDirSortType = SORT_TYPE_NAME;
    private boolean mDirSortedAscending;
    private boolean mFileSortedAscending;
    private int mFileSortType = SORT_TYPE_NAME;

    public boolean isDirSortedAscending() {
        return mDirSortedAscending;
    }

    public boolean isFileSortedAscending() {
        return mFileSortedAscending;
    }

    public int getDirSortType() {
        return mDirSortType;
    }

    public int getFileSortType() {
        return mFileSortType;
    }

    public void setDirSortedAscending(boolean dirSortedAscending) {
        mDirSortedAscending = dirSortedAscending;
    }

    public void setFileSortedAscending(boolean fileSortedAscending) {
        mFileSortedAscending = fileSortedAscending;
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
        mDirSortType = sortType;
        FileSorter.sort(mDirectories, getComparator(sortType), mDirSortedAscending);
    }

    public void sortFile(int sortType) {
        mFileSortType = sortType;
        FileSorter.sort(mScriptFiles, getComparator(sortType), mFileSortedAscending);
    }

    public void sort() {
        FileSorter.sort(mDirectories, getComparator(mDirSortType), mDirSortedAscending);
        FileSorter.sort(mScriptFiles, getComparator(mFileSortType), mFileSortedAscending);
    }
}
