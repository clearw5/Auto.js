package org.autojs.autojs.ui.edit.toolbar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.stardust.autojs.engine.RhinoJavaScriptEngine;
import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.autojs.rhino.debug.Dim;
import com.stardust.autojs.rhino.debug.DebugCallback;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.pio.PFiles;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.autojs.autojs.R;
import org.autojs.autojs.autojs.AutoJs;
import org.autojs.autojs.ui.edit.EditorView;
import org.autojs.autojs.ui.edit.debug.CodeEvaluator;
import org.autojs.autojs.ui.edit.debug.DebugBar;
import org.autojs.autojs.ui.edit.debug.WatchingVariable;
import org.autojs.autojs.ui.edit.editor.CodeEditor;
import org.mozilla.javascript.ContextFactory;

import java.util.Arrays;
import java.util.List;

@EFragment(R.layout.fragment_debug_toolbar)
public class DebugToolbarFragment extends ToolbarFragment implements DebugCallback, CodeEditor.CursorChangeCallback, CodeEvaluator {

    private static final String LOG_TAG = "DebugToolbarFragment";
    private Dim mDim;
    private EditorView mEditorView;
    private Handler mHandler;
    private boolean mSkipOtherFileBreakpoint = false;
    private String mCurrentEditorSourceUrl;
    private String mInitialEditorSourceUrl;
    private String mInitialEditorSource;
    private boolean mCursorChangeFromUser = true;
    private Dim.SourceInfo mSourceInfo;
    private final RecyclerView.AdapterDataObserver mVariableChangeObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            updateWatchingVariables(positionStart, positionStart + itemCount);
        }
    };
    private CodeEditor.BreakpointChangeListener mBreakpointChangeListener = new CodeEditor.BreakpointChangeListener() {
        @Override
        public void onBreakpointChange(int line, boolean enabled) {
            if (mSourceInfo != null) {
                mSourceInfo.breakpoint(line + 1, enabled);
            }
        }

        @Override
        public void onAllBreakpointRemoved(int count) {
            mDim.clearAllBreakpoints();
        }
    };

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
        mDim = createDim();
        setInterrupted(false);
        mSkipOtherFileBreakpoint = true;
        mCurrentEditorSourceUrl = mInitialEditorSourceUrl = mEditorView.getFile().toString();
        mInitialEditorSource = mEditorView.getEditor().getText();
        setupEditor();
        mEditorView.run(false);
        Log.d(LOG_TAG, "onViewCreated");
    }

    private void setupEditor() {
        CodeEditor editor = mEditorView.getEditor();
        editor.setRedoUndoEnabled(false);
        editor.addCursorChangeCallback(this);
        editor.setBreakpointChangeListener(mBreakpointChangeListener);
        DebugBar debugBar = mEditorView.getDebugBar();
        debugBar.registerVariableChangeObserver(mVariableChangeObserver);
        debugBar.setCodeEvaluator(this);

    }

    private Dim createDim() {
        Dim dim = new Dim();
        dim.setBreak();
        dim.setBreakOnExceptions(true);
        dim.attachTo(AutoJs.getInstance().getScriptEngineService(), ContextFactory.getGlobal());
        dim.setGuiCallback(this);
        return dim;
    }

    private void setInterrupted(boolean interrupted) {
        setMenuItemStatus(R.id.step_into, interrupted);
        setMenuItemStatus(R.id.step_over, interrupted);
        setMenuItemStatus(R.id.step_out, interrupted);
        setMenuItemStatus(R.id.resume_script, interrupted);
        if (!interrupted && mEditorView != null) {
            mEditorView.getEditor().setDebuggingLine(-1);
        }
    }

    public void detachDebugger() {
        if (!mDim.isAttached()) {
            return;
        }
        Log.d(LOG_TAG, "detachDebugger");
        mDim.detach();
        mDim.setGuiCallback(null);
        if (mEditorView == null) {
            return;
        }
        CodeEditor editor = mEditorView.getEditor();
        editor.removeCursorChangeCallback(this);
        editor.setBreakpointChangeListener(null);
        mSourceInfo = null;
        editor.setRedoUndoEnabled(true);
        if (!TextUtils.equals(mInitialEditorSourceUrl, mCurrentEditorSourceUrl)) {
            editor.setText(mInitialEditorSource);
        }
        DebugBar debugBar = mEditorView.getDebugBar();
        debugBar.setTitle(null);
        debugBar.setCodeEvaluator(null);
        debugBar.unregisterVariableChangeObserver(mVariableChangeObserver);
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
        mSourceInfo = sourceInfo;
    }

    @Override
    public void enterInterrupt(Dim.StackFrame stackFrame, String threadName, String message) {
        Log.d(LOG_TAG, "enterInterrupt: threadName = " + threadName + ", url = " + stackFrame.getUrl() + ", line = " + stackFrame.getLineNumber());
        //刚启动调试时会在init脚本的第一行自动停下，此时应该让脚本继续运行
        if (mSkipOtherFileBreakpoint && !stackFrame.getUrl().equals(mInitialEditorSourceUrl) && message == null) {
            mHandler.post(this::resumeScript);
            return;
        }
        mSkipOtherFileBreakpoint = false;
        showDebuggingLineOnEditor(stackFrame, message);
        mHandler.post(this::updateWatchingVariables);
    }

    private void updateWatchingVariables() {
        updateWatchingVariables(0, mEditorView.getDebugBar().getWatchingVariables().size());
    }

    private void updateWatchingVariables(int start, int end) {
        if (!mDim.isAttached()) {
            return;
        }
        DebugBar debugBar = mEditorView.getDebugBar();
        List<WatchingVariable> variables = debugBar.getWatchingVariables();
        for (int i = start; i < end; i++) {
            WatchingVariable variable = variables.get(i);
            String value = eval(variable.getName());
            variable.setValue(value);
        }
        debugBar.refresh(start, end - start);
    }

    public String eval(String expr) {
        if (expr == null || !mDim.isAttached() || !mDim.stringIsCompilableUnit(expr)) {
            return null;
        }
        mDim.contextSwitch(0);
        return mDim.eval(expr);
    }

    private void showDebuggingLineOnEditor(Dim.StackFrame stackFrame, String message) {
        //如果调试进入到其他脚本（例如模块脚本），则改变当前编辑器的文本为自动调试的脚本的代码
        String source;
        //标记是否需要更改编辑器文本
        boolean shouldChangeText = !stackFrame.getUrl().equals(mCurrentEditorSourceUrl);
        if (shouldChangeText) {
            source = stackFrame.sourceInfo().source();
        } else {
            source = null;
        }
        mCurrentEditorSourceUrl = stackFrame.getUrl();
        final int line = stackFrame.getLineNumber() - 1;
        mHandler.post(() -> {
            if (mEditorView == null) {
                return;
            }
            if (shouldChangeText) {
                mEditorView.getEditor().setText(source);
            }
            mCursorChangeFromUser = false;
            mEditorView.getEditor().setDebuggingLine(line);
            mEditorView.getEditor().jumpTo(line, 0);
            mEditorView.getDebugBar().setTitle(PFiles.getName(mCurrentEditorSourceUrl));
            setInterrupted(true);
            if (message != null && !message.equals(ScriptInterruptedException.class.getName())) {
                Toast.makeText(mEditorView.getContext(), message, Toast.LENGTH_LONG).show();
            }
        });
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
    public void onCursorChange(String line, int ch) {
        if (ch == 0 && !mCursorChangeFromUser) {
            mCursorChangeFromUser = true;
            return;
        }
        mCursorChangeFromUser = true;
        if (!mDim.isAttached()) {
            return;
        }
        String variable = findVariableOnCursor(line, ch);
        Log.d(LOG_TAG, "onCursorChange: variable = " + variable + ", ch = " + ch + ", line = " + line);
        String value = eval(variable);
        mEditorView.getDebugBar().updateCurrentVariable(variable, value);
    }

    private String findVariableOnCursor(String line, int ch) {
        int end;
        for (end = ch; end < line.length(); end++) {
            if (!isIdentifierChar(line.charAt(end))) {
                break;
            }
        }
        int start;
        for (start = Math.min(ch - 1, line.length() - 1); start >= 0; start--) {
            if (!isIdentifierChar(line.charAt(start))) {
                break;
            }
        }
        start++;
        if (start < end && start < line.length() && start >= 0) {
            return line.substring(start, end);
        }
        return null;
    }

    private boolean isIdentifierChar(char c) {
        return Character.isDigit(c) || Character.isLetter(c) || c == '.' || c == '_';
    }

    @Override
    public List<Integer> getMenuItemIds() {
        return Arrays.asList(R.id.step_over, R.id.step_into, R.id.step_out, R.id.resume_script, R.id.stop_script);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        detachDebugger();
    }
}
