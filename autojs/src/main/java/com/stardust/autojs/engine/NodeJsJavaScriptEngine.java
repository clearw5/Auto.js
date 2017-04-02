package com.stardust.autojs.engine;

import com.iwebpp.node.NodeContext;
import com.stardust.pio.PFile;
import com.stardust.pio.UncheckedIOException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.io.IOException;

/**
 * Created by Stardust on 2017/4/2.
 */

public class NodeJsJavaScriptEngine extends RhinoJavaScriptEngine {

    private static String initScript;
    private NodeContext mNodeContext = new NodeContext();

    public NodeJsJavaScriptEngine(RhinoJavaScriptEngineManager engineManager) {
        super(engineManager);
    }

    @Override
    public void init() {
        super.init();
        put("NodeCurrentContext", mNodeContext);
        getContext().evaluateString(getScriptable(), getNodeJsInitScript(), "<node_js_init>", 1, null);
    }

    private String getNodeJsInitScript() {
        if (initScript == null) {
            try {
                initScript = PFile.read(getEngineManager().getContext().getAssets().open("nodejs_engine_init.js"));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return initScript;
    }
}
