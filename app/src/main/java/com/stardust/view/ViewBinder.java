package com.stardust.view;

import android.view.View;
import android.widget.CompoundButton;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Stardust on 2017/1/30.
 * <p>
 * 哈？你说我为什么不用AA框架？之前还不知道嘛所以现在用了。
 */

public class ViewBinder {

    public interface ViewSupplier {
        View findViewById(int id);
    }


    public static void bind(final Object o) {
        final Method findViewById;
        try {
            findViewById = o.getClass().getMethod("findViewById", int.class);
            findViewById.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("You must implement findViewById to use view binding", e);
        }
        bind(o, new ViewSupplier() {
            @Override
            public View findViewById(int id) {
                try {
                    return (View) findViewById.invoke(o, id);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static void bind(Object o, final View view) {
        bind(o, new ViewSupplier() {
            @Override
            public View findViewById(int id) {
                return view.findViewById(id);
            }
        });
    }


    public static void bind(Object o, ViewSupplier viewSupplier) {
        Method[] methods = o.getClass().getDeclaredMethods();
        bindId(o, viewSupplier);
        for (Method method : methods) {
            method.setAccessible(true);
            bindClick(o, method, viewSupplier);
            bindCheck(o, method, viewSupplier);
        }
    }

    public static void bindId(Object o, final View v) {
        bindId(o, new ViewSupplier() {
            @Override
            public View findViewById(int id) {
                return v.findViewById(id);
            }
        });
    }

    private static void bindId(Object o, ViewSupplier viewSupplier) {
        for (Field field : o.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            ViewBinding.Id id = field.getAnnotation(ViewBinding.Id.class);
            if (id == null || id.value() == 0)
                continue;
            try {
                field.set(o, viewSupplier.findViewById(id.value()));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void bindCheck(final Object o, final Method method, ViewSupplier viewSupplier) {
        ViewBinding.Check annotation = method.getAnnotation(ViewBinding.Check.class);
        if (annotation == null || annotation.value() == 0)
            return;
        int id = annotation.value();
        CompoundButton button = (CompoundButton) viewSupplier.findViewById(id);
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
    }

    private static void bindClick(final Object o, final Method method, ViewSupplier viewSupplier) {
        ViewBinding.Click annotation = method.getAnnotation(ViewBinding.Click.class);
        if (annotation == null || annotation.value() == 0)
            return;
        int id = annotation.value();
        View view = viewSupplier.findViewById(id);
        if (view == null)
            return;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invokeMethod(o, method);
            }
        });
    }

    private static void invokeMethod(Object o, Method method) {
        try {
            method.invoke(o);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
