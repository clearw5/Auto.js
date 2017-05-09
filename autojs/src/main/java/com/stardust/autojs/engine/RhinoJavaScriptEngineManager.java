package com.stardust.autojs.engine;


import com.stardust.autojs.BuildConfig;
import com.stardust.autojs.script.ScriptSource;
import com.stardust.autojs.script.SequenceScriptSource;
import com.stardust.autojs.script.StringScriptSource;
import com.stardust.pio.PFile;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.commonjs.module.RequireBuilder;
import org.mozilla.javascript.commonjs.module.provider.ModuleSource;
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.provider.UrlModuleSourceProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Stardust on 2017/3/1.
 */

public class RhinoJavaScriptEngineManager extends AbstractScriptEngineManager {

    private String[] mFunctions;

    private ScriptSource mCustomInitScript;
    private ScriptSource mInitScript;

    public RhinoJavaScriptEngineManager(android.content.Context context) {
        super(context);
    }

    protected RhinoJavaScriptEngine createEngineInner() {
        RhinoJavaScriptEngine engine = new RhinoJavaScriptEngine(this);
        return engine;
    }

    public void setInitScript(String script) {
        setInitScriptSource(new StringScriptSource(script));
    }

    public void setInitScriptSource(ScriptSource initScriptSource) {
        if (BuildConfig.DEBUG) {
            mCustomInitScript = initScriptSource;
        } else {
            mInitScript = new SequenceScriptSource("<init>", new StringScriptSource(readInitScript()), initScriptSource);
        }
    }

    private String readInitScript() {
        try {
            return PFile.read(getContext().getAssets().open("javascript_engine_init.js"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    ScriptSource getInitScript() {
        if (mInitScript == null || BuildConfig.DEBUG) {
            // 调试时不缓存INIT_SCRIPT否则修改javascript_engine_init.js后不会更新
            if (mCustomInitScript != null) {
                mInitScript = new SequenceScriptSource("<init>", new StringScriptSource(readInitScript()), mCustomInitScript);
            } else {
                mInitScript = new StringScriptSource(readInitScript());
            }
        }
        return mInitScript;
    }

    @Override
    public String[] getGlobalFunctions() {
        if (mFunctions == null)
            mFunctions = getGlobalFunctionsInner();
        return mFunctions;
    }

    private String[] getGlobalFunctionsInner() {
        ScriptEngine engine = createEngine();
        ScriptSource source = new StringScriptSource("this", "this");
        engine.setTag("script", source);
        engine.init();
        Scriptable scriptable = (Scriptable) engine.execute(source);
        Object[] ids = scriptable.getIds();
        String[] functions = new String[ids.length];
        for (int i = 0; i < ids.length; i++) {
            functions[i] = ids[i].toString();
        }
        engine.destroy();
        return functions;
    }



}
