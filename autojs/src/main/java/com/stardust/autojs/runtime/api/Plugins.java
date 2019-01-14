package com.stardust.autojs.runtime.api;

import android.content.Context;

import com.stardust.autojs.core.plugin.Plugin;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.pio.PFiles;

import java.io.File;

public class Plugins {

    private final Context mContext;
    private final ScriptRuntime mRuntime;
    private File mPluginCacheDir;

    public Plugins(Context context, ScriptRuntime runtime) {
        mContext = context;
        mRuntime = runtime;
        mPluginCacheDir = new File(mContext.getCacheDir(), "plugin-scripts/");
    }

    public Plugin load(String packageName) {
        try {
            Context packageContext = mContext.createPackageContext(packageName, Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            Plugin plugin = Plugin.load(mContext, packageContext, mRuntime, mRuntime.getTopLevelScope());
            if (plugin == null) {
                return null;
            }
            File scriptCacheDir = getScriptCacheDir(packageName);
            PFiles.copyAssetDir(packageContext.getAssets(), plugin.getAssetsScriptDir(), scriptCacheDir.getPath(), null);
            plugin.setMainScriptPath(new File(scriptCacheDir, "index.js").getPath());
            return plugin;
        } catch (Exception e) {
            throw new Plugin.PluginLoadException(e);
        }
    }

    private File getScriptCacheDir(String packageName) {
        File dir = new File(mPluginCacheDir, packageName + "/");
        dir.mkdirs();
        return dir;
    }

    public void clear() {
        PFiles.deleteRecursively(mPluginCacheDir);
    }

}
