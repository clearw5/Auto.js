package com.stardust.autojs.core.ui.inflater;

import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;


import org.w3c.dom.Node;

import java.util.Map;

/**
 * Created by Stardust on 2017/11/3.
 */


public interface ViewInflater<V extends View> {

    boolean setAttr(V view, String attrName, String value, ViewGroup parent, Map<String, String> attrs);

    boolean setAttr(V view, String ns, String attrName, String value, ViewGroup parent, Map<String, String> attrs);

    void applyPendingAttributes(V view, ViewGroup parent);

    boolean inflateChildren(DynamicLayoutInflater inflater, Node node, V parent);

    @Nullable
    ViewCreator<? super V> getCreator();
}