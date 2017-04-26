package com.stardust.mi666.ocr;

import android.graphics.Bitmap;

/**
 * Created by Stardust on 2017/4/23.
 */

public class SimpleTextDetector {

    private ColorDetector mColorDetector;

    public SimpleTextDetector(ColorDetector colorDetector) {
        mColorDetector = colorDetector;
    }

    public Bitmap detect(Bitmap bitmap) {
        int minX = bitmap.getWidth();
        int minY = bitmap.getHeight();
        int maxX = 0;
        int maxY = 0;
        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                if (mColorDetector.isCharPixel(bitmap.getPixel(i, j))) {
                    minX = Math.min(minX, i);
                    minY = Math.min(minY, j);
                    maxX = Math.max(maxX, i);
                    maxY = Math.max(maxY, j);
                }
            }
        }
        return Bitmap.createBitmap(bitmap, minX, minY, maxX - minX, maxY - minY);
    }
}
