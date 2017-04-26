package com.stardust.mi666.ocr;

import android.graphics.Bitmap;
import android.util.Log;

import com.stardust.scriptdroid.tool.BitmapTool;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stardust on 2017/4/23.
 */

public class TemplateMatching {

    private static final String LOG_TAG = "TemplateMatching";

    private int mBlockSize;
    private ColorDetector mColorDetector;


    public TemplateMatching(int blockSize, ColorDetector colorDetector) {
        mBlockSize = blockSize;
        mColorDetector = colorDetector;
    }

    public int[][] getFeature(Bitmap bitmap) {
        int w = bitmap.getWidth() / mBlockSize;
        int h = bitmap.getHeight() / mBlockSize;
        int[][] mat = new int[w][h];
        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                if (mColorDetector.isCharPixel(bitmap.getPixel(i, j))) {
                    mat[i * w / bitmap.getWidth()][j * h / bitmap.getHeight()]++;
                }
            }
        }
        return mat;
    }

    public int distance(int[][] f, int[][] template, int offsetX, int offsetY) {
        int sum = 0;
        for (int i = 0; i < template.length; i++) {
            for (int j = 0; j < template[i].length; j++) {
                if (i >= f.length || j >= f[i].length) {
                    sum += template[i].length - j;
                    break;
                }
                sum += Math.abs(template[i][j] - f[i + offsetX][j + offsetY]);

            }
        }
        return sum;
    }

    public int minDistance(int[][] f, int[][] template) {
        int min = Integer.MAX_VALUE;
        int i = 0;
        while (i < template.length && i + template.length - 1 < f.length) {
            int j = 0;
            while (j < template[i].length && j + template[i].length - 1 < f[i].length) {
                int d = distance(f, template, i, j);
                if (d < min) {
                    min = d;
                }
                j++;
            }
            i++;
        }
        return min;
    }

    public static class TemplateMatchingOCR implements OCR {

        private Map<Character, int[][]> mChars = new HashMap<>();
        private TemplateMatching mTemplateMatching;
        private SimpleTextDetector mSimpleTextDetector;

        public TemplateMatchingOCR(int blockSize, ColorDetector colorDetector) {
            mTemplateMatching = new TemplateMatching(blockSize, colorDetector);
            mSimpleTextDetector = new SimpleTextDetector(colorDetector);
        }

        @Override
        public void addChar(char ch, Bitmap bitmap) {
            mChars.put(ch, mTemplateMatching.getFeature(mSimpleTextDetector.detect(bitmap)));
        }

        @Override
        public char detect(Bitmap bitmap) {
            int min = Integer.MAX_VALUE;
            char ch = ' ';
            for (Map.Entry<Character, int[][]> entry : mChars.entrySet()) {
                int[][] f = mTemplateMatching.getFeature(BitmapTool.scaleBitmap(bitmap, entry.getValue().length, entry.getValue()[0].length));
                int d = mTemplateMatching.distance(f, entry.getValue(), 0, 0);
                Log.d(LOG_TAG, "char: " + entry.getKey() + " distance: " + d);
                if (d < min) {
                    if (entry.getKey() == '1' && d > 30) {
                        continue;
                    }
                    min = d;
                    ch = entry.getKey();
                }
            }
            return ch;
        }
    }
}
