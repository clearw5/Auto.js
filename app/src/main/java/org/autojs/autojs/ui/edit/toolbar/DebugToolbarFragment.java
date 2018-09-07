package org.autojs.autojs.ui.edit.toolbar;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;


import com.stardust.autojs.engine.RhinoJavaScriptEngine;
import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.autojs.rhino.debug.Dim;
import com.stardust.autojs.rhino.debug.DebugCallback;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.autojs.autojs.R;
import org.autojs.autojs.autojs.AutoJs;
import org.autojs.autojs.ui.edit.EditorView;
import org.autojs.autojs.ui.edit.editor.CodeEditor;
import org.mozilla.javascript.ContextFactory;

import java.util.Arrays;
import java.util.List;

@EFragment(R.layout.fragment_debug_toolbar)
public class DebugToolbarFragment extends ToolbarFragment implements DebugCallback {

    private static final String LOG_TAG = "DebugToolbarFragment";
    private Dim mDim = new Dim();
    private EditorView mEditorView;
    private Handler mHandler;

    public DebugToolbarFragment() {
        mDim.setGuiCallback(this);
        mDim.setBreak();
        mDim.attachTo(AutoJs.getInstance().getScriptEngineService(), ContextFactory.getGlobal());
        Log.d(LOG_TAG, "DebugToolbarFragment()");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEditorView = findEditorView(view);
        mEditorView.run();
        Log.d(LOG_TAG, "onViewCreated");
    }

    public void detachDebugger() {
        mDim.detach();
    }

    @Click(R.id.step_over)
    void stepOver() {
        mEditorView.getEditor().setDebuggingLine(-1);
        mDim.setReturnValue(Dim.STEP_OVER);
    }

    @Click(R.id.step_into)
    void stepInto() {
        mEditorView.getEditor().setDebuggingLine(-1);
        mDim.setReturnValue(Dim.STEP_INTO);
    }

    @Click(R.id.stop_out)
    void stepOut() {
        mEditorView.getEditor().setDebuggingLine(-1);
        mDim.setReturnValue(Dim.STEP_OUT);
    }

    @Click(R.id.stop_script)
    void stopScript() {
        mEditorView.forceStop();
    }

    @Click(R.id.resume_script)
    void resumeScript() {
        mEditorView.getEditor().setDebuggingLine(-1);
        mDim.setReturnValue(Dim.GO);
    }

    @Override
    public void updateSourceText(Dim.SourceInfo sourceInfo) {
        Log.d(LOG_TAG, "updateSourceText: url = " + sourceInfo.url() + ", source = " + sourceInfo.source());
        if (!sourceInfo.url().equals(mEditorView.getFile().toString())) {
            return;
        }
        sourceInfo.removeAllBreakpoints();
        for (CodeEditor.Breakpoint breakpoint : mEditorView.getEditor().getBreakpoints().values()) {
            int line = breakpoint.line + 1;
            if (sourceInfo.breakableLine(line)) {
                sourceInfo.breakpoint(line, breakpoint.enabled);
                Log.d(LOG_TAG, "not breakable: " + line);
            }
        }
    }

    @Override
    public void enterInterrupt(Dim.StackFrame stackFrame, String threadName, String s1) {
        Log.d(LOG_TAG, "enterInterrupt: threadName = " + threadName + ", url = " + stackFrame.getUrl() + ", line = " + stackFrame.getLineNumber());
        if (stackFrame.getUrl().equals(mEditorView.getFile().toString())) {
            mEditorView.getEditor().setDebuggingLine(stackFrame.getLineNumber() - 1);
        } else {
            mHandler.post(this::resumeScript);
        }
    }

    @Override
    public boolean isGuiEventThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    @Override
    public void dispatchNextGuiEvent() {
        Log.d(LOG_TAG, "dispatchNextGuiEvent");

    }

    @Override
    public boolean shouldAttachDebugger(RhinoJavaScriptEngine engine) {
        ScriptExecution execution = AutoJs.getInstance().getScriptEngineService().getScriptExecution(mEditorView.getScriptExecutionId());
        return execution != null && execution.getId() == engine.getId();

    }

    @Override
    public List<Integer> getMenuItemIds() {
        return Arrays.asList(R.id.step_over, R.id.step_into, R.id.stop_out, R.id.resume_script, R.id.stop_script);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDim.detach();
    }
}
