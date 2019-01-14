package org.autojs.autojs.ui.edit.toolbar;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.autojs.rhino.debug.DebugCallback;
import com.stardust.autojs.rhino.debug.Debugger;
import com.stardust.autojs.rhino.debug.Dim;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.pio.PFiles;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.autojs.autojs.R;
import org.autojs.autojs.ui.edit.EditorView;
import org.autojs.autojs.ui.edit.debug.CodeEvaluator;
import org.autojs.autojs.ui.edit.debug.DebugBar;
import org.autojs.autojs.ui.edit.debug.DebuggerSingleton;
import org.autojs.autojs.ui.edit.debug.WatchingVariable;
import org.autojs.autojs.ui.edit.editor.CodeEditor;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

@EFragment(R.layout.fragment_debug_toolbar)
public class DebugToolbarFragment extends ToolbarFragment implements DebugCallback, CodeEditor.CursorChangeCallback, CodeEvaluator {

    private static final String LOG_TAG = "DebugToolbarFragment";
    private EditorView mEditorView;
    private boolean mCursorChangeFromUser = true;
    private Debugger mDebugger;
    private Handler mHandler;
    private String mCurrentEditorSourceUrl;
    private String mInitialEditorSourceUrl;
    private String mInitialEditorSource;

    private final RecyclerView.AdapterDataObserver mVariableChangeObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            updateWatchingVariables(positionStart, positionStart + itemCount);
        }
    };
    private CodeEditor.BreakpointChangeListener mBreakpointChangeListener = new CodeEditor.BreakpointChangeListener() {
        @Override
        public void onBreakpointChange(int line, boolean enabled) {
            if (mDebugger != null) {
                mDebugger.breakpoint(line + 1, enabled);
            }
        }

        @Override
        public void onAllBreakpointRemoved(int count) {
            mDebugger.clearAllBreakpoints();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEditorView = findEditorView(view);
        mDebugger = DebuggerSingleton.get();
        mDebugger.setWeakDebugCallback(new WeakReference<>(this));
        setInterrupted(false);
        mCurrentEditorSourceUrl = mInitialEditorSourceUrl = mEditorView.getUri().toString();
        mInitialEditorSource = mEditorView.getEditor().getText();
        setupEditor();
        ScriptExecution execution = mEditorView.run(false);
        if (execution != null) {
            mDebugger.attach(execution);
        } else {
            mEditorView.exitDebugging();
        }
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
        if (!mDebugger.isAttached()) {
            return;
        }
        Log.d(LOG_TAG, "detachDebugger");
        mDebugger.detach();
        if (mEditorView == null) {
            return;
        }
        CodeEditor editor = mEditorView.getEditor();
        if (!TextUtils.equals(mInitialEditorSourceUrl, mCurrentEditorSourceUrl)) {
            editor.setText(mInitialEditorSource);
        }
        editor.setRedoUndoEnabled(true);
        DebugBar debugBar = mEditorView.getDebugBar();
        debugBar.setTitle(null);
        debugBar.setCodeEvaluator(null);
    }

    @Click(R.id.step_over)
    void stepOver() {
        setInterrupted(false);
        mDebugger.stepOver();
    }

    @Click(R.id.step_into)
    void stepInto() {
        setInterrupted(false);
        mDebugger.stepInto();
    }

    @Click(R.id.step_out)
    void stepOut() {
        setInterrupted(false);
        mDebugger.stepOut();
    }

    @Click(R.id.stop_script)
    void stopScript() {
        mEditorView.forceStop();
    }

    @Click(R.id.resume_script)
    void resumeScript() {
        setInterrupted(false);
        mDebugger.resume();
    }

    @Override
    public void updateSourceText(Dim.SourceInfo sourceInfo) {
        Log.d(LOG_TAG, "updateSourceText: url = " + sourceInfo.url());
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
    public void enterInterrupt(Dim.StackFrame stackFrame, String threadName, String message) {
        showDebuggingLineOnEditor(stackFrame, message);
        mHandler.post(this::updateWatchingVariables);
    }

    private void updateWatchingVariables() {
        updateWatchingVariables(0, mEditorView.getDebugBar().getWatchingVariables().size());
    }

    private void updateWatchingVariables(int start, int end) {
        if (!mDebugger.isAttached()) {
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
        return mDebugger.eval(expr);
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
    public void onCursorChange(String line, int ch) {
        if (ch == 0 && !mCursorChangeFromUser) {
            mCursorChangeFromUser = true;
            return;
        }
        mCursorChangeFromUser = true;
        if (!mDebugger.isAttached()) {
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
        if (mEditorView == null) {
            return;
        }
        CodeEditor editor = mEditorView.getEditor();
        editor.removeCursorChangeCallback(this);
        editor.setBreakpointChangeListener(null);
        DebugBar debugBar = mEditorView.getDebugBar();
        debugBar.unregisterVariableChangeObserver(mVariableChangeObserver);
    }
}
