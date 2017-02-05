package com.stardust.view;

import android.view.View;
import android.widget.CompoundButton;

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
            method.setAccessible(true);
            bindClick(o, method, findViewById);
            bindCheck(o, method, findViewById);
        }
    }

    private static void bindCheck(final Object o, final Method method, Method findViewById) {
        ViewBinding.Check annotation = method.getAnnotation(ViewBinding.Check.class);
        if (annotation == null || annotation.value() == 0)
            return;
        int id = annotation.value();
        try {
            CompoundButton button = (CompoundButton) findViewById.invoke(o, id);
            button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    try {
                        method.invoke(o, isChecked);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
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
            method.invoke(o);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
