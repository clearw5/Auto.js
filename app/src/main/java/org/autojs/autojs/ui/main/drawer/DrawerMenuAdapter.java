package org.autojs.autojs.ui.main.drawer;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.autojs.autojs.R;
import org.autojs.autojs.ui.widget.BindableViewHolder;

import java.util.List;

/**
 * Created by Stardust on 2017/12/10.
 */

public class DrawerMenuAdapter extends RecyclerView.Adapter<BindableViewHolder<DrawerMenuItem>> {


    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_GROUP = 1;


    private List<DrawerMenuItem> mDrawerMenuItems;

    public DrawerMenuAdapter(List<DrawerMenuItem> drawerMenuItems) {
        mDrawerMenuItems = drawerMenuItems;
    }

    public List<DrawerMenuItem> getDrawerMenuItems() {
        return mDrawerMenuItems;
    }

    @Override
    public BindableViewHolder<DrawerMenuItem> onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_GROUP) {
            return new DrawerMenuGroupViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.drawer_menu_group, parent, false));
        } else {
            return new DrawerMenuItemViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.drawer_menu_item, parent, false));

        }
    }

    @Override
    public void onBindViewHolder(BindableViewHolder<DrawerMenuItem> holder, int position) {
        holder.bind(mDrawerMenuItems.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mDrawerMenuItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mDrawerMenuItems.get(position) instanceof DrawerMenuGroup ?
                VIEW_TYPE_GROUP : VIEW_TYPE_ITEM;
    }

    public void notifyItemChanged(DrawerMenuItem item) {
        int pos = mDrawerMenuItems.indexOf(item);
        notifyItemChanged(pos);
    }
}
