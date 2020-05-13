package com.tony;

/**
 * @author TonyJiang 2019/11/22
 */
public class ProgressInfo {

    private int totalLength;
    private int readLength;

    public ProgressInfo(int totalLength, int readLength) {
        this.totalLength = totalLength;
        this.readLength = readLength;
    }

    public double getProgress() {
        return this.readLength / (double)this.totalLength;
    }

    public int getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(int totalLength) {
        this.totalLength = totalLength;
    }

    public int getReadLength() {
        return readLength;
    }

    public void setReadLength(int readLength) {
        this.readLength = readLength;
    }
}
