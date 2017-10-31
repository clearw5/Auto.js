package com.stardust.autojs.runtime;

/**
 * Created by Stardust on 2017/7/21.
 */

public class ScriptBridges {

    public interface FunctionCaller {

        Object[] NO_ARGUMENTS = new Object[0];

        Object call(Object func, Object target, Object[] arg);
    }

    private FunctionCaller mFunctionCaller;

    public void setFunctionCaller(FunctionCaller functionCaller) {
        mFunctionCaller = functionCaller;
    }

    public Object callFunction(Object func, Object target, Object[] args) {
        if (mFunctionCaller == null)
            throw new IllegalStateException("no function caller");
        return mFunctionCaller.call(func, target, args);
    }


}
