package com.stardust.scriptdroid.droid.script;

import android.util.Log;

import com.squareup.duktape.Duktape;
import com.stardust.scriptdroid.droid.runtime.api.IDroidRuntime;

import static com.stardust.scriptdroid.droid.script.JavaScriptEngine.Init.readInitScript;


/**
 * Created by Stardust on 2017/1/27.
 */

public class DuktapeJavaScriptEngine implements JavaScriptEngine {

    private static final String TAG = "DuktapeJavaScriptEngine";

    private Duktape mDuktape = Duktape.create();
    private boolean mRecycled = false;

    public DuktapeJavaScriptEngine(IDroidRuntime runtime) {
        setRuntime(runtime);
        execute(readInitScript());
    }

    public void setRuntime(IDroidRuntime runtime) {
        set("droid", IDroidRuntime.class, runtime);
    }


    @Override
    public void execute(String script) {
        mDuktape.evaluate(script);
    }

    public <T> void set(String varName, Class<T> c, T value) {
        mDuktape.set(varName, c, value);
    }

    public void recycle() {
        if (!mRecycled) {
            mDuktape.close();
            mRecycled = true;
        }
    }

    @Override
    public void finalize() throws Throwable {
        try {
            if (!mRecycled) {
                Log.w(TAG, "Not recycled before finalize");
                recycle();
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        super.finalize();
    }

}
