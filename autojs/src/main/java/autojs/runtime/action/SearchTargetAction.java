package autojs.runtime.action;

import android.view.accessibility.AccessibilityNodeInfo;

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
    public boolean perform(List<AccessibilityNodeInfo> nodes) {
        boolean performed = false;
        for (AccessibilityNodeInfo node : nodes) {
            node = searchTarget(node);
            if (node != null) {
                performAction(node);
                performed = true;
            }
        }
        return performed;
    }

    protected void performAction(AccessibilityNodeInfo node) {
        node.performAction(mAction);
    }

    public int getAction(){
        return mAction;
    }

    public AccessibilityNodeInfo searchTarget(AccessibilityNodeInfo node) {
        return node;
    }


}
