package org.autojs.autojs.model.explorer;

import java.text.Collator;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ExplorerSorter {

    private static final Collator collator = Collator.getInstance();

    public static final Comparator<ExplorerItem> NAME = (o1, o2) -> collator.compare(o2.getName(), o1.getName());

    public static final Comparator<ExplorerItem> DATE = (o1, o2) -> Long.compare(o1.lastModified(), o2.lastModified());

    public static final Comparator<ExplorerItem> TYPE = (o1, o2) -> o2.getType().compareTo(o1.getType());

    public static final Comparator<ExplorerItem> SIZE = (o1, o2) -> Long.compare(o2.getSize(), o1.getSize());

    public static void sort(ExplorerItem[] items, final Comparator<ExplorerItem> comparator, boolean ascending) {
        if (ascending) {
            Arrays.sort(items, comparator);
        } else {
            Arrays.sort(items, (o1, o2) -> comparator.compare(o2, o1));
        }
    }

    public static void sort(ExplorerItem[] items, Comparator<ExplorerItem> comparator) {
        sort(items, comparator, true);
    }

    public static void sort(List<? extends ExplorerItem> items, final Comparator<ExplorerItem> comparator, boolean ascending) {
        if (ascending) {
            Collections.sort(items, comparator);
        } else {
            Collections.sort(items, (Comparator<ExplorerItem>) (o1, o2) -> comparator.compare(o2, o1));
        }
    }

    public static void sort(List<? extends ExplorerItem> items, Comparator<ExplorerItem> comparator) {
        sort(items, comparator, true);
    }
}
