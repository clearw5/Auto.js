package com.stardust.autojs.core.ui.nativeview;

import android.view.View;

import com.stardust.autojs.R;
import com.stardust.autojs.core.ui.JsViewHelper;
import com.stardust.autojs.core.ui.ViewExtras;
import com.stardust.autojs.core.ui.attribute.ViewAttributes;
import com.stardust.autojs.rhino.NativeJavaObjectWithPrototype;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;

public class NativeView extends NativeJavaObjectWithPrototype {

    private static final String LOG_TAG = "NativeView";

    public static class ScrollEvent {
        public int scrollX;
        public int scrollY;
        public int oldScrollX;
        public int oldScrollY;

        public ScrollEvent(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            this.scrollX = scrollX;
            this.scrollY = scrollY;
            this.oldScrollX = oldScrollX;
            this.oldScrollY = oldScrollY;
        }
    }

    public static class LongClickEvent {
        public final View view;

        public LongClickEvent(View view) {
            this.view = view;
        }
    }


    private final ViewAttributes mViewAttributes;
    private final View mView;
    private final ViewPrototype mViewPrototype;

    public NativeView(Scriptable scope, View view, Class<?> staticType, com.stardust.autojs.runtime.ScriptRuntime runtime) {
        super(scope, view, staticType);
        mViewAttributes = ViewExtras.getViewAttributes(view, runtime.ui.getResourceParser());
        mView = view;
        mViewPrototype = new ViewPrototype(mView, scope, runtime);
        prototype = new NativeJavaObject(scope, mViewPrototype, mViewPrototype.getClass());
    }

    @Override
    public boolean has(String name, Scriptable start) {
        if (mViewAttributes.contains(name)) {
            return true;
        }
        return super.has(name, start);
    }

    @Override
    public void put(String name, Scriptable start, Object value) {
        if (value != null && (value instanceof CharSequence ||
                value.getClass().getName().equals("org.mozilla.javascript.NativeString"))) {
            ViewAttributes.Attribute attribute = mViewAttributes.get(name);
            if (attribute != null) {
                attribute.set(ScriptRuntime.toString(value));
                return;
            }
        }
        super.put(name, start, value);
    }

    @Override
    public Object get(String name, Scriptable start) {
        ViewAttributes.Attribute attribute = mViewAttributes.get(name);
        if (attribute != null) {
            return attribute.get();
        }
        if (super.has(name, start)) {
            return super.get(name, start);
        } else {
            View view = JsViewHelper.findViewByStringId(mView, name);
            if (view != null) {
                return view;
            }
        }
        return Scriptable.NOT_FOUND;
    }

    public ViewPrototype getViewPrototype() {
        return mViewPrototype;
    }

    @Override
    public View unwrap() {
        return mView;
    }

}
