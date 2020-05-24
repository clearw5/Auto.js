package com.stardust.autojs.core.ui.attribute;

import android.graphics.Color;
import android.graphics.PorterDuff;

import androidx.core.widget.ImageViewCompat;
import android.view.View;
import android.widget.ImageView;

import com.stardust.autojs.core.ui.inflater.ResourceParser;
import com.stardust.util.BiMap;
import com.stardust.util.BiMaps;

import static com.stardust.autojs.core.ui.inflater.inflaters.BaseViewInflater.TINT_MODES;

public class ImageViewAttributes extends ViewAttributes {

    private static final BiMap<String, ImageView.ScaleType> SCALE_TYPES = BiMaps.<String, ImageView.ScaleType>newBuilder()
            .put("center", ImageView.ScaleType.CENTER)
            .put("centerCrop", ImageView.ScaleType.CENTER_CROP)
            .put("centerInside", ImageView.ScaleType.CENTER_INSIDE)
            .put("fitCenter", ImageView.ScaleType.FIT_CENTER)
            .put("fitEnd", ImageView.ScaleType.FIT_END)
            .put("fitStart", ImageView.ScaleType.FIT_START)
            .put("fitXY", ImageView.ScaleType.FIT_XY)
            .put("matrix", ImageView.ScaleType.MATRIX)
            .build();

    public ImageViewAttributes(ResourceParser resourceParser, View view) {
        super(resourceParser, view);
    }

    @Override
    protected void onRegisterAttrs() {
        super.onRegisterAttrs();
        registerBooleanAttr("adjustViewBounds", getView()::setAdjustViewBounds);
        registerIntPixelAttr("baseline", getView()::setBaseline);
        registerBooleanAttr("baselineAlignBottom", getView()::setBaselineAlignBottom);
        registerBooleanAttr("cropToPadding", getView()::setCropToPadding);
        registerIntPixelAttr("maxHeight", getView()::setMaxHeight);
        registerIntPixelAttr("maxWidth", getView()::setMaxWidth);
        registerAttr("path", value ->
                getDrawables().setupWithImage(getView(), wrapAsPath(value))
        );
        registerAttr("scaleType", getView()::getScaleType, getView()::setScaleType, SCALE_TYPES);
        registerAttr("src", value ->
                getDrawables().setupWithImage(getView(), value)
        );
        registerAttr("tint", Color::parseColor, value -> {
            // FIXME: 2018/10/13 setImageTineList not working
            PorterDuff.Mode mode = ImageViewCompat.getImageTintMode(getView());
            getView().setColorFilter(value, mode == null ? PorterDuff.Mode.SRC_ATOP : mode);
        });
        registerAttr("tintMode", TINT_MODES::get, value -> {
            ImageViewCompat.setImageTintMode(getView(), value);
        });
        registerAttr("url", value ->
                getDrawables().setupWithImage(getView(), wrapAsUrl(value))
        );
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

    @Override
    public ImageView getView() {
        return (ImageView) super.getView();
    }
}
