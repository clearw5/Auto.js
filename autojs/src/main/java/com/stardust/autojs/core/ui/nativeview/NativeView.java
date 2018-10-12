package com.stardust.autojs.core.ui.nativeview;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;
import android.widget.CompoundButton;

import com.stardust.autojs.core.eventloop.EventEmitter;
import com.stardust.autojs.core.ui.BaseEvent;
import com.stardust.autojs.core.ui.attribute.ViewAttributes;
import com.stardust.autojs.core.ui.widget.JsListView;
import com.stardust.autojs.rhino.NativeJavaObjectWithPrototype;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;

import java.util.HashSet;

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

    public NativeView(Scriptable scope, View javaObject, Class<?> staticType, com.stardust.autojs.runtime.ScriptRuntime runtime) {
        super(scope, javaObject, staticType);
        mViewAttributes = ViewAttributes.fromView(runtime.ui.getResourceParser(), javaObject);
        mView = javaObject;
        ViewPrototype viewPrototype = new ViewPrototype(mView, scope, runtime);
        prototype = new NativeJavaObject(scope, viewPrototype, viewPrototype.getClass());
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
        ViewAttributes.Attribute attribute = mViewAttributes.get(name);
        if (attribute != null) {
            attribute.set(ScriptRuntime.toString(value));
            return;
        }
        super.put(name, start, value);
    }

    @Override
    public Object get(String name, Scriptable start) {
        ViewAttributes.Attribute attribute = mViewAttributes.get(name);
        if (attribute != null) {
            return attribute.get();
        }
        return super.get(name, start);
    }


    @Override
    public View unwrap() {
        return mView;
    }

}
