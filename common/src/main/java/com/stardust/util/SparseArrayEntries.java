package com.stardust.util;

import androidx.annotation.NonNull;
import android.util.SparseArray;

/**
 * Created by Stardust on 2017/1/26.
 */

public class SparseArrayEntries<E> {

    private final SparseArray<E> mSparseArray = new SparseArray<>();

    public SparseArrayEntries<E> entry(int key, E value) {
        mSparseArray.put(key, value);
        return this;
    }

    @NonNull
    public SparseArray<E> sparseArray() {
        return mSparseArray;
    }

}