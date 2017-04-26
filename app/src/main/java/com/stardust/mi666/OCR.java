package com.stardust.mi666;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.stardust.mi666.ocr.ColorDetector;
import com.stardust.mi666.ocr.SimpleTextSplitter;
import com.stardust.mi666.ocr.TemplateMatching;
import com.stardust.pio.UncheckedIOException;
import com.stardust.util.ViewUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/4/24.
 */

public class OCR {

    private static com.stardust.mi666.ocr.OCR ocr;
    private static SimpleTextSplitter textSplitter;

    static {
        ColorDetector colorDetector = new ColorDetector.SimpleColorDetector(0xff9f826f);
        ocr = new TemplateMatching.TemplateMatchingOCR(1, colorDetector);
        textSplitter = new SimpleTextSplitter(colorDetector);
    }

    private int mX, mY, mIntervalX, mIntervalY, mWidth, mHeight;

    public OCR(int x, int y, int intervalX, int intervalY, int w, int h) {
        mX = x;
        mY = y;
        mIntervalY = intervalY;
        mIntervalX = intervalX;
        mWidth = w;
        mHeight = h;
    }

    public static void init(Context context) {
        try {
            for (int i = 0; i < 10; i++) {
                Bitmap number = BitmapFactory.decodeStream(context.getAssets().open("numbers/" + i + ".png"));
                ocr.addChar((char) ('0' + i), number);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public List<Integer> detect(String path) {
        Bitmap screenshot = BitmapFactory.decodeFile(path);
        try {
            List<Integer> numbers = new ArrayList<>(16);
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    Bitmap block = getBlockAt(screenshot, i, j);
                    List<Bitmap> bitmaps = textSplitter.split(block);
                    StringBuilder sb = new StringBuilder();
                    for (Bitmap num : bitmaps) {
                        sb.append(ocr.detect(num));
                    }
                    numbers.add(Integer.parseInt(sb.toString()));
                    Log.i("TestOCR", "result = " + sb);
                }
            }
            return numbers;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bitmap getBlockAt(Bitmap bitmap, int row, int col) {
        return Bitmap.createBitmap(bitmap, mX + col * mIntervalX, mY + row * mIntervalY, mWidth, mHeight);
    }
}
