package org.autojs.autojs.ui.widget;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Stardust on 2017/4/6.
 */

public class ItemTouchHelperSimpleCallback extends ItemTouchHelper.SimpleCallback {

    private boolean mLongPressDragEnabled, mItemViewSwipeEnabled;

    public ItemTouchHelperSimpleCallback(int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
        mItemViewSwipeEnabled = swipeDirs != 0;
        mLongPressDragEnabled = dragDirs != 0;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public boolean isLongPressDragEnabled() {
        return mLongPressDragEnabled;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return mItemViewSwipeEnabled;
    }
}
