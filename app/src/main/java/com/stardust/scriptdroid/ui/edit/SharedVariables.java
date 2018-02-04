package com.stardust.scriptdroid.ui.edit;

import android.util.SparseArray;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Stardust on 2018/2/4.
 */

public class SharedVariables {

    private static SparseArray<Object> sSharedVariables = new SparseArray<>();
    private static AtomicInteger sMaxId = new AtomicInteger();


    public static int put(Object value) {
        int id = sMaxId.getAndIncrement();
        sSharedVariables.put(id, value);
        return id;
    }

    @SuppressWarnings("unchecked")
    public static <T> T remove(int id) {
        Object o = sSharedVariables.get(id);
        if (o != null)
            sSharedVariables.remove(id);
        return (T) o;
    }

}
