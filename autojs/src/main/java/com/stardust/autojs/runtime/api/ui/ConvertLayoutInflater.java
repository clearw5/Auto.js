package com.stardust.autojs.runtime.api.ui;

import android.content.Context;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.nickandjerry.dynamiclayoutinflator.lib.DynamicLayoutInflator;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

/**
 * Created by Stardust on 2017/5/14.
 */

public class ConvertLayoutInflater implements JsLayoutInflater {


    @Override
    public View inflate(Context context, String xml) {
        try {
            String androidLayoutXml = XmlConverter.convertToAndroidLayout(xml);
            FrameLayout root = new FrameLayout(context);
            DynamicLayoutInflator.inflate(context, androidLayoutXml, root);
            return root;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
