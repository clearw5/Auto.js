package com.stardust.autojs.runtime.api.ui;

import android.content.Context;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

/**
 * Created by Stardust on 2017/5/14.
 */

public class ConvertLayoutInflater implements JsLayoutInflater {

    private Context mContext;

    public ConvertLayoutInflater(Context context) {
        mContext = context;
    }

    @Override
    public View inflate(String xml) {
        try {
            // 我靠%>_<% 弄完了xml转换以后发现android并不能动态inflate非resources的xml啊啊啊啊啊啊
            String androidLayoutXml = XmlConverter.convertToAndroidLayout(xml);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            parser.setInput(new StringReader(androidLayoutXml));
            return inflater.inflate(parser, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
