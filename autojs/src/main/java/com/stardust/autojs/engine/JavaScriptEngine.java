package com.stardust.autojs.engine;

import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.script.JavaScriptSource;
import com.stardust.autojs.script.ScriptSource;

import java.lang.ref.WeakReference;

/**
 * Created by Stardust on 2017/8/3.
 */

public abstract class JavaScriptEngine extends ScriptEngine.AbstractScriptEngine<JavaScriptSource> {
    private WeakReference<ScriptRuntime> mRuntime;
    private Object mExecArgv;

    @Override
    public Object execute(JavaScriptSource scriptSource) {
        if ((scriptSource.getExecutionMode() & JavaScriptSource.EXECUTION_MODE_AUTO) != 0) {
            getRuntime().ensureAccessibilityServiceEnabled();
        }
        return doExecution(scriptSource);
    }

    protected abstract Object doExecution(JavaScriptSource scriptSource);

    public ScriptRuntime getRuntime() {
        return mRuntime.get();
    }

    public void setRuntime(ScriptRuntime runtime) {
        if (mRuntime != null) {
            throw new IllegalStateException("a runtime has been set");
        }
        mRuntime = new WeakReference<>(runtime);
        runtime.engines.setCurrentEngine(this);
        put("runtime", runtime);
    }

    public void emit(String eventName, Object... args) {
        mRuntime.get().timers.getMainTimer().postDelayed(() -> mRuntime.get().events.emit(eventName, args), 0);
    }

    public ScriptSource getSource() {
        return (ScriptSource) getTag(TAG_SOURCE);
    }

    public void setExecArgv(Object execArgv) {
        if (mExecArgv != null) {
            return;
        }
        mExecArgv = execArgv;
    }

    public Object getExecArgv() {
        return mExecArgv;
    }

    @Override
    public synchronized void destroy() {
        mRuntime.get().onExit();
        mExecArgv = null;
        super.destroy();
    }

    @Override
    public String toString() {
        return "ScriptEngine@" + Integer.toHexString(hashCode()) + "{" +
                "id=" + getId() + "," +
                "source='" + getTag(TAG_SOURCE) + "'," +
                "cwd='" + cwd() + "'" +
                "}";
    }
}
