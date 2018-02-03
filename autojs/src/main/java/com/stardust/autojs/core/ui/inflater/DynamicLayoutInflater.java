package com.stardust.autojs.core.ui.inflater;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.stardust.autojs.core.ui.inflater.attrsetter.BaseViewAttrSetter;
import com.stardust.autojs.core.ui.inflater.attrsetter.DatePickerAttrSetter;
import com.stardust.autojs.core.ui.inflater.attrsetter.FrameLayoutAttrSetter;
import com.stardust.autojs.core.ui.inflater.attrsetter.ImageViewAttrSetter;
import com.stardust.autojs.core.ui.inflater.attrsetter.LinearLayoutAttrSetter;
import com.stardust.autojs.core.ui.inflater.attrsetter.ProgressBarAttrSetter;
import com.stardust.autojs.core.ui.inflater.attrsetter.RadioGroupAttrSetter;
import com.stardust.autojs.core.ui.inflater.attrsetter.SpinnerAttrSetter;
import com.stardust.autojs.core.ui.inflater.attrsetter.TextViewAttrSetter;
import com.stardust.autojs.core.ui.inflater.attrsetter.TimePickerAttrSetter;
import com.stardust.autojs.core.ui.inflater.attrsetter.ToolbarAttrSetter;
import com.stardust.autojs.core.ui.inflater.attrsetter.ViewGroupAttrSetter;
import com.stardust.autojs.core.ui.inflater.util.Drawables;
import com.stardust.autojs.core.ui.inflater.util.Res;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Copyright Nicholas White 2015.
 * Source: https://github.com/nickwah/DynamicLayoutInflator
 * <p>
 * Licensed under the MIT License:
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
public class DynamicLayoutInflater {
    private static final String LOG_TAG = "DynamicLayoutInflater";

    private Map<String, ViewAttrSetter<?>> mViewAttrSetters = new HashMap<>();
    private Map<String, com.stardust.autojs.core.ui.inflater.ViewCreator<?>> mViewCreators = new HashMap<>();
    private Context mContext;
    private ValueParser mValueParser;

