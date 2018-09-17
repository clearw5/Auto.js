package org.autojs.autojs.model.explorer;

public class ExplorerChangeEvent {

    public static final int REMOVE = 0;
    public static final int CREATE = 1;
    public static final int CHANGE = 2;
    public static final int ALL = 3;
    public static final int CHILDREN_CHANGE = 4;

    private final int mAction;
    private final ExplorerItem mItem;
    private final ExplorerItem mNewItem;
    private final ExplorerPage mPage;

    public ExplorerChangeEvent(ExplorerPage parent, int action, ExplorerItem oldItem, ExplorerItem newItem) {
        mAction = action;
        mPage = parent;
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

    public ExplorerPage getPage() {
        return mPage;
    }
}
