package com.stardust.autojs.core.ui.attribute;

import android.view.View;

public interface ViewAttributeDelegate {

    interface ViewAttributeGetter {
        String get(String name);
    }

    interface ViewAttributeSetter {
        void set(String name, String value);
    }

    boolean has(String name);

    String get(View view, String name, ViewAttributeGetter defaultGetter);

    void set(View view, String name, String value, ViewAttributeSetter defaultSetter);

}
