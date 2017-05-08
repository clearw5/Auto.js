package com.stardust.util;

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
}
