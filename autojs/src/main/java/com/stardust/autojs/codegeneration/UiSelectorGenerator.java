package com.stardust.autojs.codegeneration;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;

import com.stardust.automator.UiGlobalSelector;
import com.stardust.util.Consumer;
import com.stardust.view.accessibility.NodeInfo;

/**
 * Created by Stardust on 2017/8/4.
 */

public class UiSelectorGenerator {


    public static final int UNTIL_FIND = 0;
    public static final int FIND_ONE = 1;
    public static final int WAIT_FOR = 2;
    public static final int EXISTS = 3;

    private ReadOnlyUiObject mRoot;
    private ReadOnlyUiObject mTarget;
    private boolean mUsingId = true;
    private boolean mUsingDesc = true;
    private boolean mUsingText = true;
    private UiGlobalSelector mUiGlobalSelector;
    private int mSearchMode = FIND_ONE;
    private int mAction = -1;

    public UiSelectorGenerator(NodeInfo root, NodeInfo target) {
        this(new ReadOnlyUiObject(root), new ReadOnlyUiObject(target));
    }

    public UiSelectorGenerator(ReadOnlyUiObject root, ReadOnlyUiObject target) {
        mRoot = root;
        mTarget = target;
    }

    public void setSearchMode(int searchMode) {
        mSearchMode = searchMode;
    }

    public void setAction(int action) {
        mAction = action;
    }

    public void setUsingId(boolean usingId) {
        mUsingId = usingId;
    }

    public void setUsingDesc(boolean usingDesc) {
        mUsingDesc = usingDesc;
    }

    public void setUsingText(boolean usingText) {
        mUsingText = usingText;
    }


    private boolean tryWithStringCondition(String name, String value, Consumer<String> condition, StringBuilder code) {
        if (value == null || value.isEmpty())
            return false;
        code.append('.').append(name).append("(\"").append(value).append("\")");
        condition.accept(value);
        return isConditionEnough();
    }

    private boolean tryWithIntCondition(String name, int value, Consumer<Integer> condition, StringBuilder code) {
        code.append('.').append(name).append('(').append(value).append(')');
        condition.accept(value);
        return isConditionEnough();
    }

    private boolean isConditionEnough() {
        if (mSearchMode == UNTIL_FIND) {
            return !mUiGlobalSelector.findAndReturnList(mRoot).isEmpty();
        } else {
            return mUiGlobalSelector.findAndReturnList(mRoot).size() == 1;

        }
    }

    public String generate() {
        String selector = generateSelector();
        if (selector == null) {
            return null;
        }
        //remove '.'
        selector = selector.substring(1);
        if (mSearchMode == WAIT_FOR) {
            return selector + ".waitFor()";
        }
        if (mSearchMode == EXISTS) {
            return "if(" + selector + ".exists()){\n  \n}";
        }
        String action = getAction();
        if (action == null) {
            return selector;
        } else {
            return selector + action;
        }

    }

    private String getAction() {
        switch (mAction) {
            case AccessibilityNodeInfoCompat.ACTION_CLICK:
                return ".click()";
            case AccessibilityNodeInfoCompat.ACTION_LONG_CLICK:
                return ".longClick()";

            case AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD:
                return ".scrollBackward()";

            case AccessibilityNodeInfoCompat.ACTION_SCROLL_FORWARD:
                return ".scrollForward()";

            case AccessibilityNodeInfoCompat.ACTION_SET_TEXT:
                return ".setText(\"\")";
        }
        return null;
    }

    private String generateSelector() {
        mUiGlobalSelector = new UiGlobalSelector();
        StringBuilder code = new StringBuilder();
        if (mUsingId &&
                tryWithStringCondition("id", mTarget.id(), mUiGlobalSelector::id, code)) {
            return code.toString();
        }
        if (tryWithIntCondition("depth", mTarget.depth(), mUiGlobalSelector::depth, code)) {
            return code.toString();
        }
        if (tryWithStringCondition("className", mTarget.className(), mUiGlobalSelector::className, code)) {
            return code.toString();
        }

        if (mUsingText &&
                tryWithStringCondition("text", mTarget.text(), mUiGlobalSelector::text, code)) {
            return code.toString();
        }

        if (mUsingDesc &&
                tryWithStringCondition("desc", mTarget.desc(), mUiGlobalSelector::desc, code)) {
            return code.toString();
        }
        return null;
    }
}
