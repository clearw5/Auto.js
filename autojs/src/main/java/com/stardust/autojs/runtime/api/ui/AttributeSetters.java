package com.stardust.autojs.runtime.api.ui;

import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stardust on 2017/5/13.
 */

public class AttributeSetters<V extends View> {

    private Map<String, AttributeSetter<V>> mAttributeSetters = new HashMap<>();


    public boolean putAttribute(V view, String name, String value) {
        AttributeSetter<V> setter = mAttributeSetters.get(name);
        return setter != null && setter.putAttribute(view, value);
    }

    public AttributeSetters<V> registerAttributeSetter(String name, AttributeSetter<V> setter) {
        mAttributeSetters.put(name, setter);
        return this;
    }

    public void removeAttributeSetter(String name) {
        mAttributeSetters.remove(name);
    }
}
