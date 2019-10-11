package com.stardust.autojs.core.ui.inflater.inflaters;

import androidx.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;

import com.makeramen.roundedimageview.Corner;
import com.stardust.autojs.core.ui.inflater.ResourceParser;
import com.stardust.autojs.core.ui.inflater.ViewCreator;
import com.stardust.autojs.core.ui.inflater.util.Colors;
import com.stardust.autojs.core.ui.inflater.util.Dimensions;
import com.stardust.autojs.core.ui.inflater.util.ValueMapper;
import com.stardust.autojs.core.ui.widget.JsImageView;


import java.util.Map;

/**
 * Created by Stardust on 2017/11/30.
 */

public class JsImageViewInflater extends ImageViewInflater<JsImageView> {


    protected static final ValueMapper<ScaleType> SCALE_TYPES = new ValueMapper<ScaleType>("scaleType")
            .map("center", ScaleType.CENTER)
            .map("centerCrop", ScaleType.CENTER_CROP)
            .map("centerInside", ScaleType.CENTER_INSIDE)
            .map("fitCenter", ScaleType.FIT_CENTER)
            .map("fitEnd", ScaleType.FIT_END)
            .map("fitStart", ScaleType.FIT_START)
            .map("fitXY", ScaleType.FIT_XY)
            .map("matrix", ScaleType.MATRIX);

    public JsImageViewInflater(ResourceParser resourceParser) {
        super(resourceParser);
    }

    @Override
    public boolean setAttr(JsImageView view, String attr, String value, ViewGroup parent, Map<String, String> attrs) {
        switch (attr) {
            case "radius":
                view.setCornerRadius(Dimensions.parseToPixel(value, view));
                break;
            case "radiusTopLeft":
                view.setCornerRadius(Dimensions.parseToPixel(value, view), view.getCornerRadius(Corner.TOP_RIGHT), view.getCornerRadius(Corner.BOTTOM_LEFT), view.getCornerRadius(Corner.BOTTOM_RIGHT));
                break;
            case "radiusTopRight":
                view.setCornerRadius(view.getCornerRadius(Corner.TOP_LEFT), Dimensions.parseToPixel(value, view), view.getCornerRadius(Corner.BOTTOM_LEFT), view.getCornerRadius(Corner.BOTTOM_RIGHT));
                break;
            case "radiusBottomLeft":
                view.setCornerRadius(view.getCornerRadius(Corner.TOP_LEFT), view.getCornerRadius(Corner.TOP_RIGHT), Dimensions.parseToPixel(value, view), view.getCornerRadius(Corner.BOTTOM_RIGHT));
                break;
            case "radiusBottomRight":
                view.setCornerRadius(view.getCornerRadius(Corner.TOP_LEFT), view.getCornerRadius(Corner.TOP_RIGHT), view.getCornerRadius(Corner.BOTTOM_LEFT), Dimensions.parseToPixel(value, view));
                break;
            case "borderWidth":
                view.setBorderWidth(Dimensions.parseToPixel(value, view));
                break;
            case "borderColor":
                view.setBorderColor(Colors.parse(view, value));
                break;
            case "circle":
                view.setCircle(true);
                break;
            case "scaleType":
                view.setScaleType(SCALE_TYPES.get(value));
                break;
            default:
                return super.setAttr(view, attr, value, parent, attrs);
        }
        return true;
    }

    @Nullable
    @Override
    public ViewCreator<JsImageView> getCreator() {
        return (context, attrs) -> {
            JsImageView imageView = new JsImageView(context);
            imageView.setDrawables(getDrawables());
            return imageView;
        };
    }
}
