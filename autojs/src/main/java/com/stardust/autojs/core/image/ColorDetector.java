package com.stardust.autojs.core.image;

import android.graphics.Color;

/**
 * Created by Stardust on 2017/5/20.
 */

public interface ColorDetector {

    boolean detectsColor(int red, int green, int blue);

    abstract class AbstractColorDetector implements ColorDetector {

        protected final int mColor;
        protected final int mR, mG, mB;

        public AbstractColorDetector(int color) {
            mColor = color;
            mR = Color.red(color);
            mG = Color.green(color);
            mB = Color.blue(color);
        }
    }

    class EqualityDetector extends AbstractColorDetector {


        public EqualityDetector(int color) {
            super(color);
        }

        @Override
        public boolean detectsColor(int red, int green, int blue) {
            return mR == red && mG == green && mB == blue;
        }
    }

    class DifferenceDetector extends AbstractColorDetector {

        private final int mThreshold;

        public DifferenceDetector(int color, int threshold) {
            super(color);
            mThreshold = threshold * 3;
        }


        @Override
        public boolean detectsColor(int R, int G, int B) {
            return Math.abs(R - mR) + Math.abs(G - mG) + Math.abs(B - mB) <= mThreshold;
        }
    }

    class RDistanceDetector extends AbstractColorDetector {

        private final int mThreshold;

        public RDistanceDetector(int color, int threshold) {
            super(color);
            mThreshold = threshold;
        }

        @Override
        public boolean detectsColor(int R, int G, int B) {
            return Math.abs(mR - R) <= mThreshold;
        }
    }

    class RGBDistanceDetector extends AbstractColorDetector {

        private final int mThreshold;

        public RGBDistanceDetector(int color, int threshold) {
            super(color);
            mThreshold = threshold * threshold * 3;
        }

        @Override
        public boolean detectsColor(int R, int G, int B) {
            int dR = R - mR;
            int dG = G - mG;
            int dB = B - mB;
            int d = dR * dR + dG * dG + dB * dB;
            return d <= mThreshold;
        }
    }

    class WeightedRGBDistanceDetector extends AbstractColorDetector {

        private final int mThreshold;
        private final int mR, mG, mB;

        public WeightedRGBDistanceDetector(int color, int threshold) {
            super(color);
            mR = (color & 0xff0000) >> 16;
            mG = (color & 0x00ff00) >> 8;
            mB = color & 0xff;
            mThreshold = threshold * threshold * 8;
        }

        @Override
        public boolean detectsColor(int R, int G, int B) {
            int dR = R - mR;
            int dG = G - mG;
            int dB = B - mB;
            double meanR = (mR + R) / 2;
            double weightR = 2 + meanR / 256;
            double weightG = 4.0;
            double weightB = 2 + (255 - meanR) / 256;
            return weightR * dR * dR + weightG * dG * dG + weightB * dB * dB <= mThreshold;
        }
    }

    class HDistanceDetector extends AbstractColorDetector {

        private final int mH;
        private final int mThreshold;

        public HDistanceDetector(int color, int threshold) {
            super(color);
            mH = getH(mR, mG, mB);
            mThreshold = threshold;
        }

        @Override
        public boolean detectsColor(int R, int G, int B) {
            return Math.abs(mH - getH(R, G, B)) <= mThreshold;
        }

        private static int getH(int R, int G, int B) {
            int max, min, H;
            if (R > G) {
                min = Math.min(G, B);
                max = Math.max(R, B);
            } else {
                min = Math.min(R, B);
                max = Math.max(G, B);
            }
            if (R == max) {
                H = (G - B) / (max - min) * 60;
            } else if (G == max) {
                H = 120 + (B - R) / (max - min) * 60;
            } else {
                H = 240 + (R - G) / (max - min) * 60;
            }
            if (H < 0) H = H + 360;
            return H;
        }
    }

    class HSDistanceDetector extends AbstractColorDetector {

        private final int mH, mS;
        private final int mThreshold;

        public HSDistanceDetector(int color, int threshold) {
            super(color);
            long HS = getHS(mR, mG, mB);
            mH = (int) (HS & 0xffffffffL);
            mS = (int) ((HS >> 32) & 0xffffffffL);
            mThreshold = threshold * 3729600 / 255;
        }

        public HSDistanceDetector(int color, float similarity) {
            this(color, (int) (1.0f - similarity) * 255);
        }

        @Override
        public boolean detectsColor(int R, int G, int B) {
            long hs = getHS(R, G, B);
            int dH = (int) (hs & 0xffffffffL) - mH;
            int dS = (int) ((hs >> 32) & 0xffffffffL) - mS;
            return dH * dH + dS * dS <= mThreshold;
        }

        private static long getHS(int R, int G, int B) {
            int max, min, H;
            if (R > G) {
                min = Math.min(G, B);
                max = Math.max(R, B);
            } else {
                min = Math.min(R, B);
                max = Math.max(G, B);
            }
            if (R == max) {
                H = (G - B) / (max - min) * 60;
            } else if (G == max) {
                H = 120 + (B - R) / (max - min) * 60;
            } else {
                H = 240 + (R - G) / (max - min) * 60;
            }
            if (H < 0) H = H + 360;
            int S = (max - min) * 100 / max;
            return H & ((long) S << 32);
        }
    }

}