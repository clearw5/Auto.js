package com.stardust.autojs.runtime.api.image;

import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.concurrent.VolatileBox;
import com.stardust.util.ScreenMetrics;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Stardust on 2017/5/18.
 */

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class ColorFinder {

    private static ThreadPoolExecutor sThreadPoolExecutor = new ThreadPoolExecutor(4, 16, 5, TimeUnit.MINUTES, new SynchronousQueue<Runnable>());

    static {
        sThreadPoolExecutor.allowCoreThreadTimeOut(true);
    }

    private ThreadPoolExecutor mThreadPoolExecutor;
    private ScreenMetrics mScreenMetrics;

    public ColorFinder(ThreadPoolExecutor threadPoolExecutor) {
        mThreadPoolExecutor = threadPoolExecutor;
        mScreenMetrics = new ScreenMetrics();
    }

    public ColorFinder() {
        this(sThreadPoolExecutor);
    }

    public void prestartThreads() {
        mThreadPoolExecutor.prestartAllCoreThreads();
    }

    public Point[] findAllColors(Image image, ColorDetector detector, Rect rect, int threadCount) {
        List<Point> result = new Vector<>();
        ColorIterator[] iterators = divide(image, rect, threadCount);
        for (int i = 1; i < threadCount; i++) {
            mThreadPoolExecutor.execute(new FindAllColorsRunnable(result, iterators[i], detector));
        }
        new FindAllColorsRunnable(result, iterators[0], detector).run();
        Point[] points = new Point[result.size()];
        for (int i = 0; i < points.length; i++) {
            points[i] = scalePoint(result.get(i), image.getWidth(), image.getHeight());
        }
        return points;
    }

    public Point findColorConcurrently(Image image, ColorDetector detector, Rect rect, int threadCount) {
        if (threadCount <= 1) {
            return findColor(image, detector, rect);
        }
        VolatileBox<Point> result = new VolatileBox<>();
        ColorIterator[] iterators = divide(image, rect, threadCount);
        for (int i = 1; i < threadCount; i++) {
            mThreadPoolExecutor.execute(new FindColorRunnable(result, iterators[i], detector));
        }
        new FindColorRunnable(result, iterators[0], detector).run();
        return scalePoint(result.get(), image.getWidth(), image.getHeight());
    }


    private Point scalePoint(Point point, int width, int height) {
        if (point == null)
            return null;
        mScreenMetrics.setDesignHeight(height);
        mScreenMetrics.setDesignWidth(width);
        point.set(mScreenMetrics.scaleX(point.x), mScreenMetrics.scaleY(point.y));
        return point;
    }

    protected ColorIterator[] divide(Image image, Rect rect, int count) {
        Rect[] subAreas = divideIntoSubAreas(rect, count);
        int centerY = rect.centerY();
        ColorIterator[] iterators = new ColorIterator[count];
        for (int i = 1; i < subAreas.length; i++) {
            Rect subArea = subAreas[i];
            if (subArea.top > centerY) {
                iterators[i] = new ColorIterator.SequentialIterator(image, subArea, true);
            } else {
                iterators[i] = new ColorIterator.SequentialIterator(image, subArea, true);
            }
        }
        iterators[0] = new ColorIterator.SequentialIterator(image, subAreas[0], false);
        return iterators;
    }

    protected Rect[] divideIntoSubAreas(Rect rect, int count) {
        int row, column;
        switch (count) {
            case 4:
            case 6:
            case 8:
            case 10:
            case 14:
                row = count / 2;
                column = 2;
                break;
            case 9:
            case 12:
            case 15:
                row = count / 3;
                column = 3;
                break;
            case 16:
                row = 4;
                column = 4;
                break;
            default:
                row = count;
                column = 1;
        }
        Rect[] cells = new Rect[count];
        int cellWidth = rect.width() / column;
        int cellHeight = rect.height() / row;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                int x = rect.left + j * cellWidth;
                int y = rect.top + i * cellHeight;
                cells[i * column + j] = new Rect(x, y, x + cellWidth, y + cellHeight);
            }
        }
        return cells;
    }

    public Point findColorConcurrently(Image image, int color, Rect rect, int threadCount, int threshold) {
        return findColorConcurrently(image, defaultColorDetector(color, threshold), rect, threadCount);
    }

    public Point findColorConcurrently(Image image, int color, Rect rect, int threadCount) {
        return findColorConcurrently(image, color, rect, threadCount, 8);
    }

    public Point findColorConcurrently(Image image, int color, int threadCount) {
        Rect rect = new Rect(0, 0, image.getWidth(), image.getHeight());
        return findColorConcurrently(image, defaultColorDetector(color), rect, threadCount);
    }

    public Point findColorEqualsConcurrently(Image image, int color, Rect rect, int threadCount) {
        return findColorConcurrently(image, new ColorDetector.EqualityDetector(color), rect, threadCount);
    }

    public static Point findColor(ColorIterator iterator, ColorDetector detector) {
        Thread thread = Thread.currentThread();
        ColorIterator.Pixel pixel = new ColorIterator.Pixel();
        while (iterator.hasNext() && !thread.isInterrupted()) {
            iterator.nextColor(pixel);
            iterator.nextColor(pixel);
            if (detector.detectsColor(pixel.red, pixel.green, pixel.blue)) {
                return new Point(iterator.getX(), iterator.getY());
            }
        }
        if (thread.isInterrupted()) {
            throw new ScriptInterruptedException();
        }
        return null;
    }

    public ColorDetector defaultColorDetector(int color) {
        return new ColorDetector.RGBDistanceDetector(color, 16);
    }

    public ColorDetector defaultColorDetector(int color, int threshold) {
        return new ColorDetector.RGBDistanceDetector(color, threshold);
    }

    public ColorIterator defaultColorIterator(Image image, Rect rect) {
        return new ColorIterator.SequentialIterator(image, rect);
    }

    public Point findColor(Image image, ColorDetector detector, Rect rect) {
        return scalePoint(findColor(defaultColorIterator(image, rect), detector), image.getWidth(), image.getHeight());
    }

    public Point findColor(Image image, int color, Rect rect) {
        return scalePoint(findColor(defaultColorIterator(image, rect), defaultColorDetector(color)), image.getWidth(), image.getHeight());
    }

    public Point findColor(Image image, int color) {
        return findColor(image, color, new Rect(0, 0, image.getWidth(), image.getHeight()));
    }

    public Point findColorEquals(Image image, int color, Rect rect) {
        return scalePoint(findColor(defaultColorIterator(image, rect), new ColorDetector.EqualityDetector(color)), image.getWidth(), image.getHeight());
    }

    private static class FindColorRunnable implements Runnable {

        private final VolatileBox<Point> mResultBox;
        private final ColorIterator mColorIterator;
        private final ColorDetector mColorDetector;

        private FindColorRunnable(VolatileBox<Point> resultBox, ColorIterator colorIterator, ColorDetector colorDetector) {
            mResultBox = resultBox;
            mColorIterator = colorIterator;
            mColorDetector = colorDetector;
        }

        @Override
        public void run() {
            Thread thread = Thread.currentThread();
            ColorIterator.Pixel pixel = new ColorIterator.Pixel();
            while (mResultBox.isNull() && mColorIterator.hasNext() && !thread.isInterrupted()) {
                mColorIterator.nextColor(pixel);
                if (mColorDetector.detectsColor(pixel.red, pixel.green, pixel.blue)) {
                    mResultBox.set(new Point(mColorIterator.getX(), mColorIterator.getY()));
                    break;
                }
            }
            if (thread.isInterrupted()) {
                throw new ScriptInterruptedException();
            }
        }
    }

    private static class FindAllColorsRunnable implements Runnable {

        private final List<Point> mResult;
        private final ColorIterator mColorIterator;
        private final ColorDetector mColorDetector;

        private FindAllColorsRunnable(List<Point> result, ColorIterator colorIterator, ColorDetector colorDetector) {
            mResult = result;
            mColorIterator = colorIterator;
            mColorDetector = colorDetector;
        }

        @Override
        public void run() {
            Thread thread = Thread.currentThread();
            ColorIterator.Pixel pixel = new ColorIterator.Pixel();
            while (mColorIterator.hasNext() && !thread.isInterrupted()) {
                mColorIterator.nextColor(pixel);
                if (mColorDetector.detectsColor(pixel.red, pixel.green, pixel.blue)) {
                    mResult.add(new Point(mColorIterator.getX(), mColorIterator.getY()));
                }
            }
            if (thread.isInterrupted()) {
                throw new ScriptInterruptedException();
            }
        }
    }

}