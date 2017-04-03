package com.stardust.util;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by Stardust on 2017/3/31.
 */

public class FileSorter {

    public static void sort(File[] files) {
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.isDirectory() != o2.isDirectory())
                    return o1.isDirectory() ? Integer.MIN_VALUE : Integer.MAX_VALUE;
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

}
