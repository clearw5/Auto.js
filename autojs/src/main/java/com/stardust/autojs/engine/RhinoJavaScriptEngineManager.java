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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Stardust on 2017/3/1.
 */

public class RhinoJavaScriptEngineManager extends AbstractScriptEngineManager {

    private String[] mFunctions;

    private String mRequirePath = "";
    private ScriptSource mCustomInitScript;
    private ScriptSource mInitScript;

    public RhinoJavaScriptEngineManager(android.content.Context context) {
        super(context);
    }

    protected RhinoJavaScriptEngine createEngineInner() {
        RhinoJavaScriptEngine engine = new RhinoJavaScriptEngine(this);
        initRequireBuilder(engine.getContext(), engine.getScriptable());
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

    public void setRequirePath(String requirePath) {
        mRequirePath = requirePath;
    }

    void initRequireBuilder(Context context, Scriptable scope) {
        List<URI> list = Collections.singletonList(new File(mRequirePath).toURI());
        AssetAndUrlModuleSourceProvider provider = new AssetAndUrlModuleSourceProvider(getContext(), list);
        new RequireBuilder()
                .setModuleScriptProvider(new SoftCachingModuleScriptProvider(provider))
                .setSandboxed(true)
                .createRequire(context, scope)
                .install(scope);
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
        Scriptable scriptable = (Scriptable) engine.execute(source);
        Object[] ids = scriptable.getIds();
        String[] functions = new String[ids.length];
        for (int i = 0; i < ids.length; i++) {
            functions[i] = ids[i].toString();
        }
        engine.destroy();
        return functions;
    }


    private static class AssetAndUrlModuleSourceProvider extends UrlModuleSourceProvider {

        private static final String MODULES_PATH = "modules";
        private android.content.Context mContext;
        private List<String> mModules;
        private final URI mBaseURI = URI.create("file:///android_asset/modules");

        public AssetAndUrlModuleSourceProvider(android.content.Context context, List<URI> list) {
            super(list, null);
            mContext = context;
            try {
                mModules = Arrays.asList(mContext.getAssets().list(MODULES_PATH));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        @Override
        protected ModuleSource loadFromPrivilegedLocations(String moduleId, Object validator) throws IOException, URISyntaxException {
            String moduleIdWithExtension = moduleId;
            if (!moduleIdWithExtension.endsWith(".js")) {
                moduleIdWithExtension += ".js";
            }
            if (mModules.contains(moduleIdWithExtension)) {
                return new ModuleSource(new InputStreamReader(mContext.getAssets().open(MODULES_PATH + "/" + moduleIdWithExtension)), null,
                        URI.create(moduleIdWithExtension), mBaseURI, validator);
            }
            return super.loadFromPrivilegedLocations(moduleId, validator);
        }
    }

}
