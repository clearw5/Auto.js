package org.autojs.autojs.ui.edit.toolbar;

import android.os.Looper;
import android.util.Log;

import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.tools.debugger.Dim;
import org.mozilla.javascript.tools.debugger.GuiCallback;

public class DebugToolbarFragment implements GuiCallback {

    private Dim mDim;

    public void attachDebugger(){
        mDim.attachTo(ContextFactory.getGlobal());
    }

    public void deattchDebugger(){
        mDim.detach();
    }

    public void breakpoint(){
        mDim.setGuiCallback(this);
    }

    @Override
    public void updateSourceText(Dim.SourceInfo sourceInfo) {
    }

    @Override
    public void enterInterrupt(Dim.StackFrame stackFrame, String s, String s1) {

    }

    @Override
    public boolean isGuiEventThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    @Override
    public void dispatchNextGuiEvent() throws InterruptedException {

    }
}
