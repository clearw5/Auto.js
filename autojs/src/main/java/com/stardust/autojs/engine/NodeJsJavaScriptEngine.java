package com.stardust.autojs.engine;

import com.iwebpp.SimpleDebug;
import com.iwebpp.node.NodeContext;
import com.iwebpp.node.http.IncomingMessage;
import com.stardust.autojs.runtime.ScriptStopException;
import com.stardust.autojs.script.ScriptSource;
import com.stardust.pio.PFile;
import com.stardust.pio.UncheckedIOException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.io.IOException;

/**
 * Created by Stardust on 2017/4/2.
 */

public class NodeJsJavaScriptEngine extends RhinoJavaScriptEngine {

    static {
        SimpleDebug.setDebugLevel(SimpleDebug.DebugLevel.DEBUG);
    }

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

    @Override
    public Object execute(ScriptSource source) {
        Object result = super.execute(source);
        try {
            mNodeContext.execute();
        } catch (Throwable throwable) {
            throw new ScriptStopException(throwable);
        }
        return result;
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
