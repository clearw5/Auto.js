package com.stardust.autojs.runtime.exception;

import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.RhinoException;

/**
 * Created by Stardust on 2017/1/29.
 */
public class ScriptException extends RuntimeException {

    public ScriptException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScriptException(String message) {
        super(message);
    }

    public ScriptException() {
    }

    public ScriptException(Throwable cause) {
        super(cause);
    }
}
