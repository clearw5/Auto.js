package com.stardust.autojs.rhino.debug;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import android.util.Log;

import com.stardust.autojs.ScriptEngineService;
import com.stardust.autojs.engine.RhinoJavaScriptEngine;
import com.stardust.autojs.execution.ScriptExecution;

import org.mozilla.javascript.ContextFactory;

import java.lang.ref.WeakReference;

public class Debugger implements DebugCallbackInternal {


    private static final String LOG_TAG = "Debugger";

    private final ScriptEngineService mScriptEngineService;
    private final ContextFactory mContextFactory;

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Dim mDim = createDim();

    private DebugCallback mDebugCallback;

    private boolean mSkipOtherFileBreakpoint = false;
    @Nullable
    private String mSourceUrl;
    @Nullable
    private ScriptExecution mScriptExecution;
    @Nullable
    private volatile Dim.SourceInfo mCurrentSourceInfo;
    private WeakReference<DebugCallback> mWeakDebugCallback;


    public Debugger(ScriptEngineService scriptEngineService, ContextFactory contextFactory) {
        mScriptEngineService = scriptEngineService;
        mContextFactory = contextFactory;
    }

    public void attach(ScriptExecution execution) {
        if(isAttached()){
            detach();
        }
        mScriptExecution = execution;
        mSkipOtherFileBreakpoint = true;
        mSourceUrl = execution.getSource().toString();
        mDim.attachTo(mScriptEngineService, mContextFactory);
    }

    @Override
    public void updateSourceText(Dim.SourceInfo sourceInfo) {
        if (!sourceInfo.url().equals(mSourceUrl)) {
            return;
        }
        mCurrentSourceInfo = sourceInfo;
        if(mDebugCallback != null){
            mDebugCallback.updateSourceText(sourceInfo);
        }
        DebugCallback callback = mWeakDebugCallback == null ? null : mWeakDebugCallback.get();
        if(callback != null){
            callback.updateSourceText(sourceInfo);
        }
    }

    @Override
    public void enterInterrupt(Dim.StackFrame lastFrame, String threadTitle, String alertMessage) {
        Log.d(LOG_TAG, "enterInterrupt: threadName = " + threadTitle + ", url = " + lastFrame.getUrl() + ", line = " + lastFrame.getLineNumber());
        //刚启动调试时会在init脚本的第一行自动停下，此时应该让脚本继续运行
        if (mSkipOtherFileBreakpoint && !lastFrame.getUrl().equals(mSourceUrl) && alertMessage == null) {
            mHandler.post(this::resume);
            return;
        }
        mSkipOtherFileBreakpoint = false;
        mCurrentSourceInfo = lastFrame.sourceInfo();
        if(mDebugCallback != null){
            mDebugCallback.enterInterrupt(lastFrame, threadTitle, alertMessage);
        }
        DebugCallback callback = mWeakDebugCallback == null ? null : mWeakDebugCallback.get();
        if(callback != null){
            callback.enterInterrupt(lastFrame, threadTitle, alertMessage);
        }
    }

    @Override
    public boolean isGuiEventThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    @Override
    public void dispatchNextGuiEvent() {

    }

    @Override
    public boolean shouldAttachDebugger(RhinoJavaScriptEngine engine) {
        return mScriptExecution != null && mScriptExecution.getId() == engine.getId();

    }

    public void breakpoint(int line, boolean enabled) {
        Dim.SourceInfo sourceInfo = mCurrentSourceInfo;
        if (sourceInfo != null) {
            sourceInfo.breakpoint(line, enabled);
        }
    }

    private Dim createDim() {
        Dim dim = new Dim();
        dim.setBreak();
        dim.setBreakOnExceptions(true);
        dim.setGuiCallback(this);
        return dim;
    }

    public void resume() {
        mDim.setReturnValue(Dim.GO);
    }

    public void stepOut() {
        mDim.setReturnValue(Dim.STEP_OUT);
    }

    public void stepInto() {
        mDim.setReturnValue(Dim.STEP_INTO);
    }

    public void stepOver() {
        mDim.setReturnValue(Dim.STEP_OVER);
    }

    public boolean isAttached() {
        return mDim.isAttached();
    }

    public String eval(String expr) {
        if (expr == null || !mDim.isAttached() || !mDim.stringIsCompilableUnit(expr)) {
            return null;
        }
        mDim.contextSwitch(0);
        return mDim.eval(expr);
    }

    public void clearAllBreakpoints() {
        mDim.clearAllBreakpoints();
    }

    public void detach() {
        mDim.detach();
        mScriptExecution = null;
        mSourceUrl = null;
        mCurrentSourceInfo = null;
    }

    public void setDebugCallback(DebugCallback debugCallback) {
        mDebugCallback = debugCallback;
    }

    public void setWeakDebugCallback(WeakReference<DebugCallback> debugCallback) {
        mWeakDebugCallback = debugCallback;
    }
}
