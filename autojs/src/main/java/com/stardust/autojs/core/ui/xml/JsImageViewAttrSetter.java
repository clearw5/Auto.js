package com.stardust.autojs.core.ui.xml;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.stardust.autojs.core.ui.widget.JsImageView;

import org.autojs.dynamiclayoutinflater.attrsetter.BaseViewAttrSetter;
import org.autojs.dynamiclayoutinflater.attrsetter.ImageViewAttrSetter;
import org.autojs.dynamiclayoutinflater.util.Colors;
import org.autojs.dynamiclayoutinflater.util.Dimensions;
import org.autojs.dynamiclayoutinflater.util.ValueMapper;

import java.util.Map;

/**
 * Created by Stardust on 2017/11/30.
 */

public class JsImageViewAttrSetter<V extends JsImageView> extends ImageViewAttrSetter<V> {


    protected static final ValueMapper<ScaleType> SCALE_TYPES = new ValueMapper<ScaleType>("scaleType")
            .map("center", ScaleType.CENTER)
            .map("centerCrop", ScaleType.CENTER_CROP)
            .map("centerInside", ScaleType.CENTER_INSIDE)
            .map("fitCenter", ScaleType.FIT_CENTER)
            .map("fitEnd", ScaleType.FIT_END)
            .map("fitStart", ScaleType.FIT_START)
            .map("fitXY", ScaleType.FIT_XY)
            .map("matrix", ScaleType.MATRIX);

    @Override
    public boolean setAttr(V view, String attr, String value, ViewGroup parent, Map<String, String> attrs) {
        switch (attr) {
            case "radius":
                view.setCornerRadius(Dimensions.parseToPixel(value, view));
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
}
