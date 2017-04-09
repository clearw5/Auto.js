package com.stardust.widget;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by Stardust on 2017/4/8.
 */

public abstract class ViewHolderSupplier<VH extends RecyclerView.ViewHolder> {

    public abstract VH createViewHolder(ViewGroup parent, int viewType);

}
