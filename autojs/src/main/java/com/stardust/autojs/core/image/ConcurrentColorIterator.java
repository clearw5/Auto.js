package com.stardust.autojs.core.image;

import android.graphics.Rect;
import android.media.Image;

import java.nio.ByteBuffer;

/**
 * Created by Stardust on 2017/5/21.
 */

public interface ConcurrentColorIterator {

    void nextColor(Pixel pixel);

    class Pixel {

        public int x;
        public int y;
        public int color;
        public boolean valid = true;

    }

    abstract class ConcurrentImageColorIterator implements ConcurrentColorIterator {

        protected final ByteBuffer mByteBuffer;
        protected final int mImageWidth, mImageHeight;
        protected final Rect mIterateArea;
        protected final int mAreaWidth, mAreaHeight;
        protected volatile int mX = -1, mY;

        public ConcurrentImageColorIterator(Image image, Rect area) {
            Image.Plane plane = image.getPlanes()[0];
            mByteBuffer = plane.getBuffer();
            mImageWidth = image.getWidth();
            mImageHeight = image.getHeight();
            mIterateArea = area;
            mAreaWidth = area.width();
            mAreaHeight = area.height();
        }


        protected void skip(int i) {
            mByteBuffer.position(mByteBuffer.position() + i);
        }
    }

    class ConcurrentSequentialIterator extends ConcurrentImageColorIterator {

        private final int mSkipPerRow;

        public ConcurrentSequentialIterator(Image image, Rect area) {
            super(image, area);
            Image.Plane plane = image.getPlanes()[0];
            int pixelStride = plane.getPixelStride();
            int rowStride = plane.getRowStride();
            int rowPadding = rowStride - pixelStride * mImageWidth;
            mSkipPerRow = (mImageWidth - mAreaWidth) * pixelStride + rowPadding;
            int offset = mIterateArea.top * rowStride + mIterateArea.left * pixelStride;
            mByteBuffer.position(offset);
        }

        // TODO: 2017/5/21 对锁的竞争造成并发速度极慢。能否做到无锁？
        @Override
        public synchronized void nextColor(Pixel pixel) {
            if (!(mY < mAreaHeight - 1 || mX < mAreaWidth - 1)) {
                pixel.valid = false;
                return;
            }
            int c = mByteBuffer.getInt();
            c = ((c & 0xff) << 16) | (c & 0xff00) | ((c & 0xff0000) >> 16) | 0xff000000;
            mX++;
            if (mX == mAreaWidth) {
                mX = 0;
                mY++;
                if (mSkipPerRow > 0) {
                    skip(mSkipPerRow);
                }
            }
            pixel.x = mX + mIterateArea.left;
            pixel.y = mY + mIterateArea.top;
            pixel.color = c;
        }
    }

}
