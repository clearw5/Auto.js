package com.stardust.autojs.core.accessibility;

import com.stardust.autojs.core.bridge.ScriptBridges;
import com.stardust.automator.UiObject;
import com.stardust.automator.UiObjectCollection;
import com.stardust.view.accessibility.AccessibilityNodeInfoAllocator;

/**
 * Created by Stardust on 2017/10/31.
 */

public class ScriptUiObject extends UiObject {

    private ScriptBridges mScriptBridges;

    public ScriptUiObject(Object info, ScriptBridges scriptBridges) {
        super(info);
        mScriptBridges = scriptBridges;
    }

    public ScriptUiObject(Object info, AccessibilityNodeInfoAllocator allocator, boolean isRootNode, ScriptBridges scriptBridges) {
        super(info, allocator, isRootNode);
        mScriptBridges = scriptBridges;
    }

    public ScriptUiObject(Object info, AccessibilityNodeInfoAllocator allocator, ScriptBridges scriptBridges) {
        super(info, allocator);
        mScriptBridges = scriptBridges;
    }


    @Override
    public Object children() {
        return super.children();
    }
}
