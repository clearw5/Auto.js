package org.mozilla.javascript;

import android.os.Looper;
import android.util.Log;

import org.mozilla.javascript.jdk15.VMBridge_jdk15;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class VMBridge_custom extends VMBridge_jdk15 {

    private static final String LOG_TAG = "VMBridge_custom";

    public VMBridge_custom() throws SecurityException, InstantiationException {
    }

    @Override
    protected Object newInterfaceProxy(Object proxyHelper, ContextFactory cf, InterfaceAdapter adapter, Object target, Scriptable topScope) {
        // --- The following code is copied from super class --
        Constructor<?> c = (Constructor) proxyHelper;
        InvocationHandler handler = (proxy, method, args) -> {
            if (method.getDeclaringClass() == Object.class) {
                String methodName = method.getName();
                if (methodName.equals("equals")) {
                    Object other = args[0];
                    return proxy == other;
                }

                if (methodName.equals("hashCode")) {
                    return target.hashCode();
                }

                if (methodName.equals("toString")) {
                    return "Proxy[" + target.toString() + "]";
                }
            }
            // Add thread check
            //Check if the current thread is ui thread
            if (Looper.myLooper() == Looper.getMainLooper()) {
                // If so, catch any exception of invoking
                // Because an exception on ui thread will cause the whole app to crash
                try {
                    Object result = adapter.invoke(cf, target, topScope, proxy, method, args);
                    return castReturnValue(method, result);
                } catch (Exception e) {
                    e.printStackTrace();
                    // notify the script thread to exit
                    Object jsRuntime = topScope.get("runtime", null);
                    Log.d(LOG_TAG, "jsRuntime = " + jsRuntime);
                    if (jsRuntime instanceof NativeJavaObject) {
                        Object runtime = ((NativeJavaObject) jsRuntime).unwrap();
                        Log.d(LOG_TAG, "runtime = " + runtime);
                        if(runtime instanceof com.stardust.autojs.runtime.ScriptRuntime){
                            ((com.stardust.autojs.runtime.ScriptRuntime) runtime).exit(e);
                        }
                    }
                    // even if we caught the exception, we must return a value to for the method call.
                    return defaultValue(method.getReturnType());
                }
            } else {
                return castReturnValue(method, adapter.invoke(cf, target, topScope, proxy, method, args));
            }
        };

        // --- The following code is copied from super class --
        try {
            Object proxy = c.newInstance(handler);
            return proxy;
        } catch (InvocationTargetException var10) {
            throw Context.throwAsScriptRuntimeEx(var10);
        } catch (IllegalAccessException var11) {
            throw Kit.initCause(new IllegalStateException(), var11);
        } catch (InstantiationException var12) {
            throw Kit.initCause(new IllegalStateException(), var12);
        }
    }

    // cast the return value to boolean if needed.
    // if a javascript function that implements a java interface returns nothing,
    // it will be regarded as "false", like javascript behavior, instead of reporting error "undefined cannot be cast to boolean"
    protected Object castReturnValue(Method method, Object returnValue) {
        if (method.getReturnType().equals(Boolean.TYPE) || method.getReturnType().equals(Boolean.class)) {
            return ScriptRuntime.toBoolean(returnValue);
        }
        return returnValue;
    }

    protected Object defaultValue(Class<?> type) {
        if (type.equals(Boolean.TYPE) || type.equals(Boolean.class)) {
            return false;
        }
        if (type.equals(Integer.TYPE) || type.equals(Integer.class)) {
            return 0;
        }
        if (type.equals(Long.TYPE) || type.equals(Long.class)) {
            return 0L;
        }
        if (type.equals(Float.TYPE) || type.equals(Float.class)) {
            return 0F;
        }
        if (type.equals(Double.TYPE) || type.equals(Double.class)) {
            return 0.0;
        }
        if (type.equals(Byte.TYPE) || type.equals(Byte.class)) {
            return (byte) 0;
        }
        if (type.equals(Character.TYPE) || type.equals(Character.class)) {
            return (char) 0;
        }
        if (type.isAssignableFrom(CharSequence.class)) {
            return "";
        }
        return null;
    }
}
