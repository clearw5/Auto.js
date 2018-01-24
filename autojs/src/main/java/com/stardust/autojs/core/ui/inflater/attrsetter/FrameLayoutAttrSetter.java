package com.stardust.autojs.core.ui.inflater.attrsetter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.stardust.autojs.core.ui.inflater.ValueParser;
import com.stardust.autojs.core.ui.inflater.util.Gravities;

import java.util.Map;

/**
 * Created by Stardust on 2017/11/29.
 */

public class FrameLayoutAttrSetter<V extends FrameLayout> extends ViewGroupAttrSetter<V> {

    private Integer mGravity;

    public FrameLayoutAttrSetter(ValueParser valueParser) {
        super(valueParser);
    }

    @Override
    public boolean setAttr(V view, String attr, String value, ViewGroup parent, Map<String, String> attrs) {
        if (attr.equals("gravity")) {
            mGravity = Gravities.parse(value);
            return true;
        }
        return super.setAttr(view, attr, value, parent, attrs);
    }

    @Override
    public void applyPendingAttributesAboutChildren(V view) {
        if (mGravity == null)
            return;
        for (int i = 0; i < view.getChildCount(); i++) {
            View child = view.getChildAt(i);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) child.getLayoutParams();
            params.gravity = mGravity;
            child.setLayoutParams(params);
        }
        mGravity = null;
    }
}
