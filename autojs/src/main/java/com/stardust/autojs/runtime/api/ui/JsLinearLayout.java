package com.stardust.autojs.runtime.api.ui;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Stardust on 2017/5/13.
 */

public class JsLinearLayout extends LinearLayout {

    private static final AttributeSetters<JsLinearLayout> LINEAR_LAYOUT_ATTRIBUTE_SETTERS = new AttributeSetters<JsLinearLayout>()
            .registerAttributeSetter("orientation", new AttributeSetter<JsLinearLayout>() {
                @Override
                public boolean putAttribute(JsLinearLayout layout, String value) {
                    switch (value) {
                        case "vertical":
                            layout.setOrientation(VERTICAL);
                            return true;
                        case "horizontal":
                            layout.setOrientation(HORIZONTAL);
                            return true;
                        default:
                            return false;
                    }

                }
            });

    public JsLinearLayout(Context context) {
        super(context);
    }

    public JsLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public JsLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public JsLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void putAttribute(String name, String value) {

    }
}
