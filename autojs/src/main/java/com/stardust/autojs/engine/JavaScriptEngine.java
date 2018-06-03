package com.stardust.autojs.engine;

import android.support.annotation.CallSuper;

import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.script.JavaScriptSource;
import com.stardust.autojs.script.ScriptSource;

/**
 * Created by Stardust on 2017/8/3.
 */

public abstract class JavaScriptEngine extends ScriptEngine.AbstractScriptEngine<JavaScriptSource> {
    private ScriptRuntime mRuntime;

    @Override
    public Object execute(JavaScriptSource scriptSource) {
        if ((scriptSource.getExecutionMode() & JavaScriptSource.EXECUTION_MODE_AUTO) != 0) {
            getRuntime().ensureAccessibilityServiceEnabled();
        }
        return doExecution(scriptSource);
    }

    protected abstract Object doExecution(JavaScriptSource scriptSource);

    public ScriptRuntime getRuntime() {
        return mRuntime;
    }

    public void setRuntime(ScriptRuntime runtime) {
        if (mRuntime != null) {
            throw new IllegalStateException("a runtime has been set");
        }
        mRuntime = runtime;
        mRuntime.engines.setCurrentEngine(this);
        put("runtime", runtime);
    }

    public void emit(String eventName, Object... args) {
        mRuntime.timers.getMainTimer().postDelayed(() -> mRuntime.events.emit(eventName, args), 0);
    }

    public ScriptSource getSource() {
        return (ScriptSource) getTag(TAG_SOURCE);
    }

    @Override
    public String toString() {
        return "ScriptEngine@" + Integer.toHexString(hashCode()) + "{" +
                "source='" + getTag(TAG_SOURCE) + "'," +
                "cwd='" + cwd() + "'" +
                "}";
    }
}
