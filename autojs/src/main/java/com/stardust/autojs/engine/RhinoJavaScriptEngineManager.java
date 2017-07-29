package com.stardust.autojs.engine;


import com.stardust.autojs.BuildConfig;
import com.stardust.autojs.script.ScriptSource;
import com.stardust.autojs.script.SequenceScriptSource;
import com.stardust.autojs.script.StringScriptSource;
import com.stardust.pio.PFile;

import java.io.IOException;

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
        RhinoJavaScriptEngine engine = new LoopBasedJavaScriptEngine(this);
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
            return PFile.read(getAndroidContext().getAssets().open("javascript_engine_init.js"));
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


}
