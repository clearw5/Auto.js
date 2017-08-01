package com.stardust.util;

import java.util.List;

/**
 * Created by Stardust on 2017/5/8.
 */

public class ArrayUtils {


    public static Integer[] box(int[] array) {
        Integer[] box = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            box[i] = array[i];
        }
        return box;
    }


    public static int[] unbox(Integer[] array) {
        int[] unbox = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            unbox[i] = array[i];
        }
        return unbox;
    }

    public static String[] toStringArray(List<?> list) {
        int i = 0;
        String[] str = new String[list.size()];
        for (Object o : list) {
            str[i] = o == null ? null : o.toString();
        }
        return str;
    }
}
