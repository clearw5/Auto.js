package org.autojs.autojs.ui.edit.debug;

import com.stardust.autojs.rhino.debug.Debugger;

import org.autojs.autojs.autojs.AutoJs;
import org.mozilla.javascript.ContextFactory;

public class DebuggerSingleton {

    private static Debugger sDebugger = new Debugger(AutoJs.getInstance().getScriptEngineService(), ContextFactory.getGlobal());

    public static Debugger get(){
        return sDebugger;
    }
}
