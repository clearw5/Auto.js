package com.stardust.automator.simple_action;

import com.stardust.automator.UiObject;

/**
 * Created by Stardust on 2017/1/27.
 */

public class DepthFirstSearchTargetAction extends SearchTargetAction {


    private Able mAble;

    public DepthFirstSearchTargetAction(int action, Filter filter) {
        super(action, filter);
        mAble = Able.ABLE_MAP.get(action);
    }


    @Override
    public UiObject searchTarget(UiObject n) {
        if (n == null)
            return null;
        if (mAble.isAble(n))
            return n;
        for (int i = 0; i < n.getChildCount(); i++) {
            UiObject child = n.child(i);
            if (child == null)
                continue;
            UiObject node = searchTarget(child);
            if (node != null)
                return node;
        }
        return null;
    }


}
