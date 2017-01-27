package com.stardust.util.function;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/1/26.
 */

public class ListTool {

    @FunctionalInterface
    public interface ChildSupplier<T> {
        T getChild(int i);
    }

    public static <T> List<T> toList(ChildSupplier<T> supplier, int size) {
        ArrayList<T> arrayList = new ArrayList<T>(size);
        for (int i = 0; i < size - 1; i++) {
            arrayList.add(supplier.getChild(i));
        }
        return arrayList;
    }
}
