package com.stardust.scriptdroid.tool;

/**
 * Created by Stardust on 2017/4/23.
 */

import android.graphics.Bitmap;

import com.stardust.mi666.ocr.ColorDetector;
import com.stardust.mi666.ocr.SimpleTextDetector;

import java.util.HashMap;
import java.util.Map;

public class ImagePHash {

    private static final String LOG_TAG = "ImagePHash";

    private int width = 32;
    private int height;
    private int smallerWidth = 8;
    private int smallerHeight;

    public ImagePHash() {
        this(32, 8);
    }

    public ImagePHash(int width, int smallerWidth) {
        this(width, width, smallerWidth, smallerWidth);
    }

    public ImagePHash(int width, int height, int smallerWidth, int smallerHeight) {
        this.width = width;
        this.height = height;
        this.smallerWidth = smallerWidth;
        this.smallerHeight = smallerHeight;
        initCoefficients();
    }

    public int distance(boolean[] s1, boolean[] s2) {
        int counter = 0;
        for (int k = 0; k < s1.length; k++) {
            if (s1[k] != s2[k]) {
                counter++;
            }
        }
        return counter;
    }


    public boolean[] getHash(Bitmap img) {
        double[][] vals = new double[width][height];
        img = BitmapTool.scaleBitmap(img, width, height);

        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                vals[x][y] = getBlue(img, x, y);
            }
        }

        double[][] dctVals = applyDCT(vals);

        double total = 0;

        for (int x = 0; x < smallerWidth; x++) {
            for (int y = 0; y < smallerHeight; y++) {
                total += dctVals[x][y];
            }
        }
        total -= dctVals[0][0];

        double avg = total / (double) ((smallerWidth * smallerHeight) - 1);
        boolean[] hash = new boolean[smallerWidth * smallerHeight];
        int i = 0;
        for (int x = 0; x < smallerWidth; x++) {
            for (int y = 0; y < smallerHeight; y++) {
                if (x != 0 && y != 0) {
                    hash[i++] = dctVals[x][y] > avg;
                }
            }
        }
        return hash;
    }


    private static int getBlue(Bitmap img, int x, int y) {
        return img.getPixel(x, y) & 0xff;
    }

    private double[] c;
    private double[] c2;

    private void initCoefficients() {
        c = new double[width];
        c2 = new double[height];
        for (int i = 1; i < width; i++) {
            c[i] = 1;
        }
        for (int i = 1; i < height; i++) {
            c2[i] = 1;
        }
        c[0] = 1 / Math.sqrt(2.0);
        c2[0] = 1 / Math.sqrt(2.0);
    }

    private double[][] applyDCT(double[][] f) {
        double[][] F = new double[width][height];
        for (int u = 0; u < width; u++) {
            for (int v = 0; v < height; v++) {
                double sum = 0.0;
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        sum += Math.cos(((2 * i + 1) / (2.0 * width)) * u * Math.PI) * Math.cos(((2 * j + 1) / (2.0 * height)) * v * Math.PI) * (f[i][j]);
                    }
                }
                sum *= ((c[u] * c2[v]) / 4.0);
                F[u][v] = sum;
            }
        }
        return F;
    }

    public static class OCR implements com.stardust.mi666.ocr.OCR {

        private SimpleTextDetector mSimpleTextDetector;
        private Map<Character, boolean[]> mChars = new HashMap<>();
        private ImagePHash mImagePHash;

        public OCR(ColorDetector colorDetector, int size, int smallerSize) {
            mSimpleTextDetector = new SimpleTextDetector(colorDetector);
            mImagePHash = new ImagePHash(size, smallerSize);
        }

        public OCR(ColorDetector colorDetector, ImagePHash pHash) {
            mSimpleTextDetector = new SimpleTextDetector(colorDetector);
            mImagePHash = pHash;
        }

        @Override
        public void addChar(char ch, Bitmap bitmap) {
            mChars.put(ch, mImagePHash.getHash(mSimpleTextDetector.detect(bitmap)));
        }

        @Override
        public char detect(Bitmap bitmap) {
            bitmap = mSimpleTextDetector.detect(bitmap);
            int min = Integer.MAX_VALUE;
            boolean[] h = mImagePHash.getHash(bitmap);
            char ch = ' ';
            for (Map.Entry<Character, boolean[]> entry : mChars.entrySet()) {
                int d = mImagePHash.distance(h, entry.getValue());
                //Log.i(LOG_TAG, "char: " + entry.getKey() + " distance: " + d);
                if (d < min) {
                    min = d;
                    ch = entry.getKey();
                }
            }
            return ch;
        }
    }

}