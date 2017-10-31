package com.stardust.autojs.core.bridge;

/**
 * Created by Stardust on 2017/10/31.
 */

public abstract class ScriptBridges {


    public abstract Object toArray(Object[] nativeArray);

    public abstract Object toString(String nativeString);

    public abstract Object callFunction(Object function, Object target, Object[] args);

    public abstract Object wrapAsArray(Iterable<?> iterable);

    public abstract AccessibilityBridge getAccessibilityBrige();

}
