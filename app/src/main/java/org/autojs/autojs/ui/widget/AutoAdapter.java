package org.autojs.autojs.ui.widget;

import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Stardust on 2017/8/19.
 */

public class AutoAdapter<DT> extends RecyclerView.Adapter<BindableViewHolder<DT>> {

    private ViewHolderSupplier<? extends BindableViewHolder<DT>> mViewHolderSupplier;
    private final List<DT> mData = new ArrayList<>();

    public AutoAdapter(ViewHolderSupplier<? extends BindableViewHolder<DT>> viewHolderSupplier) {
        mViewHolderSupplier = viewHolderSupplier;
    }

    public AutoAdapter(ViewHolderSupplier.ViewHolderCreator<? extends BindableViewHolder<DT>> viewHolderCreator, int layoutRes) {
        mViewHolderSupplier = ViewHolderSupplier.of(viewHolderCreator, layoutRes);
    }


    @Override
    public BindableViewHolder<DT> onCreateViewHolder(ViewGroup parent, int viewType) {
        return mViewHolderSupplier.createViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(BindableViewHolder<DT> holder, int position) {
        holder.bind(mData.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void remove(DT data) {
        int pos = mData.indexOf(data);
        if (pos < 0)
            return;
        mData.remove(pos);
        notifyItemRemoved(pos);
    }

    public void remove(int index) {
        mData.remove(index);
        notifyItemRemoved(index);
    }

    public void addAll(Collection<? extends DT> c) {
        mData.addAll(c);
        notifyItemRangeInserted(mData.size() - c.size() - 1, mData.size() - 1);
    }

    public DT get(int index){
        return mData.get(index);
    }

    public void setData(Collection<? extends DT> c) {
        mData.clear();
        mData.addAll(c);
        notifyDataSetChanged();
    }

    public void add(DT item) {
        mData.add(item);
        notifyItemInserted(mData.size() - 1);
    }

    public List<DT> getData() {
        return mData;
    }

    public void removeAll() {
        mData.clear();
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged(List<DT> result) {
        mData.clear();
        mData.addAll(result);
        notifyDataSetChanged();
    }
}
