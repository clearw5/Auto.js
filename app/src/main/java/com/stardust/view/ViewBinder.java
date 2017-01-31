package com.stardust.view;

import android.view.View;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Stardust on 2017/1/30.
 */

public class ViewBinder {

    public static void bind(Object o) {
        Method findViewById;
        try {
            findViewById = o.getClass().getMethod("findViewById", int.class);
            findViewById.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("You must implement findViewById to use view binding", e);
        }
        Method[] methods = o.getClass().getDeclaredMethods();
        for (Method method : methods) {
            bindClick(o, method, findViewById);
        }
    }

    private static void bindClick(final Object o, final Method method, Method findViewById) {
        ViewBinding.Click annotation = method.getAnnotation(ViewBinding.Click.class);
        if (annotation == null || annotation.value() == 0)
            return;
        int id = annotation.value();
        try {
            View view = (View) findViewById.invoke(o, id);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    invokeMethod(o, method);
                }
            });
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static void invokeMethod(Object o, Method method) {
        try {
            method.setAccessible(true);
            method.invoke(o);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
