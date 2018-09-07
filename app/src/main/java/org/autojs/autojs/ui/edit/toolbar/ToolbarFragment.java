package org.autojs.autojs.ui.edit.toolbar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.View;

import java.util.List;

public abstract class ToolbarFragment extends Fragment implements View.OnClickListener {

    public interface OnMenuItemClickListener {
        void onToolbarMenuItemClick(int id);
    }

    private OnMenuItemClickListener mOnMenuItemClickListener;
    private List<Integer> mMenuItemIds;
    private SparseBooleanArray mMenuItemStatus = new SparseBooleanArray();

    public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
        mOnMenuItemClickListener = listener;
    }

    public abstract List<Integer> getMenuItemIds();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateMenuItemStatus();
    }

    private void updateMenuItemStatus() {
        View rootView = getView();
        if (rootView == null) {
            return;
        }
        if (mMenuItemIds == null) {
            mMenuItemIds = getMenuItemIds();
        }
        for (int id : mMenuItemIds) {
            View view = rootView.findViewById(id);
            view.setOnClickListener(this);
            view.setEnabled(mMenuItemStatus.get(id, view.isEnabled()));
        }
    }


    @Override
    public void onClick(View view) {
        if (mOnMenuItemClickListener != null) {
            mOnMenuItemClickListener.onToolbarMenuItemClick(view.getId());
        }
    }

    public void setMenuItemStatus(int id, boolean enabled) {
        if (mMenuItemIds == null) {
            mMenuItemIds = getMenuItemIds();
        }
        if (!mMenuItemIds.contains(id)) {
            return;
        }
        mMenuItemStatus.put(id, enabled);
        View rootView = getView();
        if (rootView == null) {
            return;
        }
        View view = rootView.findViewById(id);
        if (view == null) {
            return;
        }
        view.setEnabled(enabled);
    }

}
