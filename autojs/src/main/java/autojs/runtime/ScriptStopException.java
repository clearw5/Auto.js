package autojs.runtime;

/**
 * Created by Stardust on 2017/1/29.
 */
public class ScriptStopException extends RuntimeException {


    public ScriptStopException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScriptStopException(String message) {
        super(message);
    }

    public ScriptStopException() {

    }

    public ScriptStopException(Throwable cause) {
        super(cause);
    }
}
