package com.stardust.autojs.engine;


import android.content.Context;

import com.stardust.autojs.runtime.ScriptRuntime;

/**
 * Created by Stardust on 2017/3/25.
 */

public class NodeJsJavaScriptEngineManager extends RhinoJavaScriptEngineManager {

    public NodeJsJavaScriptEngineManager(Context context) {
        super(context);
    }

    @Override
    protected NodeJsJavaScriptEngine createEngineInner() {
        NodeJsJavaScriptEngine engine = new NodeJsJavaScriptEngine(this);
        return engine;
    }

}
