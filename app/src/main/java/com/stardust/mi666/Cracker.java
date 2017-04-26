package com.stardust.mi666;

import com.stardust.util.ScreenMetrics;

import java.util.List;

/**
 * Created by Stardust on 2017/4/24.
 */

public class Cracker {

    private OCR mOCR;
    private Solver mSolver;
    private int mX, mY, mIntervalX, mIntervalY, mWidth, mHeight;

    private void init(int x, int y, int intervalX, int intervalY, int w, int h) {
        mOCR = new OCR(x, y, intervalX, intervalY, w, h);
        mSolver = new Solver();
        mX = x;
        mY = y;
        mIntervalY = intervalY;
        mIntervalX = intervalX;
        mWidth = w;
        mHeight = h;
    }

    public Cracker() {
        if (ScreenMetrics.getScreenWidth() == 1080 && ScreenMetrics.getScreenHeight() == 1920) {
            init(68, 724, 238, 236, 228, 68);
        } else {
            double scaleX = 1080 / ScreenMetrics.getScreenWidth();
            double scaleY = 1920 / ScreenMetrics.getScreenHeight();
            init((int) (68 * scaleX), (int) (724 * scaleY), (int) (238 * scaleX), (int) (236 * scaleY), (int) (228 * scaleX), (int) (68 * scaleY));
        }
    }

    public int[][] crack(String path) {
        List<Integer> numbers = mOCR.detect(path);
        mSolver.solve(numbers);
        if (!mSolver.isSolved()) {
            return new int[0][];
        }
        List<Integer> result = mSolver.getResult();
        boolean[] used = new boolean[numbers.size()];
        int[][] coordinates = new int[result.size()][2];
        for (int i = 0; i < coordinates.length; i++) {
            int index = indexOf(numbers, used, result.get(i));
            int row = index / 4;
            int col = index % 4;
            int x = mX + mIntervalX * col + mWidth / 2;
            int y = mY + mIntervalY * row + mHeight / 2;
            coordinates[i][0] = x;
            coordinates[i][1] = y;
        }
        return coordinates;
    }

    private int indexOf(List<Integer> numbers, boolean[] used, int integer) {
        for (int i = 0; i < numbers.size(); i++) {
            if (numbers.get(i) == integer && !used[i]) {
                used[i] = true;
                return i;
            }
        }
        return -1;
    }
}
