package com.stardust.autojs.core.ui;

import android.content.Context;
import android.view.View;

import com.stardust.autojs.core.ui.inflater.DynamicLayoutInflater;
import com.stardust.autojs.core.ui.widget.JsFrameLayout;
import com.stardust.autojs.core.ui.xml.XmlConverter;


/**
 * Created by Stardust on 2017/5/14.
 */

public class ConvertLayoutInflater implements JsLayoutInflater {

    private DynamicLayoutInflater mDynamicLayoutInflater;

    public ConvertLayoutInflater(DynamicLayoutInflater dynamicLayoutInflater) {
        mDynamicLayoutInflater = dynamicLayoutInflater;
    }

    @Override
    public View inflate(Context context, String xml) {
        try {
            String androidLayoutXml = XmlConverter.convertToAndroidLayout(xml);
            JsFrameLayout root = new JsFrameLayout(context);
            mDynamicLayoutInflater.setContext(context);
            mDynamicLayoutInflater.inflate(androidLayoutXml, root);
            return root;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
