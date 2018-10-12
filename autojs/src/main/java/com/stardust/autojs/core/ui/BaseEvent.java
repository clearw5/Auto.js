package com.stardust.autojs.core.ui;

import com.stardust.autojs.rhino.NativeJavaObjectWithPrototype;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;

public class BaseEvent extends NativeJavaObjectWithPrototype {

    private boolean mConsumed = false;

    public BaseEvent(Scriptable scope, Object javaObject, Class<?> staticType) {
        super(scope, javaObject, staticType);
    }

    public BaseEvent(Scriptable scope, Object javaObject, Class<?> staticType, boolean isAdapter) {
        super(scope, javaObject, staticType, isAdapter);
    }



    public BaseEvent(Scriptable scope, Object javaObject) {
        super(scope, javaObject, javaObject.getClass());
    }


    @Override
    public boolean has(String name, Scriptable start) {
        return super.has(name, start) || "consumed".equals(name);
    }

    @Override
    public Object get(String name, Scriptable start) {
        if ("consumed".equals(name)) {
            return mConsumed;
        }
        return super.get(name, start);
    }

    @Override
    public void put(String name, Scriptable start, Object value) {
        if ("consumed".equals(name)) {
            mConsumed = ScriptRuntime.toBoolean(value);
            return;
        }
        super.put(name, start, value);
    }

    public boolean isConsumed() {
        return mConsumed;
    }
}
