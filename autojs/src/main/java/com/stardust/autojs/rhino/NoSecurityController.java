package com.stardust.autojs.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.GeneratedClassLoader;
import org.mozilla.javascript.SecurityController;

import java.io.Serializable;

/**
 * Created by Stardust on 2017/4/5.
 */

public class NoSecurityController extends SecurityController implements Serializable {
    @Override
    public GeneratedClassLoader createClassLoader(ClassLoader classLoader, Object o) {
        return Context.getCurrentContext().createClassLoader(classLoader);
    }

    @Override
    public Object getDynamicSecurityDomain(Object o) {
        return null;
    }
}
