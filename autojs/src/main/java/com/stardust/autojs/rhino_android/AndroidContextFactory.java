package com.stardust.autojs.rhino_android;


import com.stardust.autojs.runtime.ScriptInterruptedException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.xml.XMLLib;

import java.io.File;

/**
 * Created by Stardust on 2017/4/5.
 */

public class AndroidContextFactory extends ContextFactory {
    private final File cacheDirectory;

    /**
     * Create a new factory. It will cache generated code in the given directory
     *
     * @param cacheDirectory the cache directory
     */
    public AndroidContextFactory(File cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
        initApplicationClassLoader(createClassLoader(com.faendir.rhino_android.AndroidContextFactory.class.getClassLoader()));
    }

    /**
     * Create a ClassLoader which is able to deal with bytecode
     *
     * @param parent the parent of the create classloader
     * @return a new ClassLoader
     */
    @Override
    protected AndroidClassLoader createClassLoader(ClassLoader parent) {
        return new AndroidClassLoader(parent, cacheDirectory);
    }

    @Override
    protected void observeInstructionCount(Context cx, int instructionCount) {
        if (Thread.currentThread().isInterrupted()) {
            throw new ScriptInterruptedException();
        }
    }

    @Override
    protected Context makeContext() {
        Context cx = super.makeContext();
        cx.setInstructionObserverThreshold(10000);
        return cx;
    }

    @Override
    protected XMLLib.Factory getE4xImplementationFactory() {
        return org.mozilla.javascript.xml.XMLLib.Factory.create(
                "org.mozilla.javascript.xmlimpl.XMLLibImpl"
        );
    }
}
