package com.stardust.util;

import org.junit.Assert;
import org.junit.Test;

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

    public static class TestSuite {

        @Test
        public void testEngFileSort() {
            File file1 = new File("d:/a.txt");
            File file2 = new File("e:/b.txt");
            File file3 = new File("c:/c.txt");
            File[] files = {file2, file3, file1};
            sort(files);
            Assert.assertArrayEquals(new File[]{file1, file2, file3}, files);
        }

        @Test
        public void testEngFileSortWithDirectory() {
            File dir1 = new File("d:/");
            File dir2 = new File("e:/");
            File file1 = new File("e:/a.txt");
            File file2 = new File("e:/b.txt");
            File file3 = new File("d:/c.txt");
            Assert.assertTrue(dir1.isDirectory());
            Assert.assertTrue(dir2.isDirectory());
            File[] files = {file2, file3, dir1, file1, dir2};
            sort(files);
            Assert.assertArrayEquals(new File[]{dir1, dir2, file1, file2, file3}, files);
        }

        @Test
        public void testCnFileSort() {
            File file1 = new File("a.txt");
            File file2 = new File("b.txt");
            File file3 = new File("啊.txt");
            File file4 = new File("啊啊.txt");
            File[] files = {file2, file4, file3, file1};
            sort(files);
            Assert.assertArrayEquals(new File[]{file1, file2, file3, file4}, files);
        }

        @Test
        public void testCnFileSortWithDirectory() {
            File dir1 = new File("d:/整理/");
            File dir2 = new File("d:/迅雷下载/");
            File file1 = new File("d:/整理/a.txt");
            File file2 = new File("啊.txt");
            File file3 = new File("啊啊.txt");
            Assert.assertTrue(dir1.isDirectory());
            Assert.assertTrue(dir2.isDirectory());
            File[] files = {file2, file3, dir1, file1, dir2};
            sort(files);
            Assert.assertArrayEquals(new File[]{dir1, dir2, file1, file2, file3}, files);
        }
    }

}
