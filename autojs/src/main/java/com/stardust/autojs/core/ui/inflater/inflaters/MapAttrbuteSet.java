package com.stardust.autojs.core.ui.inflater.inflaters;

import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Stardust on 2017/11/5.
 */

public class MapAttrbuteSet implements AttributeSet {
    private Map<String, String> mMap;
    private ArrayList<String> mKeys;
    private ArrayList<String> mValues;

    public MapAttrbuteSet(Map<String, String> map) {
        mMap = map;
        mKeys = new ArrayList<>(map.size());
        mValues = new ArrayList<>(map.size());
        for (Map.Entry<String, String> entry : map.entrySet()) {
            mKeys.add(entry.getKey());
            mValues.add(entry.getValue());
        }
    }

    @Override
    public int getAttributeCount() {
        return mKeys.size();
    }

    @Override
    public String getAttributeName(int index) {
        return mKeys.get(index);
    }

    @Override
    public String getAttributeValue(int index) {
        return mValues.get(index);
    }

    @Override
    public String getAttributeValue(String namespace, String name) {
        return mMap.get(name);
    }

    @Override
    public String getPositionDescription() {
        return null;
    }

    @Override
    public int getAttributeNameResource(int index) {
        return 0;
    }

    @Override
    public int getAttributeListValue(String namespace, String attribute, String[] options, int defaultValue) {
        return 0;
    }

    @Override
    public boolean getAttributeBooleanValue(String namespace, String attribute, boolean defaultValue) {
        String v = mMap.get(attribute);
        return v == null ? defaultValue : Boolean.valueOf(v);

    }

    @Override
    public int getAttributeResourceValue(String namespace, String attribute, int defaultValue) {
        return 0;
    }

    @Override
    public int getAttributeIntValue(String namespace, String attribute, int defaultValue) {
        return 0;
    }

    @Override
    public int getAttributeUnsignedIntValue(String namespace, String attribute, int defaultValue) {
        return 0;
    }

    @Override
    public float getAttributeFloatValue(String namespace, String attribute, float defaultValue) {
        return 0;
    }

    @Override
    public int getAttributeListValue(int index, String[] options, int defaultValue) {
        return 0;
    }

    @Override
    public boolean getAttributeBooleanValue(int index, boolean defaultValue) {
        return false;
    }

    @Override
    public int getAttributeResourceValue(int index, int defaultValue) {
        return 0;
    }

    @Override
    public int getAttributeIntValue(int index, int defaultValue) {
        return 0;
    }

    @Override
    public int getAttributeUnsignedIntValue(int index, int defaultValue) {
        return 0;
    }

    @Override
    public float getAttributeFloatValue(int index, float defaultValue) {
        return 0;
    }

    @Override
    public String getIdAttribute() {
        return null;
    }

    @Override
    public String getClassAttribute() {
        return null;
    }

    @Override
    public int getIdAttributeResourceValue(int defaultValue) {
        return 0;
    }

    @Override
    public int getStyleAttribute() {
        return 0;
    }
}
