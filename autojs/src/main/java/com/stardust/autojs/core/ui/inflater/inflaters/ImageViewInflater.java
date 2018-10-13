package com.stardust.autojs.core.ui.inflater.inflaters;

import android.os.Build;
import android.view.InflateException;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.stardust.autojs.core.ui.inflater.ResourceParser;
import com.stardust.autojs.core.ui.inflater.util.Colors;
import com.stardust.autojs.core.ui.inflater.util.Dimensions;

import java.util.Map;

/**
 * Created by Stardust on 2017/11/3.
 */

public class ImageViewInflater<V extends ImageView> extends BaseViewInflater<V> {

    public ImageViewInflater(ResourceParser resourceParser) {
        super(resourceParser);
    }

    @Override
    public boolean setAttr(V view, String attr, String value, ViewGroup parent, Map<String, String> attrs) {
        if (super.setAttr(view, attr, value, parent, attrs)) {
            return true;
        }
        switch (attr) {
            case "adjustViewBounds":
                view.setAdjustViewBounds(Boolean.valueOf(value));
                break;
            case "baseline":
                view.setBaseline(Dimensions.parseToIntPixel(value, view));
                break;
            case "baselineAlignBottom":
                view.setBaselineAlignBottom(Boolean.valueOf(value));
                break;
            case "cropToPadding":
                view.setCropToPadding(Boolean.valueOf(value));
                break;
            case "maxHeight":
                view.setMaxHeight(Dimensions.parseToIntPixel(value, view));
                break;
            case "maxWidth":
                view.setMaxWidth(Dimensions.parseToIntPixel(value, view));
                break;
            case "path":
                getDrawables().setupWithImage(view,  wrapAsPath(value));
                break;
            case "scaleType":
                view.setScaleType(parseScaleType(value));
                break;
            case "src":
                getDrawables().setupWithImage(view, value);
                break;
            case "tint":
                view.setColorFilter(Colors.parse(view, value));
                break;
            case "tintMode":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setImageTintMode(TINT_MODES.get(value));
                }
                break;
            case "url":
                getDrawables().setupWithImage(view,  wrapAsUrl(value));
                break;
            default:
                return false;
        }
        return true;
    }
    private String wrapAsPath(String value) {
        if (!value.startsWith("file://")) {
            return "file://" + value;
        }
        return value;
    }

    private String wrapAsUrl(String value) {
        if (!value.startsWith("http://") && !value.startsWith("https://")) {
            return "http://" + value;
        }
        return value;
    }

    private ImageView.ScaleType parseScaleType(String value) {
        switch (value.toLowerCase()) {
            case "center":
                return ImageView.ScaleType.CENTER;
            case "center_crop":
                return ImageView.ScaleType.CENTER_CROP;
            case "center_inside":
                return ImageView.ScaleType.CENTER_INSIDE;
            case "fit_center":
                return ImageView.ScaleType.FIT_CENTER;
            case "fit_end":
                return ImageView.ScaleType.FIT_END;
            case "fit_start":
                return ImageView.ScaleType.FIT_START;
            case "fit_xy":
                return ImageView.ScaleType.FIT_XY;
            case "matrix":
                return ImageView.ScaleType.MATRIX;
        }
        throw new InflateException("unknown scale type: " + value);
    }
}
