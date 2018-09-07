package org.autojs.autojs.ui.edit.toolbar;

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
    private Dim mDim;
    private EditorView mEditorView;
    private Handler mHandler;

    public DebugToolbarFragment() {
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
        ScriptExecution scriptExecution = mEditorView.getScriptExecution();
        if (scriptExecution != null) {
            mDim = (Dim) scriptExecution.getEngine().getTag(Dim.TAG);
        }
        if (mDim == null) {
            mDim = new Dim();
            mDim.setBreak();
            mDim.setBreakOnExceptions(true);
            mDim.attachTo(AutoJs.getInstance().getScriptEngineService(), ContextFactory.getGlobal());
            mDim.setGuiCallback(this);
            setInterrupted(false);
            mEditorView.run();
        } else {
            mDim.setGuiCallback(this);
        }
        Log.d(LOG_TAG, "onViewCreated");
    }

    private void setInterrupted(boolean interrupted) {
        setMenuItemStatus(R.id.step_into, interrupted);
        setMenuItemStatus(R.id.step_over, interrupted);
        setMenuItemStatus(R.id.step_out, interrupted);
        setMenuItemStatus(R.id.resume_script, interrupted);
        if (!interrupted) {
            mEditorView.getEditor().setDebuggingLine(-1);
        }
    }

    public void detachDebugger() {
        mDim.detach();
        mDim.setGuiCallback(null);
    }

    @Click(R.id.step_over)
    void stepOver() {
        setInterrupted(false);
        mDim.setReturnValue(Dim.STEP_OVER);
    }

    @Click(R.id.step_into)
    void stepInto() {
        setInterrupted(false);
        mDim.setReturnValue(Dim.STEP_INTO);
    }

    @Click(R.id.step_out)
    void stepOut() {
        setInterrupted(false);
        mDim.setReturnValue(Dim.STEP_OUT);
    }

    @Click(R.id.stop_script)
    void stopScript() {
        mEditorView.forceStop();
    }

    @Click(R.id.resume_script)
    void resumeScript() {
        setInterrupted(false);
        mDim.setReturnValue(Dim.GO);
    }

    @Override
    public void updateSourceText(Dim.SourceInfo sourceInfo) {
        Log.d(LOG_TAG, "updateSourceText: url = " + sourceInfo.url());
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
            final int line = stackFrame.getLineNumber() - 1;
            mHandler.post(() -> {
                mEditorView.getEditor().setDebuggingLine(line);
                setInterrupted(true);
            });

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
    }

    @Override
    public boolean shouldAttachDebugger(RhinoJavaScriptEngine engine) {
        ScriptExecution execution = mEditorView.getScriptExecution();
        return execution != null && execution.getId() == engine.getId();

    }

    @Override
    public List<Integer> getMenuItemIds() {
        return Arrays.asList(R.id.step_over, R.id.step_into, R.id.step_out, R.id.resume_script, R.id.stop_script);
    }

}
