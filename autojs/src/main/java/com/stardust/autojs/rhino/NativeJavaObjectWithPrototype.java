package com.stardust.autojs.rhino;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

/**
 * Created by Stardust on 2018/4/4.
 */

public class NativeJavaObjectWithPrototype extends NativeJavaObject {


    public NativeJavaObjectWithPrototype(Scriptable scope, Object javaObject, Class<?> staticType) {
        super(scope, javaObject, staticType);
    }

    @Override
    public boolean has(String name, Scriptable start) {
        return super.has(name, start) || (prototype != null && prototype.has(name, start))
                || name.equals("prototype");
    }

    @Override
    public Object get(String name, Scriptable start) {
        if (name.equals("prototype")) {
            return prototype;
        }
        Object o = super.get(name, start);
        if (o != Scriptable.NOT_FOUND) {
            return o;
        }
        if (prototype == null) {
            return o;
        }
        return prototype.get(name, start);
    }

    @Override
    public void put(String name, Scriptable start, Object value) {
        if (name.equals("prototype")) {
            prototype = (Scriptable) value;
            return;
        }
        super.put(name, start, value);
    }
}
