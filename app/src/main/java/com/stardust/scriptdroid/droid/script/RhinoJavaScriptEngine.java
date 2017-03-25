package com.stardust.scriptdroid.droid.script;

import android.net.Uri;

import com.stardust.scriptdroid.droid.Droid;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.droid.runtime.DroidRuntime;
import com.stardust.scriptdroid.droid.runtime.ScriptStopException;
import com.stardust.scriptdroid.droid.script.file.ScriptFile;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.commonjs.module.RequireBuilder;
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.provider.UrlModuleSourceProvider;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Stardust on 2017/3/1.
 */

public class RhinoJavaScriptEngine extends JavaScriptEngine {

    static {
        ContextFactory.initGlobal(new InterruptibleContextFactory());
    }

    private final Set<Thread> mThreads = new HashSet<>();
    private String[] mFunctions;

    public RhinoJavaScriptEngine(DroidRuntime runtime) {
        set("droid", runtime);
    }

    @Override
    public Object execute(String script) {
        Context context = createContext();
        Scriptable scope = createScope(context);
        init(context, scope);
        Object result = context.evaluateString(scope, script, "<script>", 1, null);
        if (!script.startsWith(Droid.UI) && !script.startsWith(Droid.STAY))
            removeAndDestroy();
        return result;
    }

    private Scriptable createScope(Context context) {
        ImporterTopLevel importerTopLevel = new ImporterTopLevel();
        importerTopLevel.initStandardObjects(context, false);
        return importerTopLevel;
    }

    private Context createContext() {
        Context context = Context.enter();
        context.setOptimizationLevel(-1);
        context.setLanguageVersion(Context.VERSION_1_7);
        context.setInstructionObserverThreshold(10000);
        return context;
    }

    private void init(Context context, Scriptable scope) {
        ScriptableObject.putProperty(scope, "context", App.getApp());
        ScriptableObject.putProperty(scope, "__engine__", "rhino");
        for (Map.Entry<String, Object> variable : mVariableMap.entrySet()) {
            ScriptableObject.putProperty(scope, variable.getKey(), variable.getValue());
        }
        initRequireBuilder(context, scope);
        context.evaluateString(scope, Init.getInitScript(), "<init>", 1, null);
        mThreads.add(Thread.currentThread());

    }

    private void initRequireBuilder(Context context, Scriptable scope) {
        List<URI> paths = Collections.singletonList(new File(ScriptFile.DEFAULT_FOLDER).toURI());
        new RequireBuilder()
                .setModuleScriptProvider(new SoftCachingModuleScriptProvider(
                        new UrlModuleSourceProvider(paths, null)))
                .setSandboxed(true)
                .createRequire(context, scope)
                .install(scope);

    }


    @Override
    public int stopAll() {
        int n;
        synchronized (mThreads) {
            for (Thread thread : mThreads) {
                thread.interrupt();
            }
            n = mThreads.size();
            mThreads.clear();
        }
        return n;
    }


    @Override
    public String[] getGlobalFunctions() {
        if (mFunctions == null)
            mFunctions = getGlobalFunctionsInner();
        return mFunctions;
    }

    private String[] getGlobalFunctionsInner() {
        Scriptable scriptable = (Scriptable) execute("this");
        Object[] ids = scriptable.getIds();
        String[] functions = new String[ids.length];
        for (int i = 0; i < ids.length; i++) {
            functions[i] = ids[i].toString();
        }
        return functions;
    }

    @Override
    public void removeAndDestroy() {
        synchronized (mThreads) {
            mThreads.remove(Thread.currentThread());
        }
    }


    public static class InterruptibleContextFactory extends ContextFactory {

        @Override
        protected void observeInstructionCount(Context cx, int instructionCount) {
            if (Thread.currentThread().isInterrupted()) {
                Context.exit();
                throw new ScriptStopException();
            }
        }

        @Override
        protected Context makeContext() {
            Context cx = super.makeContext();
            cx.setInstructionObserverThreshold(10000);
            return cx;
        }
    }
}
