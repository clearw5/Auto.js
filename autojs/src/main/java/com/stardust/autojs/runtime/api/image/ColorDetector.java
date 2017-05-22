package com.stardust.autojs.runtime.api.image;

/**
 * Created by Stardust on 2017/5/20.
 */

public interface ColorDetector {

    boolean detectsColor(int color);

    abstract class AbstractColorDetector implements ColorDetector {

        protected final int mColor;

        public AbstractColorDetector(int color) {
            mColor = color;
        }
    }

    class EqualityDetector implements ColorDetector {

        private final int mColor;

        public EqualityDetector(int color) {
            mColor = color & 0xffffff;
        }

        @Override
        public boolean detectsColor(int color) {
            return mColor == (color & 0xffffff);
        }
    }

    class DifferenceDetector extends AbstractColorDetector {

        private final int mThreshold;

        public DifferenceDetector(int color, int threshold) {
            super(color);
            mThreshold = threshold;
        }

        @Override
        public boolean detectsColor(int color) {
            return Math.abs(mColor - color) <= mThreshold;
        }
    }

    class RDistanceDetector extends AbstractColorDetector {

        private final int mR;
        private final int mThreshold;

        public RDistanceDetector(int color, int threshold) {
            super(color);
            mThreshold = threshold;
            mR = (color & 0xff0000) >> 16;
        }

        @Override
        public boolean detectsColor(int color) {
            int R = (color & 0xff0000) >> 16;
            return Math.abs(mR - R) <= mThreshold;
        }
    }

    class RGBDistanceDetector extends AbstractColorDetector {

        private final int mThreshold;
        private final int mR, mG, mB;

        public RGBDistanceDetector(int color, int threshold) {
            super(color);
            mR = (color & 0xff0000) >> 16;
            mG = (color & 0x00ff00) >> 8;
            mB = color & 0xff;
            mThreshold = threshold * threshold;
        }

        @Override
        public boolean detectsColor(int color) {
            int dR = ((color & 0xff0000) >> 16) - mR;
            int dG = ((color & 0x00ff00) >> 8) - mG;
            int dB = (color & 0xff) - mB;
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
            mThreshold = threshold * threshold;
        }

        @Override
        public boolean detectsColor(int color) {
            int R = (color & 0xff0000) >> 16;
            int dR = R - mR;
            int dG = ((color & 0x00ff00) >> 8) - mG;
            int dB = (color & 0xff) - mB;
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
            mH = getH(color);
            mThreshold = threshold;
        }

        @Override
        public boolean detectsColor(int color) {
            return Math.abs(mH - getH(color)) <= mThreshold;
        }

        private static int getH(int color) {
            int R = (color & 0xff0000) >> 16;
            int G = (color & 0x00ff00) >> 8;
            int B = color & 0xff;
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
            long HS = getHS(color);
            mH = (int) (HS & 0xffffffffL);
            mS = (int) ((HS >> 32) & 0xffffffffL);
            mThreshold = threshold * threshold;
        }

        @Override
        public boolean detectsColor(int color) {
            long hs = getHS(color);
            int dH = (int) (hs & 0xffffffffL) - mH;
            int dS = (int) ((hs >> 32) & 0xffffffffL) - mS;
            return dH * dH + dS * dS <= mThreshold;
        }

        private static long getHS(int color) {
            int R = (color & 0xff0000) >> 16;
            int G = (color & 0x00ff00) >> 8;
            int B = color & 0xff;
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
            int S = (max - min) / max;
            return H & ((long) S << 32);
        }
    }

}
