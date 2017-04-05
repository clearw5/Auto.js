package com.stardust.util;

import java.io.File;
import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

/**
 * Created by Stardust on 2017/3/31.
 */

public class FileSorter {

    public static void sort(File[] files) {
        final Collator collator = Collator.getInstance();
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.isDirectory() != o2.isDirectory())
                    return o1.isDirectory() ? Integer.MIN_VALUE : Integer.MAX_VALUE;
                return collator.compare(o1.getName(), o2.getName());
            }
        });
    }

}
