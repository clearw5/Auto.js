package com.stardust.autojs.core.ui.inflater;

import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.appbar.AppBarLayout;
import com.stardust.autojs.core.ui.inflater.inflaters.AppBarInflater;
import com.stardust.autojs.core.ui.inflater.inflaters.BaseViewInflater;
import com.stardust.autojs.core.ui.inflater.inflaters.DatePickerInflater;
import com.stardust.autojs.core.ui.inflater.inflaters.FrameLayoutInflater;
import com.stardust.autojs.core.ui.inflater.inflaters.ImageViewInflater;
import com.stardust.autojs.core.ui.inflater.inflaters.LinearLayoutInflater;
import com.stardust.autojs.core.ui.inflater.inflaters.ProgressBarInflater;
import com.stardust.autojs.core.ui.inflater.inflaters.RadioGroupInflater;
import com.stardust.autojs.core.ui.inflater.inflaters.SpinnerInflater;
import com.stardust.autojs.core.ui.inflater.inflaters.TabLayoutInflater;
import com.stardust.autojs.core.ui.inflater.inflaters.TextViewInflater;
import com.stardust.autojs.core.ui.inflater.inflaters.TimePickerInflater;
import com.stardust.autojs.core.ui.inflater.inflaters.ToolbarInflater;
import com.stardust.autojs.core.ui.inflater.inflaters.ViewGroupInflater;
import com.stardust.autojs.core.ui.inflater.util.Res;
import com.stardust.autojs.core.ui.widget.JsSpinner;
import com.stardust.autojs.core.ui.widget.JsTabLayout;
import com.stardust.autojs.core.ui.widget.JsToolbar;
import com.stardust.autojs.core.ui.xml.XmlConverter;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class DynamicLayoutInflater {

    public static final int FLAG_DEFAULT = 0;
    public static final int FLAG_IGNORES_DYNAMIC_ATTRS = 1;
    public static final int FLAG_JUST_DYNAMIC_ATTRS = 2;

    private static final String LOG_TAG = "DynamicLayoutInflater";

    private Map<String, ViewInflater<?>> mViewAttrSetters = new HashMap<>();
    private Map<String, ViewCreator<?>> mViewCreators = new HashMap<>();
    private Context mContext;
    private ResourceParser mResourceParser;
    @NonNull
    private LayoutInflaterDelegate mLayoutInflaterDelegate = LayoutInflaterDelegate.NO_OP;
    private int mInflateFlags;

    public DynamicLayoutInflater(ResourceParser resourceParser) {
        mResourceParser = resourceParser;
        registerViewAttrSetters();
    }

    @SuppressWarnings("IncompleteCopyConstructor")
    public DynamicLayoutInflater(DynamicLayoutInflater inflater) {
        this.mResourceParser = inflater.mResourceParser;
        this.mContext = inflater.mContext;
        this.mViewAttrSetters = new HashMap<>(inflater.mViewAttrSetters);
        this.mViewCreators = new HashMap<>(inflater.mViewCreators);
    }

    public int getInflateFlags() {
        return mInflateFlags;
    }

    public void setInflateFlags(int inflateFlags) {
        mInflateFlags = inflateFlags;
    }

    public ResourceParser getResourceParser() {
        return mResourceParser;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    @NonNull
    public LayoutInflaterDelegate getLayoutInflaterDelegate() {
        return mLayoutInflaterDelegate;
    }

    public void setLayoutInflaterDelegate(@Nullable LayoutInflaterDelegate layoutInflaterDelegate) {
        if (layoutInflaterDelegate == null)
            layoutInflaterDelegate = LayoutInflaterDelegate.NO_OP;
        mLayoutInflaterDelegate = layoutInflaterDelegate;
    }

    protected void registerViewAttrSetters() {
        registerViewAttrSetter(TextView.class.getName(), new TextViewInflater<>(mResourceParser));
        registerViewAttrSetter(EditText.class.getName(), new TextViewInflater<>(mResourceParser));
        registerViewAttrSetter(ImageView.class.getName(), new ImageViewInflater<>(mResourceParser));
        registerViewAttrSetter(LinearLayout.class.getName(), new LinearLayoutInflater<>(mResourceParser));
        registerViewAttrSetter(FrameLayout.class.getName(), new FrameLayoutInflater<>(mResourceParser));
        registerViewAttrSetter(View.class.getName(), new BaseViewInflater<>(mResourceParser));
        registerViewAttrSetter(JsToolbar.class.getName(), new ToolbarInflater<>(mResourceParser));
        registerViewAttrSetter(DatePicker.class.getName(), new DatePickerInflater(mResourceParser));
        registerViewAttrSetter(RadioGroup.class.getName(), new RadioGroupInflater<>(mResourceParser));
        registerViewAttrSetter(ProgressBar.class.getName(), new ProgressBarInflater<>(mResourceParser));
        registerViewAttrSetter(JsSpinner.class.getName(), new SpinnerInflater(mResourceParser));
        registerViewAttrSetter(TimePicker.class.getName(), new TimePickerInflater(mResourceParser));
        registerViewAttrSetter(AppBarLayout.class.getName(), new AppBarInflater<>(mResourceParser));
        registerViewAttrSetter(JsTabLayout.class.getName(), new TabLayoutInflater<>(mResourceParser));
    }

    public void registerViewAttrSetter(String fullName, ViewInflater<?> inflater) {
        mViewAttrSetters.put(fullName, inflater);
        ViewCreator<?> creator = inflater.getCreator();
        if (creator != null) {
            mViewCreators.put(fullName, creator);
        }
    }

    public View inflate(String xml) {
        return inflate(xml, null);
    }

    public View inflate(String xml, @Nullable ViewGroup parent) {
        return inflate(xml, parent, parent != null);
    }

    public View inflate(String xml, @Nullable ViewGroup parent, boolean attachToParent) {
        InflateContext context = newInflateContext();
        return inflate(context, xml, parent, attachToParent);
    }

    public View inflate(InflateContext context, String xml, @Nullable ViewGroup parent, boolean attachToParent) {
        View view = mLayoutInflaterDelegate.beforeInflation(context, xml, parent);
        if (view != null)
            return view;
        xml = convertXml(context, xml);
        return mLayoutInflaterDelegate.afterInflation(context, doInflation(context, xml, parent, attachToParent), xml, parent);
    }

    public InflateContext newInflateContext(){
        return new InflateContext();
    }


    protected View doInflation(InflateContext context, String xml, @Nullable ViewGroup parent, boolean attachToParent) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(new ByteArrayInputStream(xml.getBytes()));
            return inflate(context, document.getDocumentElement(), parent, attachToParent);
        } catch (Exception e) {
            throw new InflateException(e);
        }
    }

    protected String convertXml(InflateContext context, String xml) {
        String str = mLayoutInflaterDelegate.beforeConvertXml(context, xml);
        if (str != null)
            return str;
        try {
            return mLayoutInflaterDelegate.afterConvertXml(context, XmlConverter.convertToAndroidLayout(xml));
        } catch (Exception e) {
            throw new InflateException(e);
        }
    }

    public View inflate(InflateContext context, Node node, @Nullable ViewGroup parent, boolean attachToParent) {
        View view = doInflation(context, node, parent, attachToParent);
        if (view instanceof ShouldCallOnFinishInflate) {
            ((ShouldCallOnFinishInflate) view).onFinishDynamicInflate();
        }
        return view;
    }

    protected View doInflation(InflateContext context, Node node, @Nullable ViewGroup parent, boolean attachToParent) {
        View view = mLayoutInflaterDelegate.beforeInflateView(context, node, parent, attachToParent);
        if (view != null)
            return view;
        HashMap<String, String> attrs = getAttributesMap(node);
        view = doCreateView(context, node, node.getNodeName(), parent, attrs);
        if (parent != null) {
            parent.addView(view); // have to add to parent to generate layout params
            if (!attachToParent) {
                parent.removeView(view);
            }
        }
        ViewInflater<View> inflater = applyAttributes(context, view, attrs, parent);
        if (view instanceof ViewGroup && node.hasChildNodes()) {
            inflateChildren(context, inflater, node, (ViewGroup) view);
            if (inflater instanceof ViewGroupInflater) {
                applyPendingAttributesOfChildren(context, (ViewGroupInflater) inflater, (ViewGroup) view);
            }
        }
        return mLayoutInflaterDelegate.afterInflateView(context, view, node, parent, attachToParent);
    }

    @SuppressWarnings("unchecked")
    protected void applyPendingAttributesOfChildren(InflateContext context, ViewGroupInflater inflater, ViewGroup view) {
        if (mLayoutInflaterDelegate.beforeApplyPendingAttributesOfChildren(context, inflater, view)) {
            return;
        }
        inflater.applyPendingAttributesOfChildren(view);
        mLayoutInflaterDelegate.afterApplyPendingAttributesOfChildren(context, inflater, view);

    }


    @SuppressWarnings("unchecked")
    public ViewInflater<View> applyAttributes(InflateContext context, View view, HashMap<String, String> attrs, @Nullable ViewGroup parent) {
        ViewInflater<View> inflater = (ViewInflater<View>) getViewInflater(view);
        if (mLayoutInflaterDelegate.beforeApplyAttributes(context, view, inflater, attrs, parent)) {
            return inflater;
        }
        applyAttributes(context, view, inflater, attrs, parent);
        mLayoutInflaterDelegate.afterApplyAttributes(context, view, inflater, attrs, parent);
        return inflater;
    }

    @Nullable
    public ViewInflater<?> getViewInflater(View view) {
        ViewInflater<?> setter = mViewAttrSetters.get(view.getClass().getName());
        Class c = view.getClass();
        while (setter == null && c != View.class) {
            c = c.getSuperclass();
            setter = mViewAttrSetters.get(c.getName());
        }
        return setter;
    }

    protected void inflateChildren(InflateContext context, ViewInflater<View> inflater, Node node, ViewGroup parent) {
        if (mLayoutInflaterDelegate.beforeInflateChildren(context, inflater, node, parent)) {
            return;
        }
        if (inflater.inflateChildren(this, node, parent)) {
            return;
        }
        inflateChildren(context, node, parent);
        mLayoutInflaterDelegate.afterInflateChildren(context, inflater, node, parent);
    }

    public void inflateChildren(InflateContext context, Node node, ViewGroup parent) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() != Node.ELEMENT_NODE) continue;
            inflate(context, currentNode, parent, true);
        }
    }

    protected View doCreateView(InflateContext context, Node node, String viewName, ViewGroup parent, HashMap<String, String> attrs) {
        View view = mLayoutInflaterDelegate.beforeCreateView(context, node, viewName, parent, attrs);
        if (view != null)
            return view;
        return mLayoutInflaterDelegate.afterCreateView(context, createViewForName(viewName, attrs), node, viewName, parent, attrs);
    }

    public View createViewForName(String name, HashMap<String, String> attrs) {
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


    public HashMap<String, String> getAttributesMap(Node currentNode) {
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
    protected void applyAttributes(InflateContext context, View view, ViewInflater<View> setter, Map<String, String> attrs, @Nullable ViewGroup parent) {
        if (setter != null) {
            for (Map.Entry<String, String> entry : attrs.entrySet()) {
                String[] attr = entry.getKey().split(":");
                if (attr.length == 1) {
                    applyAttribute(context, setter, view, null, attr[0], entry.getValue(), parent, attrs);
                } else if (attr.length == 2) {
                    applyAttribute(context, setter, view, attr[0], attr[1], entry.getValue(), parent, attrs);
                } else {
                    throw new InflateException("illegal attr name: " + entry.getKey());
                }
            }
            setter.applyPendingAttributes(view, parent);
        } else {
            Log.e(LOG_TAG, "cannot set attributes for view: " + view.getClass());
        }

    }

    protected void applyAttribute(InflateContext context, ViewInflater<View> inflater, View view, String ns, String attrName, String value, ViewGroup parent, Map<String, String> attrs) {
        if (mLayoutInflaterDelegate.beforeApplyAttribute(context, inflater, view, ns, attrName, value, parent, attrs)) {
            return;
        }
        boolean isDynamic = isDynamicValue(value);
        if ((isDynamic && mInflateFlags == FLAG_IGNORES_DYNAMIC_ATTRS)
                || (!isDynamic && mInflateFlags == FLAG_JUST_DYNAMIC_ATTRS)) {
            return;
        }
        inflater.setAttr(view, ns, attrName, value, parent, attrs);
        mLayoutInflaterDelegate.afterApplyAttribute(context, inflater, view, ns, attrName, value, parent, attrs);

    }

    public boolean isDynamicValue(String value) {
        int i = value.indexOf("{{");
        if (i < 0)
            return false;
        return value.indexOf("}}", i + 1) >= 0;
    }


}
