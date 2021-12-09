package com.stardust.autojs.core.ui.nativeview;

import android.view.View;

import com.stardust.autojs.core.ui.JsViewHelper;
import com.stardust.autojs.core.ui.ViewExtras;
import com.stardust.autojs.core.ui.attribute.ViewAttributes;
import com.stardust.autojs.rhino.NativeJavaObjectWithPrototype;

import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

import java.lang.ref.WeakReference;

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
        public final WeakReference<View> view;

        public LongClickEvent(View view) {
            this.view = new WeakReference<>(view);
        }
    }


    private final WeakReference<ViewAttributes> mViewAttributes;
    private final WeakReference<View> mView;
    private final WeakReference<ViewPrototype> mViewPrototype;

    public NativeView(Scriptable scope, View view, Class<?> staticType, com.stardust.autojs.runtime.ScriptRuntime runtime) {
        super(scope, view, staticType);
        mViewAttributes = new WeakReference<>(ViewExtras.getViewAttributes(view, runtime.ui.getResourceParser()));
        mView = new WeakReference<>(view);
        mViewPrototype = new WeakReference<>(new ViewPrototype(view, mViewAttributes.get(), scope, runtime));
        prototype = new NativeJavaObjectWithPrototype(scope, mViewPrototype.get(), mViewPrototype.get().getClass());
        prototype.setPrototype(new NativeObject());
    }

    @Override
    public boolean has(String name, Scriptable start) {
        if (mViewAttributes.get().contains(name)) {
            return true;
        }
        return super.has(name, start);
    }

    @Override
    public Object get(String name, Scriptable start) {
        if (super.has(name, start)) {
            return super.get(name, start);
        } else {
            View view = JsViewHelper.findViewByStringId(mView.get(), name);
            if (view != null) {
                return view;
            }
        }
        return Scriptable.NOT_FOUND;
    }

    public ViewPrototype getViewPrototype() {
        return mViewPrototype.get();
    }

    @Override
    public View unwrap() {
        return mView.get();
    }

}
