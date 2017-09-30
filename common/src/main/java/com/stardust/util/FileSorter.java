package com.stardust.util;

import com.stardust.pio.PFile;

import java.io.File;
import java.text.Collator;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Created by Stardust on 2017/3/31.
 */

public class FileSorter {

    public static final Comparator<File> NAME = new Comparator<File>() {
        final Collator collator = Collator.getInstance();

        @Override
        public int compare(File o1, File o2) {
            if (o1.isDirectory() != o2.isDirectory())
                return o1.isDirectory() ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            return -collator.compare(o1.getName(), o2.getName());
        }
    };

    public static final Comparator<File> DATE = new Comparator<File>() {
        @Override
        public int compare(File o1, File o2) {
            return o1.lastModified() == o2.lastModified() ? 0 :
                    o1.lastModified() > o2.lastModified() ? 1 : -1;
        }
    };

    public static final Comparator<File> TYPE = new Comparator<File>() {
        @Override
        public int compare(File o1, File o2) {
            return -PFile.getExtension(o1.getName()).compareTo(PFile.getExtension(o2.getName()));
        }
    };

    public static final Comparator<File> SIZE = new Comparator<File>() {
        @Override
        public int compare(File o1, File o2) {
            return o1.length() == o2.length() ? 0 :
                    o1.length() < o2.length() ? 1 : -1;
        }
    };

    public static void sort(File[] files, final Comparator<File> comparator, boolean ascending) {
        if (ascending) {
            Arrays.sort(files, comparator);
        } else {
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return comparator.compare(o2, o1);
                }
            });
        }
    }

    public static void sort(File[] files, Comparator<File> comparator) {
        sort(files, comparator, true);
    }

    public static void sort(List<? extends File> files, final Comparator<File> comparator, boolean ascending) {
        if (ascending) {
            Collections.sort(files, comparator);
        } else {
            Collections.sort(files, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return comparator.compare(o2, o1);
                }
            });
        }
    }

    public static void sort(List<? extends File> files, Comparator<File> comparator) {
        sort(files, comparator, true);
    }

}
