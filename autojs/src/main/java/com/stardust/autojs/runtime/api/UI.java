package com.stardust.autojs.runtime.api;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.View;

import com.stardust.autojs.core.graphics.ScriptCanvasView;
import com.stardust.autojs.core.ui.inflater.DynamicLayoutInflater;
import com.stardust.autojs.core.ui.inflater.ResourceParser;
import com.stardust.autojs.core.ui.inflater.inflaters.CanvasViewInflater;
import com.stardust.autojs.core.ui.inflater.inflaters.JsGridViewInflater;
import com.stardust.autojs.core.ui.inflater.inflaters.JsImageViewInflater;
import com.stardust.autojs.core.ui.inflater.inflaters.JsListViewInflater;
import com.stardust.autojs.core.ui.widget.JsGridView;
import com.stardust.autojs.core.ui.widget.JsImageView;
import com.stardust.autojs.core.ui.widget.JsListView;
import com.stardust.autojs.rhino.ProxyObject;
import com.stardust.autojs.runtime.ScriptRuntime;

import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.UniqueTag;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Stardust on 2017/5/14.
 */

public class UI extends ProxyObject {


    private Context mContext;
    private Map<String, Object> mProperties = new ConcurrentHashMap<>();
    private DynamicLayoutInflater mDynamicLayoutInflater;
    private ScriptRuntime mRuntime;
    private ResourceParser mResourceParser;

    public UI(Context context, ScriptRuntime runtime) {
        mContext = context;
        mRuntime = runtime;
        mResourceParser = new ResourceParser(new Drawables());
        mDynamicLayoutInflater = new DynamicLayoutInflater(mResourceParser);
        mDynamicLayoutInflater.setContext(context);
        mDynamicLayoutInflater.registerViewAttrSetter(JsImageView.class.getName(),
                new JsImageViewInflater(mResourceParser));
        mDynamicLayoutInflater.registerViewAttrSetter(JsListView.class.getName(),
                new JsListViewInflater(mResourceParser, runtime));
        mDynamicLayoutInflater.registerViewAttrSetter(JsGridView.class.getName(),
                new JsGridViewInflater(mResourceParser, runtime));
        mDynamicLayoutInflater.registerViewAttrSetter(ScriptCanvasView.class.getName(),
                new CanvasViewInflater(mResourceParser, runtime));
        mProperties.put("layoutInflater", this.mDynamicLayoutInflater);
    }

    @SuppressWarnings("unchecked")
    public static <V extends View> V unwrapJsViewObject(@Nullable NativeObject object, Class<V> c) {
        if (object == null)
            return null;
        if (!object.containsKey("__javaObject__")) {
            throw new ClassCastException("object " + object + " cannot be cast to " + c.getName());
        }
        Object view = object.get("__javaObject__");
        if (!c.isInstance(view)) {
            throw new ClassCastException("object " + object + " cannot be cast to " + c.getName());
        }
        return (V) view;

    }

    public Object getBindingContext() {
        return mProperties.get("bindingContext");
    }

    public void setBindingContext(Object context) {
        if (context == null)
            mProperties.remove("bindingContext");
        else
            mProperties.put("bindingContext", context);
    }


    public DynamicLayoutInflater getLayoutInflater() {
        return mDynamicLayoutInflater;
    }

    @Override
    public String getClassName() {
        return UI.class.getSimpleName();
    }


    @Override
    public Object get(String name, Scriptable start) {
        Object value = mProperties.get(name);
        if (value != null)
            return value;
        return super.get(name, start);
    }

    @Override
    public void put(String name, Scriptable start, Object value) {
        if (mProperties.containsKey(name)) {
            if (value == null) {
                mProperties.remove(name);
            } else {
                mProperties.put(name, value);
            }
        } else {
            super.put(name, start, value);
        }
    }

    private class Drawables extends com.stardust.autojs.core.ui.inflater.util.Drawables {

        @Override
        public Drawable decodeImage(String path) {
            return super.decodeImage(mRuntime.files.path(path));
        }
    }


}
