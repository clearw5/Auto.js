package com.stardust.automator.simple_action;


import com.stardust.automator.UiObject;

/**
 * Created by Stardust on 2017/1/27.
 */

public class SearchUpTargetAction extends SearchTargetAction {

    private static final String TAG = SearchUpTargetAction.class.getSimpleName();
    private static final int LOOP_MAX = 20;
    private Able mAble;

    public SearchUpTargetAction(int action, Filter filter) {
        super(action, filter);
        mAble = Able.ABLE_MAP.get(action);
    }

    @Override
    public UiObject searchTarget(UiObject n) {
        UiObject node = n;
        int i = 0;
        while (node != null && !mAble.isAble(node)) {
            i++;
            if (i > LOOP_MAX) {
                return null;
            }
            node = node.parent();
        }
        return node;
    }

    @Override
    public String toString() {
        return "SearchUpTargetAction{" +
                "mAble=" + mAble + ", " +
                super.toString() +
                '}';
    }
}
