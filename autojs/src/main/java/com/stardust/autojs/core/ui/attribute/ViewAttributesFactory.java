package com.stardust.autojs.core.ui.attribute;

import android.support.design.widget.AppBarLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.stardust.autojs.core.ui.inflater.ResourceParser;

import java.util.HashMap;

public class ViewAttributesFactory {

    interface ViewAttributesCreator {
        ViewAttributes create(ResourceParser resourceParser, View view);
    }

    private static HashMap<Class<? extends View>, ViewAttributesCreator> sViewAttributesCreators = new HashMap<>();

    static {
        sViewAttributesCreators.put(ImageView.class, ImageViewAttributes::new);
        sViewAttributesCreators.put(AppBarLayout.class, AppbarAttributes::new);
        sViewAttributesCreators.put(TextView.class, TextViewAttributes::new);
    }

    public static void put(Class<? extends View> clazz, ViewAttributesCreator creator) {
        sViewAttributesCreators.put(clazz, creator);
    }

    public static ViewAttributes create(ResourceParser resourceParser, View view) {
        Class viewClass = view.getClass();
        while (viewClass != null && !viewClass.equals(Object.class)) {
            ViewAttributesCreator creator = sViewAttributesCreators.get(viewClass);
            if (creator != null) {
                return creator.create(resourceParser, view);
            }
            viewClass = viewClass.getSuperclass();
        }
        return new ViewAttributes(resourceParser, view);
    }
}
