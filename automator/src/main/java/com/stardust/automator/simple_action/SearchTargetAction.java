package com.stardust.automator.simple_action;

import android.graphics.BitmapFactory;

import com.stardust.automator.UiObject;

import java.util.List;

/**
 * Created by Stardust on 2017/1/27.
 */

public abstract class SearchTargetAction extends FilterAction {

    private int mAction;

    public SearchTargetAction(int action, Filter filter) {
        super(filter);
        mAction = action;
    }


    @Override
    public boolean perform(List<UiObject> nodes) {
        boolean performed = false;
        for (UiObject node : nodes) {
            node = searchTarget(node);
            if (node != null && performAction(node)) {
                performed = true;
            }
        }
        return performed;
    }

    protected boolean performAction(UiObject node) {
        return node.performAction(mAction);
    }

    public int getAction() {
        return mAction;
    }

    public UiObject searchTarget(UiObject node) {
        return node;
    }

    @Override
    public String toString() {
        return "SearchTargetAction{" +
                "mAction=" + mAction + ", " +
                super.toString() +
                "}";
    }
}
