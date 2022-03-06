package com.baidu.paddle.lite.ocr;

import android.graphics.Point;
import android.graphics.Rect;

public class OcrResult implements Comparable<OcrResult> {
    private String label;
    private float confidence;
    private Rect bounds;

    public OcrResult() {
    }

    public OcrResult(OcrResultModel resultModel) {
        this.label = resultModel.getLabel();
        this.confidence = resultModel.getConfidence();
        int left = -1, right = -1, top = -1, bottom = -1;
        for (Point point : resultModel.getPoints()) {
            if (point.x < left || left == -1) {
                left = point.x;
            }
            if (point.x > right || right == -1) {
                right = point.x;
            }
            if (point.y < top || top == -1) {
                top = point.y;
            }
            if (point.y > bottom || bottom == -1) {
                bottom = point.y;
            }
        }
        this.bounds = new Rect(left, top, right, bottom);
    }

    public OcrResult(String label, float confidence, Rect bounds) {
        this.label = label;
        this.confidence = confidence;
        this.bounds = bounds;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public float getConfidence() {
        return confidence;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }

    public Rect getBounds() {
        return bounds;
    }

    public RectLocation getLocation() {
        return new RectLocation(bounds);
    }

    public void setBounds(Rect bounds) {
        this.bounds = bounds;
    }

    public String getWords() {
        return label.trim().replace("\r", "");
    }

    @Override
    public int compareTo(OcrResult o) {
        // 上下差距小于二分之一的高度 判定为同一行
        int deviation = Math.max(this.bounds.height(), o.bounds.height()) / 2;
        // 通过垂直中心点的距离判定
        if (Math.abs((this.bounds.top + this.bounds.bottom) / 2 - (o.bounds.top + o.bounds.bottom) / 2) < deviation) {
            return this.bounds.left - o.bounds.left;
        } else {
            return this.bounds.bottom - o.bounds.bottom;
        }
    }

    public static class RectLocation {
        public int left;
        public int top;
        public int width;
        public int height;

        public RectLocation() {
        }

        public RectLocation(int left, int top, int width, int height) {
            this.left = left;
            this.top = top;
            this.width = width;
            this.height = height;
        }

        public RectLocation(Rect rect) {
            left = rect.left;
            top = rect.top;
            width = rect.right - rect.left;
            height = rect.bottom - rect.top;
        }
    }
}
