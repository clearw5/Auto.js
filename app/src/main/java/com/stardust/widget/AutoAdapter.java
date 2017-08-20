package com.stardust.widget;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Stardust on 2017/8/19.
 */

public class AutoAdapter<DT> extends RecyclerView.Adapter<BindableViewHolder<DT>> {

    private ViewHolderSupplier<? extends BindableViewHolder<DT>> mViewHolderSupplier;
    private List<DT> mList = new ArrayList<>();

    public AutoAdapter(ViewHolderSupplier<? extends BindableViewHolder<DT>> viewHolderSupplier) {
        mViewHolderSupplier = viewHolderSupplier;
    }


    @Override
    public BindableViewHolder<DT> onCreateViewHolder(ViewGroup parent, int viewType) {
        return mViewHolderSupplier.createViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(BindableViewHolder<DT> holder, int position) {
        holder.bind(mList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void remove(DT data) {
        int pos = mList.indexOf(data);
        if (pos < 0)
            return;
        mList.remove(pos);
        notifyItemRemoved(pos);
    }

    public void remove(int index) {
        mList.remove(index);
        notifyItemRemoved(index);
    }

    public void addAll(Collection<? extends DT> c) {
        mList.addAll(c);
        notifyItemRangeInserted(mList.size() - c.size() - 1, mList.size() - 1);
    }

    public void setData(Collection<? extends DT> c) {
        mList.clear();
        mList.addAll(c);
        notifyDataSetChanged();
    }

}
