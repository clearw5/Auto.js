package com.stardust.autojs.runtime.api.ui;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ViewGroup;

import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stardust on 2017/5/14.
 */

public interface AttributeHandler {

    boolean handle(String nodeName, Node attr, StringBuilder layoutXml);

    class AttrNameRouter implements AttributeHandler {

        private Map<String, AttributeHandler> mHandlerMap = new HashMap<>();
        private AttributeHandler mDefaultHandler;

        @Override
        public boolean handle(String nodeName, Node attr, StringBuilder layoutXml) {
            AttributeHandler handler = mHandlerMap.get(attr.getNodeName());
            if (handler == null)
                handler = mDefaultHandler;
            return handler != null && handler.handle(nodeName, attr, layoutXml);
        }

        public AttrNameRouter registerHandler(String attrName, AttributeHandler handler) {
            mHandlerMap.put(attrName, handler);
            return this;
        }

        public AttrNameRouter setDefaultHandler(AttributeHandler defaultHandler) {
            mDefaultHandler = defaultHandler;
            return this;
        }
    }

    class MappedAttributeHandler implements AttributeHandler {

        private Map<String, String> mAttrNameMap = new HashMap<>();
        private Map<String, Map<String, String>> mAttrValueMap = new HashMap<>();

        @Override
        public boolean handle(String nodeName, Node attr, StringBuilder layoutXml) {
            layoutXml.append("android:").append(mapAttrName(nodeName, attr.getNodeName()))
                    .append("=\"").append(mapAttrValue(nodeName, attr.getNodeName(), attr.getNodeValue())).append("\"\n");
            return true;
        }

        public MappedAttributeHandler putAttrNameMap(String oldAttrName, String newAttrName) {
            mAttrNameMap.put(oldAttrName, newAttrName);
            return this;
        }

        public MappedAttributeHandler putAttrValueMap(String attrName, String oldValue, String newValue) {
            Map<String, String> valueMap = mAttrValueMap.get(attrName);
            if (valueMap == null) {
                valueMap = new HashMap<>();
                mAttrValueMap.put(attrName, valueMap);
            }
            valueMap.put(oldValue, newValue);
            return this;
        }


        private String mapAttrName(String nodeName, String attrName) {
            String mappedAttrName = mAttrNameMap.get(attrName);
            if (mappedAttrName == null)
                return attrName;
            return mappedAttrName;
        }

        private String mapAttrValue(String nodeName, String attrName, String value) {
            Map<String, String> valueMap = mAttrValueMap.get(attrName);
            if (valueMap == null)
                return value;
            String mappedValue = valueMap.get(value);
            return mappedValue == null ? value : mappedValue;
        }
    }

    class IdHandler implements AttributeHandler {

        @Override
        public boolean handle(String nodeName, Node attr, StringBuilder layoutXml) {
            layoutXml.append("android:id=\"@+id/").append(attr.getNodeValue()).append("\"\n");
            return true;
        }
    }

    class DimenHandler implements AttributeHandler {

        private String mAttrName;

        public DimenHandler(String attrName) {
            mAttrName = attrName;
        }

        @Override
        public boolean handle(String nodeName, Node attr, StringBuilder layoutXml) {
            String dimen = convertToAndroidDimen(attr.getNodeName());
            layoutXml.append("android:").append(mAttrName).append("=\"").append(dimen).append("\"\n");
            return true;
        }

        static String convertToAndroidDimen(String dimen) {
            if (dimen.equals("*")) {
                return "match_parent";
            }
            if (dimen.equals("auto")) {
                return "wrap_content";
            }
            if (Character.isDigit(dimen.charAt(dimen.length() - 1))) {
                return dimen + "dp";
            }
            return dimen;
        }
    }
}
