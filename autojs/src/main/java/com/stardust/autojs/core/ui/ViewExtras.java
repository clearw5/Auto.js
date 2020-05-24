package com.stardust.autojs.core.ui;

import android.util.Log;
import android.view.View;

import com.stardust.autojs.R;
import com.stardust.autojs.core.ui.attribute.ViewAttributes;
import com.stardust.autojs.core.ui.attribute.ViewAttributesFactory;
import com.stardust.autojs.core.ui.inflater.ResourceParser;
import com.stardust.autojs.core.ui.nativeview.NativeView;
import com.stardust.autojs.runtime.ScriptRuntime;

import org.mozilla.javascript.Scriptable;

public class ViewExtras {

    private static final String LOG_TAG = "ViewExtras";
    private NativeView mNativeView;

    private ViewAttributes mViewAttributes;

    public static ViewExtras get(View view) {
        ViewExtras extras;
        Object tag = view.getTag(R.id.view_tag_view_extras);
        Log.d(LOG_TAG, "view = " + view + ", tag = " + tag);
        if (tag instanceof ViewExtras) {
            extras = (ViewExtras) tag;
        } else {
            extras = new ViewExtras();
            view.setTag(R.id.view_tag_view_extras, extras);
        }
        return extras;
    }


    public static ViewAttributes getViewAttributes(View view, ResourceParser parser) {
        ViewExtras extras = get(view);
        ViewAttributes attributes = extras.getViewAttributes();
        if (attributes == null) {
            attributes = ViewAttributesFactory.create(parser, view);
            extras.setViewAttributes(attributes);
        }
        return attributes;
    }


    public static NativeView getNativeView(Scriptable scope, View view, Class<?> staticType, ScriptRuntime runtime) {
        ViewExtras extras = get(view);
        NativeView nativeView = extras.getNativeView();
        if (nativeView == null) {
            nativeView = new NativeView(scope, view, staticType, runtime);
            extras.setNativeView(nativeView);
        }
        return nativeView;
    }

    public static NativeView getNativeView(View view) {
        ViewExtras extras = get(view);
        return extras.getNativeView();
    }

    public final NativeView getNativeView() {
        return mNativeView;
    }

    public final ViewAttributes getViewAttributes() {
        return mViewAttributes;
    }

    public final void setNativeView(NativeView nativeView) {
        mNativeView = nativeView;
    }

    public final void setViewAttributes(ViewAttributes viewAttributes) {
        mViewAttributes = viewAttributes;
    }
}
