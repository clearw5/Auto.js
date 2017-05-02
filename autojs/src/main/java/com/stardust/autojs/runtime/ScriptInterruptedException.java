package com.stardust.autojs.runtime;

/**
 * Created by Stardust on 2017/4/30.
 */

public class ScriptInterruptedException extends ScriptStopException {

    public static boolean causedByInterrupted(Throwable e) {
        return e instanceof ScriptInterruptedException || e.getCause() instanceof ScriptInterruptedException;
    }

}
