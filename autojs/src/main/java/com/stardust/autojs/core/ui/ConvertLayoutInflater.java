package com.stardust.autojs.core.ui;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nickandjerry.dynamiclayoutinflator.lib.DynamicLayoutInflator;
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

    static {
        DynamicLayoutInflator.createViewRunnables();
        DynamicLayoutInflator.viewRunnables.put("inputType", new DynamicLayoutInflator.ViewParamRunnable() {

            private final Map<String, Integer> mInputTypes = new MapEntries<String, Integer>()
                    .entry("text", InputType.TYPE_CLASS_TEXT)
                    .entry("number", InputType.TYPE_CLASS_NUMBER)
                    .entry("phone", InputType.TYPE_CLASS_PHONE)
                    .entry("datetime", InputType.TYPE_CLASS_DATETIME)
                    .entry("password", InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT)
                    .entry("email", InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_CLASS_TEXT)
                    .entry("date", InputType.TYPE_DATETIME_VARIATION_DATE | InputType.TYPE_CLASS_DATETIME)
                    .entry("time", InputType.TYPE_DATETIME_VARIATION_TIME | InputType.TYPE_CLASS_DATETIME)
                    .map();

            @Override
            public void apply(View view, String value, ViewGroup parent, Map<String, String> attrs) {
                if (!(view instanceof TextView))
                    return;
                TextView textView = (TextView) view;
                String[] types = value.split("[|]");
                int inputType = 0;
                for (String type : types) {
                    if (mInputTypes.containsKey(type)) {
                        inputType |= mInputTypes.get(type);
                    }
                }
                if (inputType != 0)
                    textView.setInputType(inputType);
            }
        });
    }

    @Override
    public View inflate(Context context, String xml) {
        try {
            String androidLayoutXml = XmlConverter.convertToAndroidLayout(xml);
            JsFrameLayout root = new JsFrameLayout(context);
            DynamicLayoutInflator.inflate(context, androidLayoutXml, root);
            return root;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
