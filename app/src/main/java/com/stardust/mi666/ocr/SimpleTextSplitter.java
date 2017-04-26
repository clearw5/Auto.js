package com.stardust.mi666.ocr;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/4/24.
 */

public class SimpleTextSplitter {

    private ColorDetector mColorDetector;
    private SimpleTextDetector mSimpleTextDetector;

    public SimpleTextSplitter(ColorDetector colorDetector) {
        mColorDetector = colorDetector;
        mSimpleTextDetector = new SimpleTextDetector(colorDetector);
    }

    public List<Bitmap> split(Bitmap bitmap) {
        Bitmap text = mSimpleTextDetector.detect(bitmap);
        int[] projection = new int[text.getWidth()];
        for (int i = 0; i < text.getWidth(); i++) {
            for (int j = 0; j < text.getHeight(); j++) {
                if (mColorDetector.isCharPixel(text.getPixel(i, j))) {
                    projection[i]++;
                }
            }
        }
        int start = -1;
        List<Bitmap> numbers = new ArrayList<>();
        for (int i = 0; i < text.getWidth(); i++) {
            if (projection[i] > 0) {
                if (start == -1) {
                    start = i;
                }
            } else {
                if (start >= 0) {
                    numbers.add(Bitmap.createBitmap(text, start, 0, i - start, text.getHeight()));
                    start = -1;
                }
            }
        }
        if (start >= 0) {
            numbers.add(Bitmap.createBitmap(text, start, 0, text.getWidth() - start, text.getHeight()));
        }
        return numbers;
    }

}
