package com.stardust.autojs.runtime.exception;


/**
 * Created by Stardust on 2017/4/30.
 */

public class ScriptInterruptedException extends ScriptException {

    public ScriptInterruptedException() {

    }

    public ScriptInterruptedException(Throwable e) {
        super(e);
    }

    public static boolean causedByInterrupted(Throwable e) {
        while (e != null) {
            if (e instanceof ScriptInterruptedException || e instanceof InterruptedException) {
                return true;
            }
            e = e.getCause();
        }
        return false;
    }

}
