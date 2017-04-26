package com.stardust.mi666.ocr;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Stardust on 2017/4/23.
 */

public class SimpleOCR implements OCR {

    private static class PixelLine {
        int start;
        int end;

        PixelLine(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    private static class PixelLines {
        int start = -1;
        List<PixelLine> lines = new ArrayList<>();


        void addLine(PixelLine line) {
            lines.add(line);
            if (start == -1 || start > line.start) {
                start = line.start;
            }
        }

        int distanceTo(PixelLines another) {
            if (lines.size() != another.lines.size()) {
                return lineWidthSum() + another.lineWidthSum();
            }
            int offset = another.start - start;
            int sum = 0;
            for (int i = 0; i < lines.size(); i++) {
                sum += Math.abs(lines.get(i).start + offset - another.lines.get(i).start);
                sum += Math.abs(lines.get(i).end + offset - another.lines.get(i).end);
            }
            return sum;
        }

        private int lineWidthSum() {
            int sum = 0;
            for (PixelLine line : lines) {
                sum += line.end - line.start;
            }
            return sum;
        }
    }


    public static class CharFeature {

        private List<PixelLines> mPixelLinesPerRow;

        public CharFeature(int rowNumber) {
            mPixelLinesPerRow = new ArrayList<>(rowNumber);
            for (int i = 0; i < rowNumber; i++) {
                mPixelLinesPerRow.add(new PixelLines());
            }
        }

        void addLine(int row, PixelLine line) {
            mPixelLinesPerRow.get(row).addLine(line);
        }

        int distanceTo(CharFeature feature) {
            int sum = 0;
            if (feature.mPixelLinesPerRow.size() != mPixelLinesPerRow.size()) {
                for (PixelLines lines : mPixelLinesPerRow) {
                    sum += lines.lines.size();
                }
                return sum;
            }
            for (int i = 0; i < mPixelLinesPerRow.size(); i++) {
                sum += mPixelLinesPerRow.get(i).distanceTo(feature.mPixelLinesPerRow.get(i));
            }
            return sum;
        }

    }

    private static final String TAG = "SimpleOCR";
    private Map<Character, CharFeature> mChars = new HashMap<>();
    private ColorDetector mColorDetector;

    public SimpleOCR(ColorDetector colorDetector) {
        mColorDetector = colorDetector;
    }

    public void addChar(char c, Bitmap ch) {
        mChars.put(c, getCharFeature(ch));
    }

    public CharFeature getCharFeature(Bitmap bitmap) {
        CharFeature feature = new CharFeature(bitmap.getHeight());
        for (int y = 0; y < bitmap.getHeight(); y++) {
            int mPixelStart = -1;
            for (int x = 0; x < bitmap.getWidth(); x++) {
                if (mColorDetector.isCharPixel(bitmap.getPixel(x, y))) {
                    if (mPixelStart == -1) {
                        mPixelStart = x;
                    }
                } else if (mPixelStart >= 0) {
                    feature.addLine(y, new PixelLine(mPixelStart, x));
                    mPixelStart = -1;
                }
            }
            if (mPixelStart >= 0) {
                feature.addLine(y, new PixelLine(mPixelStart, bitmap.getWidth()));
            }
        }
        return feature;
    }

    public char detect(Bitmap bitmap) {
        CharFeature feature = getCharFeature(bitmap);
        int min = Integer.MAX_VALUE;
        char detectedChar = ' ';
        for (Map.Entry<Character, CharFeature> ch : mChars.entrySet()) {
            int distance = ch.getValue().distanceTo(feature);
            Log.d(TAG, "ch=" + ch.getKey() + " distance=" + distance);
            if (distance < min) {
                min = distance;
                detectedChar = ch.getKey();
            }
        }
        return detectedChar;
    }


}
