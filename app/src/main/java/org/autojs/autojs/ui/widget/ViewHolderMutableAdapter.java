package org.autojs.autojs.ui.widget;

import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by Stardust on 2017/4/8.
 */

public abstract class ViewHolderMutableAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private ViewHolderSupplier<VH> mViewHolderSupplier;

    public ViewHolderMutableAdapter(ViewHolderSupplier<VH> viewHolderSupplier) {
        mViewHolderSupplier = viewHolderSupplier;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return mViewHolderSupplier.createViewHolder(parent, viewType);
    }

    public void setViewHolderSupplier(ViewHolderSupplier<VH> viewHolderSupplier) {
        mViewHolderSupplier = viewHolderSupplier;
        notifyDataSetChanged();
    }

}
