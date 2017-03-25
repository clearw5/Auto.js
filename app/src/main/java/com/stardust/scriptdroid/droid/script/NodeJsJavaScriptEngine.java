package com.stardust.scriptdroid.droid.script;

import com.iwebpp.node.Dns;
import com.iwebpp.node.NodeContext;
import com.iwebpp.node.Url;
import com.iwebpp.node.http.http;
import com.iwebpp.node.js.rhino.Host;
import com.iwebpp.node.stream.Duplex;
import com.iwebpp.node.stream.PassThrough;
import com.iwebpp.node.stream.Readable2;
import com.iwebpp.node.stream.Transform;
import com.iwebpp.node.stream.Writable2;
import com.iwebpp.nodeandroid.MainActivity;
import com.iwebpp.nodeandroid.Toaster;
import com.iwebpp.wspp.WebSocketServer;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.droid.Droid;
import com.stardust.scriptdroid.droid.runtime.DroidRuntime;
import com.stardust.scriptdroid.tool.FileUtils;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.io.IOException;

/**
 * Created by Stardust on 2017/3/25.
 */

public class NodeJsJavaScriptEngine extends RhinoJavaScriptEngine {

    public NodeJsJavaScriptEngine(DroidRuntime runtime) {
        super(runtime);
    }


    @Override
    public Object execute(String script) {
        NodeContext nodectx = new NodeContext();
        Context context = createContext();
        Scriptable scope = createScope(context);
        ScriptableObject.putProperty(scope, "NodeCurrentContext", Context.javaToJS(nodectx, scope));
        init(context, scope);
        Object result = context.evaluateString(scope, script, "<script>", 1, null);
        try {
            nodectx.execute();
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
        if (!script.startsWith(Droid.UI) && !script.startsWith(Droid.STAY))
            removeAndDestroy();
        return result;
    }

    @Override
    protected void init(Context context, Scriptable scope) {
        super.init(context, scope);
        try {
            context.evaluateString(scope, FileUtils.readString(App.getApp().getAssets().open("nodejs_engine_init.js")), "<nodejs_init>", 1, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void putProperties(Context context, Scriptable scope) {
        super.putProperties(context, scope);
        ScriptableObject.putProperty(scope, "NodeHostEnv", Context.javaToJS(this, scope));
    }
}
