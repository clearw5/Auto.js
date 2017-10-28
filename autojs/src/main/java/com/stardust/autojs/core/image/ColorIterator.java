package com.stardust.autojs.core.image;

import android.graphics.Rect;
import android.media.Image;

import java.nio.ByteBuffer;

/**
 * Created by Stardust on 2017/5/20.
 */

public interface ColorIterator {

    class Pixel {
        int red;
        int green;
        int blue;
    }

    boolean hasNext();

    void nextColor(Pixel pixel);

    int getX();

    int getY();

    abstract class ImageColorIterator implements ColorIterator {

        protected final ByteBuffer mByteBuffer;
        protected final Rect mIterateArea;

        public ImageColorIterator(Image image, Rect area, boolean duplicateBuffer) {
            Image.Plane plane = image.getPlanes()[0];
            if (duplicateBuffer) {
                mByteBuffer = plane.getBuffer().duplicate();
            } else {
                mByteBuffer = plane.getBuffer();
            }
            mIterateArea = area;
        }

        public ImageColorIterator(Image image, Rect area) {
            this(image, area, false);
        }


        protected void skip(int i) {
            mByteBuffer.position(mByteBuffer.position() + i);
        }
    }

    class SequentialIterator extends ImageColorIterator {

        private static final String LOG_TAG = "SequentialIterator";

        private final int mRowStride;
        private final int mSkipPerRow;
        private final int mWidth;
        private final int mHeight;
        private int mX = -1;
        private int mY = 0;

        public SequentialIterator(Image image, Rect area, boolean duplicateBuffer) {
            super(image, area, duplicateBuffer);
            Image.Plane plane = image.getPlanes()[0];
            int pixelStride = plane.getPixelStride();
            mRowStride = plane.getRowStride();
            mWidth = area.width();
            mHeight = area.height();
            int rowPadding = mRowStride - pixelStride * image.getWidth();
            mSkipPerRow = rowPadding + (image.getWidth() - mWidth) * pixelStride;
            int offset = mIterateArea.top * mRowStride + mIterateArea.left * pixelStride;
            mByteBuffer.position(offset);
        }

        public SequentialIterator(Image image, Rect area) {
            this(image, area, false);
        }

        public SequentialIterator(Image image) {
            this(image, new Rect(0, 0, image.getWidth(), image.getHeight()));
        }


        @Override
        public boolean hasNext() {
            return mX < mWidth - 1 || mY < mHeight - 1;
        }

        @Override
        public int getX() {
            return mIterateArea.left + mX;
        }

        @Override
        public int getY() {
            return mIterateArea.top + mY;
        }

        @Override
        public void nextColor(Pixel pixel) {
            if (mX == mWidth - 1) {
                skip(mSkipPerRow);
                mX = 0;
                mY++;
            } else {
                mX++;
            }
            pixel.red = mByteBuffer.get() & 0xff;
            pixel.green = mByteBuffer.get() & 0xff;
            pixel.blue = mByteBuffer.get() & 0xff;
            mByteBuffer.get();
        }
    }


    // TODO: 2017/5/29  中心螺旋。未完成。
    class CentralSpiralIterator extends ImageColorIterator {

        private static final int DIRECTION_RIGHT = 0;
        private static final int DIRECTION_TOP = 1;
        private static final int DIRECTION_LEFT = 2;
        private static final int DIRECTION_BOTTOM = 3;

        private final int mPixelStride, mRowStride;
        private int mNextStepSkip;
        private int mStepCount;
        private int mMaxStep = 1;
        private int mDirection = DIRECTION_RIGHT;

        public CentralSpiralIterator(Image image, Rect area, boolean duplicateBuffer) {
            super(image, area, duplicateBuffer);
            Image.Plane plane = image.getPlanes()[0];
            mPixelStride = mNextStepSkip = plane.getPixelStride();
            mRowStride = plane.getRowStride();
            mByteBuffer.position(area.centerX() * mPixelStride + area.centerY() * mRowStride);
        }

        public CentralSpiralIterator(Image image, Rect area) {
            this(image, area, false);
        }

        @Override
        public boolean hasNext() {
            return mByteBuffer.position() < mByteBuffer.limit();
        }

        @Override
        public void nextColor(Pixel pixel) {
            int c = mByteBuffer.getInt();
            skip(mNextStepSkip);
            mStepCount++;
            if (mStepCount == mMaxStep) {
                mStepCount = 0;
                mMaxStep++;
                mDirection = (mDirection + 1) & 4;
                switch (mDirection) {
                    case DIRECTION_RIGHT:
                        mNextStepSkip = mPixelStride;
                        break;
                    case DIRECTION_TOP:
                        mNextStepSkip = -mRowStride;
                        break;
                    case DIRECTION_LEFT:
                        mNextStepSkip = -mPixelStride;
                        break;
                    case DIRECTION_BOTTOM:
                        mNextStepSkip = mRowStride;
                        break;
                }
            }
        }

        @Override
        public int getX() {
            return 0;
        }

        @Override
        public int getY() {
            return 0;
        }
    }


}
