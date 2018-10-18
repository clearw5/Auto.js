package com.stardust.autojs.core.ui.attribute;

import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ImageView;

import com.stardust.autojs.core.ui.inflater.ResourceParser;

public class FabViewAttributes extends ImageViewAttributes {

    public FabViewAttributes(ResourceParser resourceParser, View view) {
        super(resourceParser, view);
    }

    @Override
    protected void onRegisterAttrs() {
        super.onRegisterAttrs();
        registerPixelAttr("elevation", getView()::setCompatElevation);
        registerIntPixelAttr("fabCustomSize", getView()::setCustomSize);
        registerIntPixelAttr("fabSize", getView()::setSize);
        registerAttr("rippleColor", Color::parseColor, getView()::setRippleColor);
        registerBooleanAttr("useCompatPadding", getView()::setUseCompatPadding);
    }

    @Override
    public FloatingActionButton getView() {
        return (FloatingActionButton) super.getView();
    }


}
