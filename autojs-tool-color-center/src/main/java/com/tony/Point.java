package com.tony;

public class Point extends org.opencv.core.Point {
    private int same;
    private int regionSame;
    private int left;
    private int right;
    private int top;
    private int bottom;

    public Point(double x, double y) {
        super((int)x, (int)y);
    }

    public int getSame() {
        return same;
    }

    public void setSame(int same) {
        this.same = same;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public int getRegionSame() {
        return regionSame;
    }

    public void setRegionSame(int regionSame) {
        this.regionSame = regionSame;
    }
}
