package com.baidu.paddle.lite.ocr;

import android.graphics.Point;
import android.graphics.Rect;

public class OcrResult {
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

    public void setBounds(Rect bounds) {
        this.bounds = bounds;
    }
}
