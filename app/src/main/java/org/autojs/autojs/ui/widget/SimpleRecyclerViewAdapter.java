package org.autojs.autojs.ui.widget;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Stardust on 2017/5/24.
 */

public class SimpleRecyclerViewAdapter<M, VH extends BindableViewHolder<M>> extends RecyclerView.Adapter<VH> {

    public interface ViewHolderFactory<VH> {
        VH create(View itemView);
    }

    private List<M> mDataList = new ArrayList<>();
    private int mLayoutResource;
    private ViewHolderFactory<VH> mVHViewHolderFactory;

    public SimpleRecyclerViewAdapter(int layoutResource, List<M> dataList, ViewHolderFactory<VH> VHViewHolderFactory) {
        mLayoutResource = layoutResource;
        mVHViewHolderFactory = VHViewHolderFactory;
        mDataList.addAll(dataList);
    }

    public SimpleRecyclerViewAdapter(int layoutResource, ViewHolderFactory<VH> VHViewHolderFactory) {
        this(layoutResource, Collections.<M>emptyList(), VHViewHolderFactory);
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return mVHViewHolderFactory.create(LayoutInflater.from(parent.getContext()).inflate(mLayoutResource, parent, false));
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        M m = mDataList.get(position);
        holder.bind(m, position);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void add(M m) {
        mDataList.add(m);
        notifyItemInserted(mDataList.size() - 1);
    }
}
