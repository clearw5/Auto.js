package com.stardust.autojs.rhino;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

public class BindingNativeJavaObject extends NativeJavaObject {

    public BindingNativeJavaObject() {
    }

    public BindingNativeJavaObject(Scriptable scope, Object javaObject, Class<?> staticType) {
        super(scope, javaObject, staticType);
    }

    public BindingNativeJavaObject(Scriptable scope, Object javaObject, Class<?> staticType, boolean isAdapter) {
        super(scope, javaObject, staticType, isAdapter);
    }

    @Override
    protected void initMembers() {
        Class dynamicType;
        if (this.javaObject != null) {
            dynamicType = this.javaObject.getClass();
        } else {
            dynamicType = this.staticType;
        }


    }
}
