package autojs.runtime.action;

import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by Stardust on 2017/1/27.
 */

public class SearchUpTargetAction extends SearchTargetAction {

    private Able mAble;

    public SearchUpTargetAction(int action, Filter filter) {
        super(action, filter);
        mAble = Able.ABLE_MAP.get(action);
    }

    @Override
    public AccessibilityNodeInfo searchTarget(AccessibilityNodeInfo n) {
        AccessibilityNodeInfo node = n;
        while (node != null && !mAble.isAble(node)) {
            AccessibilityNodeInfo parent = node.getParent();
            if (node != n) {
                node.recycle();
            }
            node = parent;
        }
        return node;
    }
}
