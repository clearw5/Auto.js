package org.autojs.autojs.model.explorer;

public class ExplorerChangeEvent {

    public static final int REMOVE = 0;
    public static final int CREATE = 1;
    public static final int CHANGE = 2;
    public static final int ALL = 3;

    private final int mAction;
    private final ExplorerItem mItem;
    private final ExplorerItem mNewItem;
    private final ExplorerPage mItemGroup;

    public ExplorerChangeEvent(ExplorerPage itemGroup) {
        this(itemGroup, CHANGE, null, null);
    }

    public ExplorerChangeEvent(ExplorerPage parent, int action, ExplorerItem oldItem, ExplorerItem newItem) {
        mAction = action;
        mItemGroup = parent;
        mNewItem = newItem;
        mItem = oldItem;
    }

    public ExplorerChangeEvent(ExplorerPage parent, int action, ExplorerItem item) {
       this(parent, action, item, null);
    }

    public ExplorerChangeEvent(int action) {
        this(null, action, null, null);
    }

    public int getAction() {
        return mAction;
    }

    public ExplorerItem getItem() {
        return mItem;
    }

    public ExplorerItem getNewItem() {
        return mNewItem;
    }

    public ExplorerPage getItemGroup() {
        return mItemGroup;
    }
}
