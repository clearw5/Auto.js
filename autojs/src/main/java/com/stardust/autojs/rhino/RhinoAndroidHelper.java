package com.stardust.autojs.rhino;

import androidx.annotation.VisibleForTesting;


import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.SecurityController;

import java.io.File;
import java.io.IOException;

/**
 * Created by Stardust on 2017/4/5.
 */

public class RhinoAndroidHelper {

    private final File cacheDirectory;

    /**
     * Constructs a new helper using the default temporary directory.
     * Note: It is recommended to use a custom directory, so no permission problems occur.
     */
    public RhinoAndroidHelper() {
        this(new File(System.getProperty("java.io.tmpdir", "."), "classes"));
    }

    /**
     * Constructs a new helper using a directory in the applications cache.
     *
     * @param context any context
     */
    public RhinoAndroidHelper(android.content.Context context) {
        this(new File(context.getCacheDir(), "classes"));
    }

    /**
     * Constructs a helper using the specified directory as cache.
     *
     * @param cacheDirectory the cache directory to use
     */
    public RhinoAndroidHelper(File cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
    }

    /**
     * call this instead of {@link Context#enter()}
     *
     * @return a context prepared for android
     */
    public Context enterContext() {
        if (!SecurityController.hasGlobal())
            SecurityController.initGlobal(new NoSecurityController());
        return getContextFactory().enterContext();
    }

    /**
     * @return The Context factory which has to be used on android.
     */
    @VisibleForTesting
    public AndroidContextFactory getContextFactory() {
        AndroidContextFactory factory;
        if (!ContextFactory.hasExplicitGlobal()) {
            factory = new AndroidContextFactory(cacheDirectory);
            ContextFactory.getGlobalSetter().setContextFactoryGlobal(factory);
        } else if (!(ContextFactory.getGlobal() instanceof AndroidContextFactory)) {
            throw new IllegalStateException("Cannot initialize factory for Android Rhino: There is already another factory");
        } else {
            factory = (AndroidContextFactory) ContextFactory.getGlobal();
        }
        return factory;
    }

    /**
     * Compiles all classes in a jar file. They will be available in Rhino.
     *
     * @param jar the jar to load
     * @throws IOException if the jar cannot be read or is invalid or there is a problem with the cache
     */
    public void loadClassJar(File jar) throws IOException {
        ((AndroidClassLoader) getContextFactory().getApplicationClassLoader()).loadJar(jar);
    }

    /**
     * @return a context prepared for android
     * @deprecated use {@link #enterContext()} instead
     */
    @Deprecated
    public static Context prepareContext() {
        return new RhinoAndroidHelper().enterContext();
    }
}
