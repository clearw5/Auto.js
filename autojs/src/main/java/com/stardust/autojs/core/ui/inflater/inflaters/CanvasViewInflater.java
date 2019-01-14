package com.stardust.autojs.core.ui.inflater.inflaters;

import androidx.annotation.Nullable;

import com.stardust.autojs.core.graphics.ScriptCanvasView;
import com.stardust.autojs.core.ui.inflater.ResourceParser;
import com.stardust.autojs.core.ui.inflater.ViewCreator;
import com.stardust.autojs.runtime.ScriptRuntime;

/**
 * Created by Stardust on 2018/3/16.
 */

public class CanvasViewInflater extends BaseViewInflater<ScriptCanvasView> {

    private ScriptRuntime mScriptRuntime;

    public CanvasViewInflater(ResourceParser resourceParser, ScriptRuntime runtime) {
        super(resourceParser);
        mScriptRuntime = runtime;
    }

    @Nullable
    @Override
    public ViewCreator<ScriptCanvasView> getCreator() {
        return (context, attrs) -> new ScriptCanvasView(context, mScriptRuntime);
    }
}
