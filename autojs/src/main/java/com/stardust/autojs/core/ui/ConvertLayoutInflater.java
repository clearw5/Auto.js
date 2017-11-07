package com.stardust.autojs.core.ui;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nickandjerry.dynamiclayoutinflator.lib.DynamicLayoutInflater;
import com.nickandjerry.dynamiclayoutinflator.lib.util.Drawables;
import com.stardust.autojs.core.ui.widget.JsFrameLayout;
import com.stardust.autojs.core.ui.xml.XmlConverter;
import com.stardust.util.MapEntries;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Created by Stardust on 2017/5/14.
 */

public class ConvertLayoutInflater implements JsLayoutInflater {


    @Override
    public View inflate(Context context, String xml) {
        try {
            String androidLayoutXml = XmlConverter.convertToAndroidLayout(xml);
            JsFrameLayout root = new JsFrameLayout(context);
            new DynamicLayoutInflater(context).inflate(androidLayoutXml, root);
            return root;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
