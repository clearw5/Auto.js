package com.stardust.autojs.runtime.api;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.stardust.autojs.core.ui.ConvertLayoutInflater;
import com.stardust.autojs.core.ui.JsLayoutInflater;
import com.stardust.autojs.core.ui.inflater.DynamicLayoutInflater;
import com.stardust.autojs.core.ui.inflater.ValueParser;
import com.stardust.autojs.core.ui.inflater.attrsetter.JsImageViewAttrSetter;
import com.stardust.autojs.core.ui.widget.JsImageView;
import com.stardust.autojs.rhino.ProxyObject;
import com.stardust.autojs.runtime.ScriptRuntime;

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
    private JsLayoutInflater mJsLayoutInflater;
    private ScriptRuntime mRuntime;
    private ValueParser mValueParser;

    public UI(Context context, ScriptRuntime runtime) {
        mContext = context;
        mRuntime = runtime;
        mValueParser = new ValueParser(new Drawables());
        DynamicLayoutInflater inflater = new DynamicLayoutInflater(mValueParser);
        inflater.registerViewAttrSetter(JsImageView.class.getName(),
                new JsImageViewAttrSetter(mValueParser));
        mJsLayoutInflater = new ConvertLayoutInflater(inflater);
        mProperties.put("layoutInflater", mJsLayoutInflater);
    }


    public JsLayoutInflater getJsLayoutInflater() {
        return mJsLayoutInflater;
    }

    @Override
    public String getClassName() {
        return UI.class.getSimpleName();
    }


    @Override
    public Object get(String name, Scriptable start) {
        Object value = super.get(name, start);
        if (value != null && value != UniqueTag.NOT_FOUND && !value.equals(org.mozilla.javascript.Context.getUndefinedValue())) {
            return value;
        }
        value = mProperties.get(name);
        if (value != null)
            return value;
        return UniqueTag.NOT_FOUND;
    }


    private class Drawables extends com.stardust.autojs.core.ui.inflater.util.Drawables {

        @Override
        public Drawable decodeImage(String path) {
            return super.decodeImage(mRuntime.files.path(path));
        }
    }


}
