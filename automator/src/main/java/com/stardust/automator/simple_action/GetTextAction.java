package com.stardust.automator.simple_action;

import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.view.accessibility.AccessibilityNodeInfoAllocator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/2/14.
 */

public class GetTextAction extends SimpleAction {


    @Override
    public boolean perform(AccessibilityNodeInfo root) {
        List<String> texts = new ArrayList<>();
        getText(root, texts);
        super.setResult(texts);
        return true;
    }

    private void getText(AccessibilityNodeInfo nodeInfo, List<String> texts) {
        CharSequence text = nodeInfo.getText();
        if (text != null && text.length() != 0) {
            texts.add(text.toString());
        }
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo child = AccessibilityNodeInfoAllocator.getGlobal().getChild(nodeInfo, i);
            if (child != null) {
                getText(child, texts);
                child.recycle();
            }
        }
    }

    @Override
    public void setResult(Object result) {
        if (result instanceof List)
            super.setResult(result);
    }
}
