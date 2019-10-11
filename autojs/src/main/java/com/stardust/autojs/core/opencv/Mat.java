package com.stardust.autojs.core.opencv;

import com.stardust.util.ResourceMonitor;

import org.mozilla.javascript.ScriptRuntime;
import org.opencv.core.Range;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

public class Mat extends org.opencv.core.Mat implements ResourceMonitor.Resource {

    private static Method nClone;

    static {
        try {
            nClone = org.opencv.core.Mat.class.getDeclaredMethod("n_clone", long.class);
            nClone.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private static final AtomicInteger sResourceId = new AtomicInteger();
    private volatile boolean mReleased = false;
    private final int mResourceId = sResourceId.incrementAndGet();

    public Mat(long addr) {
        super(addr);
        ResourceMonitor.onOpen(this);
    }

    public Mat() {
        super();
        ResourceMonitor.onOpen(this);
    }

    public Mat(int rows, int cols, int type) {
        super(rows, cols, type);
        ResourceMonitor.onOpen(this);
    }

    public Mat(Size size, int type) {
        super(size, type);
        ResourceMonitor.onOpen(this);
    }

    public Mat(int rows, int cols, int type, Scalar s) {
        super(rows, cols, type, s);
        ResourceMonitor.onOpen(this);
    }

    public Mat(Size size, int type, Scalar s) {
        super(size, type, s);
        ResourceMonitor.onOpen(this);
    }

    public Mat(Mat m, Range rowRange, Range colRange) {
        super(m, rowRange, colRange);
        ResourceMonitor.onOpen(this);
    }

    public Mat(Mat m, Range rowRange) {
        super(m, rowRange);
        ResourceMonitor.onOpen(this);
    }

    public Mat(Mat m, Rect roi) {
        super(m, roi);
        ResourceMonitor.onOpen(this);
    }

    @Override
    public Mat clone() {
        return new Mat(n_clone(this.nativeObj));
    }

    protected long n_clone(long addr) {
        try {
            return (long) nClone.invoke(this, addr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
            mReleased = true;
        }
        super.finalize();
    }

    @Override
    public int getResourceId() {
        return mResourceId;
    }
}
