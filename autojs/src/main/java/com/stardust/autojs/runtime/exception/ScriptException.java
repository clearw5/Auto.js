package com.stardust.autojs.runtime.exception;

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
