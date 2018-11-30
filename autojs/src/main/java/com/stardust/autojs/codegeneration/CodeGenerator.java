package com.stardust.autojs.codegeneration;

import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import com.stardust.automator.UiGlobalSelector;
import com.stardust.automator.UiObject;
import com.stardust.view.accessibility.NodeInfo;

/**
 * Created by Stardust on 2017/12/7.
 */

public class CodeGenerator {

    public static final int UNTIL_FIND = 0;
    public static final int FIND_ONE = 1;
    public static final int WAIT_FOR = 2;
    public static final int EXISTS = 3;

    private final ReadOnlyUiObject mRoot;
    private final ReadOnlyUiObject mTarget;
    private boolean mUsingId = true;
    private boolean mUsingDesc = true;
    private boolean mUsingText = true;
    private int mSearchMode = FIND_ONE;
    private int mAction = -1;

    public CodeGenerator(NodeInfo root, NodeInfo target) {
        this(new ReadOnlyUiObject(root), new ReadOnlyUiObject(target));
    }

    public CodeGenerator(ReadOnlyUiObject root, ReadOnlyUiObject target) {
        mRoot = root;
        mTarget = target;
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

    public void setSearchMode(int searchMode) {
        mSearchMode = searchMode;
    }

    public void setAction(int action) {
        mAction = action;
    }

    public String generateCode() {
        UiObject collection = getCollectionParent(mTarget);
        if (collection != null) {
            return generateCodeForCollectionChild(collection, mTarget);
        }

        UiSelectorGenerator generator = new UiSelectorGenerator(mRoot, mTarget);
        generator.setSearchMode(mSearchMode);
        generator.setUsingDesc(mUsingDesc);
        generator.setUsingId(mUsingId);
        generator.setUsingText(mUsingText);
        String selector = generateCode(generator, mRoot, mTarget, 2, 2, true);
        if (selector == null)
            return null;
        return generateAction(selector);
    }


    protected String generateCode(UiSelectorGenerator generator, UiObject root, UiObject target, int maxParentLevel, int maxChildrenLevel, boolean withFind) {
        String selector;
        if (withFind) {
            selector = generator.generateSelectorCode();
        } else {
            UiGlobalSelector s = generator.generateSelector();
            selector = s == null ? null : s.toString();
        }
        if (selector != null) {
            return selector;
        }

        if (maxChildrenLevel > 0) {
            for (int i = 0; i < target.childCount(); i++) {
                UiObject child = target.child(i);
                if (child == null)
                    continue;
                String childCode = generateCode(root, child, 0, maxChildrenLevel - 1);
                if (childCode != null) {
                    return childCode + ".parent()";
                }
            }
        }
        if (maxParentLevel > 0 && target.parent() != null) {
            int index = target.indexInParent();
            if (index > 0) {
                String parentCode = generateCode(root, target.parent(), maxParentLevel - 1, 0);
                if (parentCode != null) {
                    return parentCode + "child(" + index + ")";
                }
            }
        }
        return null;
    }

    protected String generateCode(UiObject root, UiObject target, int maxParentLevel, int maxChildrenLevel) {
        return generateCode(root, target, maxParentLevel, maxChildrenLevel, true);
    }

    protected String generateCode(UiObject root, UiObject target, int maxParentLevel, int maxChildrenLevel, boolean withFind) {
        UiSelectorGenerator generator = new UiSelectorGenerator(root, target);
        generator.setUsingId(mUsingId);
        return generateCode(generator, root, target, maxParentLevel, maxChildrenLevel, withFind);
    }

    private String generateAction(String selector) {
        if (selector == null)
            return null;
        if (mSearchMode == WAIT_FOR) {
            return selector + ".waitFor()";
        }
        if (mSearchMode == EXISTS) {
            return "if(" + selector + ".exists()){\n  \n}";
        }
        String action = getAction();
        if (action.isEmpty()) {
            return selector;
        } else {
            return selector + "." + action;
        }
    }

    private String getAction() {
        switch (mAction) {
            case AccessibilityNodeInfoCompat.ACTION_CLICK:
                return "click()";
            case AccessibilityNodeInfoCompat.ACTION_LONG_CLICK:
                return "longClick()";

            case AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD:
                return "scrollBackward()";

            case AccessibilityNodeInfoCompat.ACTION_SCROLL_FORWARD:
                return "scrollForward()";

            case AccessibilityNodeInfoCompat.ACTION_SET_TEXT:
                return "setText(\"\")";
        }
        return "";
    }

    private String generateCodeForCollectionChild(UiObject collection, UiObject target) {
        UiObject parent = target.parent();
        if (parent == null)
            return null;
        UiObject collectionItem = null;
        for (int i = 0; i < collection.childCount(); i++) {
            if (inherits(collection.child(i), target)) {
                collectionItem = collection.child(i);
                break;
            }
        }
        if (collectionItem == null)
            return null;
        String collectionCode = generateCode(mRoot, collection, 2, 0);
        if (collectionCode == null)
            return null;
        String itemCode = generateCode(collectionItem, target, 1, 2, false);
        if (itemCode == null)
            return null;
        return collectionCode + ".children().forEach(child => {\n"
                + "var target = child.findOne(" + itemCode + ");\n"
                + "target." + getAction() + ";\n"
                + "});";
    }

    private boolean inherits(UiObject root, UiObject target) {
        for (int i = 0; i < root.childCount(); i++) {
            UiObject child = root.child(i);
            if (child != null) {
                if (child.equals(target) || inherits(child, target)) {
                    return true;
                }
            }
        }
        return false;
    }

    private UiObject getCollectionParent(UiObject target) {
        UiObject parent = target.parent();
        while (parent != null) {
            if (parent.rowCount() > 0 || parent.columnCount() > 0) {
                return parent;
            }
            parent = parent.parent();
        }
        return null;
    }

}
