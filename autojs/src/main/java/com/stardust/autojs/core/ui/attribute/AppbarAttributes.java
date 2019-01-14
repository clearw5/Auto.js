package com.stardust.autojs.core.ui.attribute;

import android.view.View;

import com.google.android.material.appbar.AppBarLayout;
import com.stardust.autojs.core.ui.inflater.ResourceParser;

public class AppbarAttributes extends ViewAttributes {
    public AppbarAttributes(ResourceParser resourceParser, View view) {
        super(resourceParser, view);
    }

    @Override
    protected void onRegisterAttrs() {
        super.onRegisterAttrs();
        registerPixelAttr("elevation", getView()::setTargetElevation);
        registerBooleanAttr("expanded", getView()::setExpanded);
    }

    @Override
    public AppBarLayout getView() {
        return (AppBarLayout) super.getView();
    }
}
