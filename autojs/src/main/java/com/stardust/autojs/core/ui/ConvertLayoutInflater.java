package com.stardust.autojs.core.ui;

import android.content.Context;
import android.view.View;

import com.stardust.autojs.core.ui.inflater.DynamicLayoutInflater;
import com.stardust.autojs.core.ui.widget.JsFrameLayout;
import com.stardust.autojs.core.ui.widget.JsImageView;
import com.stardust.autojs.core.ui.inflater.attrsetter.JsImageViewAttrSetter;
import com.stardust.autojs.core.ui.xml.XmlConverter;


/**
 * Created by Stardust on 2017/5/14.
 */

public class ConvertLayoutInflater implements JsLayoutInflater {

    private DynamicLayoutInflater mDynamicLayoutInflater;

    public ConvertLayoutInflater() {

    }

    private void ensureInflater(Context context) {
        if (mDynamicLayoutInflater != null) {
            return;
        }
        mDynamicLayoutInflater = new DynamicLayoutInflater(context);
        mDynamicLayoutInflater.registerViewAttrSetter(JsImageView.class.getName(),
                new JsImageViewAttrSetter<>());
    }

    @Override
    public View inflate(Context context, String xml) {
        ensureInflater(context);
        try {
            String androidLayoutXml = XmlConverter.convertToAndroidLayout(xml);
            JsFrameLayout root = new JsFrameLayout(context);
            mDynamicLayoutInflater.inflate(androidLayoutXml, root);
            return root;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
