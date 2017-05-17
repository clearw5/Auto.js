package com.stardust.autojs.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.UniqueTag;

/**
 * Created by Stardust on 2017/5/17.
 */

public class ProxyObject extends NativeObject {

    private NativeFunction mGetter;
    private NativeFunction mSetter;

    @Override
    public void put(String name, Scriptable start, Object value) {
        if (name.equals("__proxy__")) {
            NativeObject proxy = (NativeObject) value;
            Object getter = proxy.get("get", start);
            if (getter instanceof NativeFunction) {
                mGetter = (NativeFunction) getter;
            }
            Object setter = proxy.get("set", start);
            if (setter instanceof NativeFunction) {
                mSetter = (NativeFunction) setter;
            }
        } else if (mSetter != null) {
            mSetter.call(Context.getCurrentContext(), start, start, new Object[]{name, value});
        } else {
            super.put(name, start, value);
        }
    }

    public Object getWithoutProxy(String name, Scriptable start) {
        return super.get(name, start);
    }

    @Override
    public Object get(String name, Scriptable start) {
        Object value = super.get(name, start);
        if (value != null && value != UniqueTag.NOT_FOUND) {
            return value;
        }
        if (mGetter != null) {
            value = mGetter.call(Context.getCurrentContext(), start, start, new Object[]{name});
        }
        return value;
    }

    @Override
    public Object getDefaultValue(Class<?> typeHint) {
        return toString();
    }
}
