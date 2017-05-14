package com.stardust.autojs.runtime.api.ui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stardust on 2017/5/14.
 */

public class MethodMapper<O> {

    public interface IMethod<O> {

        Object call(O receiver, Object... params);
    }

    private Class<O> mType;

    public MethodMapper(Class<O> type) {
        mType = type;
    }

    private Map<String, IMethod<O>> mMethodMap = new HashMap<>();

    public void put(String methodName, String actualMethodName) {
        final Method method = getMethod(methodName);
        mMethodMap.put(methodName, new IMethod<O>() {
            @Override
            public Object call(O obj, Object... params) {
                try {
                    return method.invoke(obj, params);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private Method getMethod(String methodName) {
        for (Method method : mType.getMethods()) {
            if (method.getName().equals(methodName))
                return method;
        }
        throw new RuntimeException(new NoSuchMethodException(methodName));
    }

    public void put(String methodName, IMethod<O> iMethod) {
        mMethodMap.put(methodName, iMethod);
    }


    public Object invoke(String methodName, O obj, Object... params) {
        IMethod<O> iMethod = mMethodMap.get(methodName);
        if (iMethod == null)
            throw new RuntimeException(new NoSuchMethodException(methodName));
        return iMethod.call(obj, params);
    }
}
