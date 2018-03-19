package com.stardust.autojs.core.ui.inflater.attrsetter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.stardust.autojs.core.eventloop.EventEmitter;
import com.stardust.autojs.core.graphics.ScriptCanvasView;
import com.stardust.autojs.core.ui.inflater.ValueParser;
import com.stardust.autojs.core.ui.inflater.ViewCreator;
import com.stardust.autojs.runtime.ScriptRuntime;

import java.util.Map;

/**
 * Created by Stardust on 2018/3/16.
 */

public class CanvasViewAttrSetter extends BaseViewAttrSetter<ScriptCanvasView> {

    private ScriptRuntime mScriptRuntime;

    public CanvasViewAttrSetter(ValueParser valueParser, ScriptRuntime runtime) {
        super(valueParser);
        mScriptRuntime = runtime;
    }

    @Nullable
    @Override
    public ViewCreator<ScriptCanvasView> getCreator() {
        return (context, attrs) -> new ScriptCanvasView(context, mScriptRuntime);
    }
}
