package com.stardust.autojs.core.ui.inflater.inflaters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.stardust.autojs.core.ui.inflater.ResourceParser;
import com.stardust.autojs.core.ui.inflater.util.Gravities;

import java.util.Map;

/**
 * Created by Stardust on 2017/11/29.
 */

public class FrameLayoutInflater<V extends FrameLayout> extends ViewGroupInflater<V> {

    private Integer mGravity;

    public FrameLayoutInflater(ResourceParser resourceParser) {
        super(resourceParser);
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
    public void applyPendingAttributesOfChildren(V view) {
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
