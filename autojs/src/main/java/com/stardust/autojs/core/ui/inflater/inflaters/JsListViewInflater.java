package com.stardust.autojs.core.ui.inflater.inflaters;

import androidx.annotation.Nullable;
import android.view.ViewGroup;

import com.stardust.autojs.core.ui.inflater.DynamicLayoutInflater;
import com.stardust.autojs.core.ui.inflater.ResourceParser;
import com.stardust.autojs.core.ui.inflater.ViewCreator;
import com.stardust.autojs.core.ui.widget.JsListView;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.workground.WrapContentLinearLayoutManager;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Map;

/**
 * Created by Stardust on 2018/3/28.
 */

public class JsListViewInflater<V extends JsListView> extends BaseViewInflater<V> {

    private final ScriptRuntime mRuntime;

    public JsListViewInflater(ResourceParser resourceParser, ScriptRuntime runtime) {
        super(resourceParser);
        mRuntime = runtime;
    }

    public ScriptRuntime getRuntime() {
        return mRuntime;
    }

    @Override
    public boolean setAttr(V view, String attr, String value, ViewGroup parent, Map<String, String> attrs) {
        switch (attr) {
            case "orientation":
                view.setLayoutManager(new WrapContentLinearLayoutManager(view.getContext(), LinearLayoutInflater.ORIENTATIONS.get(value), false));
                return true;
            default:
                return super.setAttr(view, attr, value, parent, attrs);
        }
    }

    @Override
    public boolean setAttr(V view, String ns, String attrName, String value, ViewGroup parent, Map<String, String> attrs) {
        return super.setAttr(view, ns, attrName, value, parent, attrs);
    }

    @Override
    public boolean inflateChildren(DynamicLayoutInflater inflater, Node node, JsListView parent) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node child = nodeList.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) continue;
            parent.setItemTemplate(inflater, child);
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public ViewCreator<? super JsListView> getCreator() {
        return (context, attrs) -> new JsListView(context, mRuntime);
    }
}
