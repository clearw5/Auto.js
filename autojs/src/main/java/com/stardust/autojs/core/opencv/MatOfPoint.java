package com.stardust.autojs.core.opencv;

import com.stardust.util.ResourceMonitor;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.concurrent.atomic.AtomicInteger;

public class MatOfPoint extends org.opencv.core.MatOfPoint implements ResourceMonitor.Resource {

    private static final AtomicInteger sResourceId = new AtomicInteger();
    private volatile boolean mReleased = false;
    private final int mResourceId = sResourceId.incrementAndGet();

    public MatOfPoint() {
        super();
        ResourceMonitor.onOpen(this);
    }

    public MatOfPoint(long addr) {
        super(addr);
        ResourceMonitor.onOpen(this);
    }

    public MatOfPoint(Mat m) {
        super(m);
        ResourceMonitor.onOpen(this);
    }

    public MatOfPoint(Point... a) {
        super(a);
        ResourceMonitor.onOpen(this);
    }

    @Override
    public void release() {
        super.release();
        mReleased = true;
        ResourceMonitor.onClose(this);
    }

    @Override
    protected void finalize() throws Throwable {
        if (!mReleased) {
            ResourceMonitor.onFinalize(this);
            super.release();
        }
        super.finalize();
    }

    @Override
    public int getResourceId() {
        return mResourceId;
    }
}