    public DynamicLayoutInflater(ValueParser valueParser) {
        mValueParser = valueParser;
        registerViewAttrSetters();
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    protected void registerViewAttrSetters() {
        registerViewAttrSetter(TextView.class.getName(), new TextViewAttrSetter<>(mValueParser));
        registerViewAttrSetter(EditText.class.getName(), new TextViewAttrSetter<>(mValueParser));
        registerViewAttrSetter(ImageView.class.getName(), new ImageViewAttrSetter<>(mValueParser));
        registerViewAttrSetter(LinearLayout.class.getName(), new LinearLayoutAttrSetter<>(mValueParser));
        registerViewAttrSetter(FrameLayout.class.getName(), new FrameLayoutAttrSetter<>(mValueParser));
        registerViewAttrSetter(View.class.getName(), new BaseViewAttrSetter<>(mValueParser));
        registerViewAttrSetter(Toolbar.class.getName(), new ToolbarAttrSetter<>(mValueParser));
        registerViewAttrSetter(DatePicker.class.getName(), new DatePickerAttrSetter(mValueParser));
        registerViewAttrSetter(RadioGroup.class.getName(), new RadioGroupAttrSetter<>(mValueParser));
        registerViewAttrSetter(ProgressBar.class.getName(), new ProgressBarAttrSetter<>(mValueParser));
        registerViewAttrSetter(Spinner.class.getName(), new SpinnerAttrSetter(mValueParser));
        registerViewAttrSetter(TimePicker.class.getName(), new TimePickerAttrSetter(mValueParser));
    }

    public void registerViewAttrSetter(String fullName, ViewAttrSetter<?> setter) {
        mViewAttrSetters.put(fullName, setter);
        com.stardust.autojs.core.ui.inflater.ViewCreator<?> creator = setter.getCreator();
        if (creator != null) {
            mViewCreators.put(fullName, creator);
        }
    }

    public View inflate(String xml) {
        InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        return inflate(inputStream);
    }

    public View inflate(String xml, ViewGroup parent) {
        InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        return inflate(inputStream, parent);
    }

    public View inflate(InputStream inputStream) {
        return inflate(inputStream, null);
    }

    public View inflate(InputStream inputStream, ViewGroup parent) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(inputStream);
            return inflate(document.getDocumentElement(), parent);
        } catch (Exception e) {
            throw new InflateException(e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private View inflate(Node node, ViewGroup parent) {
        HashMap<String, String> attrs = getAttributesMap(node);
        View view = createViewForName(node.getNodeName(), attrs);
        if (parent != null) {
            parent.addView(view); // have to add to parent to enable certain layout attrs
        }
        ViewAttrSetter<View> setter = (ViewAttrSetter<View>) getViewAttrSetter(view);
        applyAttributes(view, setter, attrs, parent);
        if (view instanceof ViewGroup && node.hasChildNodes()) {
            inflateChildren(node, (ViewGroup) view);
            if (setter instanceof ViewGroupAttrSetter) {
                ((ViewGroupAttrSetter) setter).applyPendingAttributesAboutChildren((ViewGroup) view);
            }
        }
        return view;
    }

    @Nullable
    private ViewAttrSetter<?> getViewAttrSetter(View view) {
        ViewAttrSetter<?> setter = mViewAttrSetters.get(view.getClass().getName());
        Class c = view.getClass();
        while (setter == null && c != View.class) {
            c = c.getSuperclass();
            setter = mViewAttrSetters.get(c.getName());
        }
        return setter;
    }

    private void inflateChildren(Node node, ViewGroup parent) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() != Node.ELEMENT_NODE) continue;
            inflate(currentNode, parent); // this recursively can call inflateChildren
        }
    }

    private View createViewForName(String name, HashMap<String, String> attrs) {
        try {
            if (name.equals("View")) {
                return new View(mContext);
            }
            if (!name.contains(".")) {
                name = "android.widget." + name;
            }
            ViewCreator<?> creator = mViewCreators.get(name);
            if (creator != null) {
                return creator.create(mContext, attrs);
            }
            Class<?> clazz = Class.forName(name);
            String style = attrs.get("style");
            if (style == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                return (View) clazz.getConstructor(Context.class).newInstance(mContext);
            } else {
                int styleRes = Res.parseStyle(mContext, style);
                return (View) clazz.getConstructor(Context.class, AttributeSet.class, int.class, int.class)
                        .newInstance(mContext, null, 0, styleRes);
            }
        } catch (Exception e) {
            throw new InflateException(e);
        }
    }


    private HashMap<String, String> getAttributesMap(Node currentNode) {
        NamedNodeMap attributeMap = currentNode.getAttributes();
        int attributeCount = attributeMap.getLength();
        HashMap<String, String> attributes = new HashMap<>(attributeCount);
        for (int j = 0; j < attributeCount; j++) {
            Node attr = attributeMap.item(j);
            String nodeName = attr.getNodeName();
            attributes.put(nodeName, attr.getNodeValue());
        }
        return attributes;
    }

    @SuppressWarnings("unchecked")
    private void applyAttributes(View view, ViewAttrSetter<View> setter, Map<String, String> attrs, ViewGroup parent) {
        if (setter != null) {
            for (Map.Entry<String, String> entry : attrs.entrySet()) {
                String[] attr = entry.getKey().split(":");
                if (attr.length == 1) {
                    setter.setAttr(view, attr[0], entry.getValue(), parent, attrs);
                } else if (attr.length == 2) {
                    setter.setAttr(view, attr[0], attr[1], entry.getValue(), parent, attrs);
                } else {
                    throw new InflateException("illegal attr name: " + entry.getKey());
                }
            }
            setter.applyPendingAttributes(view, parent);
        } else {
            Log.e(LOG_TAG, "cannot set attributes for view: " + view.getClass());
        }

    }


}
