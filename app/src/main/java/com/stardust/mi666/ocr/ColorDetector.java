package com.stardust.mi666.ocr;

import android.graphics.Color;

/**
 * Created by Stardust on 2017/4/23.
 */

public interface ColorDetector {

    boolean isCharPixel(int color);

    class ColorDistanceDetector implements ColorDetector {

        private int mColor;
        private double mThreshold;

        public ColorDistanceDetector(int color, double threshold) {
            mColor = color;
            mThreshold = threshold;
        }

        public static double distance(int c1, int c2) {
            double meanR = (Color.red(c1) + Color.red(c2)) / 2;
            int r = Color.red(c1) - Color.red(c2);
            int g = Color.green(c1) - Color.green(c2);
            int b = Color.blue(c1) - Color.blue(c2);
            double weightR = 2 + meanR / 256;
            double weightG = 4.0;
            double weightB = 2 + (255 - meanR) / 256;
            return Math.sqrt(weightR * r * r + weightG * g * g + weightB * b * b);
        }

        @Override
        public boolean isCharPixel(int color) {
            return distance(color, mColor) < mThreshold;
        }
    }

    class SimpleColorDetector implements ColorDetector {

        private int mColor;

        public SimpleColorDetector(int color) {
            mColor = color;
        }

        @Override
        public boolean isCharPixel(int color) {
            return mColor == color;
        }
    }
}
