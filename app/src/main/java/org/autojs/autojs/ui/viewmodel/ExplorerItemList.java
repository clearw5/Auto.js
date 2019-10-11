package org.autojs.autojs.ui.viewmodel;

import android.content.SharedPreferences;

import org.autojs.autojs.model.explorer.ExplorerDirPage;
import org.autojs.autojs.model.explorer.ExplorerItem;
import org.autojs.autojs.model.explorer.ExplorerPage;
import org.autojs.autojs.model.explorer.ExplorerSorter;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Stardust on 2017/9/30.
 */

public class ExplorerItemList {

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
    private ArrayList<ExplorerItem> mItems = new ArrayList<>();
    private ArrayList<ExplorerPage> mItemGroups = new ArrayList<>();


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

    private Comparator<ExplorerItem> getComparator(int sortType) {
        switch (sortType) {
            case SORT_TYPE_NAME:
                return ExplorerSorter.NAME;
            case SORT_TYPE_DATE:
                return ExplorerSorter.DATE;
            case SORT_TYPE_SIZE:
                return ExplorerSorter.SIZE;
            case SORT_TYPE_TYPE:
                return ExplorerSorter.TYPE;
        }
        throw new IllegalArgumentException("unknown type " + sortType);
    }

    public int groupCount() {
        return mItemGroups.size();
    }

    public int itemCount() {
        return mItems.size();
    }

    public void clear() {
        mItems.clear();
        mItemGroups.clear();
    }

    public void add(ExplorerItem item) {
        if (item instanceof ExplorerPage) {
            mItemGroups.add((ExplorerPage) item);
        } else {
            mItems.add(item);
        }
    }

    public void insertAtFront(ExplorerItem item) {
        if (item instanceof ExplorerPage) {
            mItemGroups.add(0, (ExplorerPage) item);
        } else {
            mItems.add(0, item);
        }
    }


    public int remove(ExplorerItem item) {
        if (item instanceof ExplorerPage) {
            return remove(mItemGroups, item);
        } else {
            return remove(mItems, item);
        }
    }

    public int update(ExplorerItem oldItem, ExplorerItem newItem) {
        if (oldItem instanceof ExplorerPage) {
            return update(mItemGroups, (ExplorerPage) oldItem, (ExplorerPage) newItem);
        } else {
            return update(mItems, oldItem, newItem);
        }
    }

    private <T> int update(ArrayList<T> list, T oldItem, T newItem) {
        int i = list.indexOf(oldItem);
        if (i >= 0) {
            list.set(i, newItem);
        }
        return i;
    }

    private <T> int remove(ArrayList<?> list, T o) {
        int i = list.indexOf(o);
        if (i >= 0) {
            list.remove(i);
        }
        return i;
    }

    public ExplorerPage getItemGroup(int i) {
        return mItemGroups.get(i);
    }

    public ExplorerItem getItem(int i) {
        return mItems.get(i);
    }

    public int count() {
        return mItems.size() + mItemGroups.size();
    }

    public void sortItemGroup(int sortType) {
        mSortConfig.mDirSortType = sortType;
        ExplorerSorter.sort(mItemGroups, getComparator(sortType), mSortConfig.mDirSortedAscending);
    }

    public void sortFile(int sortType) {
        mSortConfig.mFileSortType = sortType;
        ExplorerSorter.sort(mItems, getComparator(sortType), mSortConfig.mFileSortedAscending);
    }

    public void sort() {
        ExplorerSorter.sort(mItemGroups, getComparator(mSortConfig.mDirSortType), mSortConfig.mDirSortedAscending);
        ExplorerSorter.sort(mItems, getComparator(mSortConfig.mFileSortType), mSortConfig.mFileSortedAscending);
    }

    public SortConfig getSortConfig() {
        return mSortConfig;
    }

    public void setSortConfig(SortConfig sortConfig) {
        mSortConfig = sortConfig;
    }

    public ExplorerItemList cloneConfig() {
        ExplorerItemList list = new ExplorerItemList();
        list.mSortConfig = mSortConfig;
        return list;
    }
}
