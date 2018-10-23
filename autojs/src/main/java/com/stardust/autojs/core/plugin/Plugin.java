package com.stardust.autojs.core.plugin;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.stardust.autojs.rhino.TopLevelScope;
import com.stardust.autojs.runtime.ScriptRuntime;

import java.lang.reflect.Method;

public class Plugin {

    public static class PluginLoadException extends RuntimeException {
        public PluginLoadException(Throwable cause) {
            super(cause);
        }

        public PluginLoadException(String message) {
            super(message);
        }
    }

    private static final String KEY_REGISTRY = "org.autojs.plugin.sdk.registry";

    public static Plugin load(Context context, Context packageContext, ScriptRuntime runtime, TopLevelScope scope) {
        try {
            ApplicationInfo applicationInfo = packageContext.getPackageManager().getApplicationInfo(packageContext.getPackageName(), PackageManager.GET_META_DATA);
            String registryClass = applicationInfo.metaData.getString(KEY_REGISTRY);
            if (registryClass == null) {
                throw new PluginLoadException("no registry in metadata");
            }
            Class<?> pluginClass = Class.forName(registryClass, true, packageContext.getClassLoader());
            Method loadDefault = pluginClass.getMethod("loadDefault", Context.class, Context.class, Object.class, Object.class);
            return Plugin.create(loadDefault.invoke(null, context, packageContext, runtime, scope));
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new PluginLoadException(e);
        }
    }

    private static Plugin create(Object pluginInstance) {
        if (pluginInstance == null)
            return null;
        return new Plugin(pluginInstance);
    }

    private final Object mPluginInstance;
    private Method mGetVersion;
    private Method mGetScriptDir;
    private String mMainScriptPath;

    public Plugin(Object pluginInstance) {
        mPluginInstance = pluginInstance;
        findMethods(pluginInstance.getClass());
    }

    @SuppressWarnings("unchecked")
    private void findMethods(Class pluginClass) {
        try {
            mGetVersion = pluginClass.getMethod("getVersion");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            mGetScriptDir = pluginClass.getMethod("getAssetsScriptDir");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public Object unwrap() {
        return mPluginInstance;
    }

    public String getMainScriptPath() {
        return mMainScriptPath;
    }

    public void setMainScriptPath(String mainScriptPath) {
        mMainScriptPath = mainScriptPath;
    }

    public String getAssetsScriptDir() {
        try {
            return (String) mGetScriptDir.invoke(mPluginInstance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
