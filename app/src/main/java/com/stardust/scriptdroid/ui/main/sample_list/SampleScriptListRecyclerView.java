package com.stardust.scriptdroid.ui.main.sample_list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.bignerdranch.expandablerecyclerview.model.Parent;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.scripts.sample.Sample;
import com.stardust.widget.LevelBeamView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/3/13.
 */

public class SampleScriptListRecyclerView extends RecyclerView {

    public interface OnItemLongClickListener {
        void onItemLongClick(Sample sample);
    }

    public interface OnItemClickListener {
        void onItemClick(Sample sample);
    }

    private OnClickListener mOnItemClickListenerProxy = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                SampleViewHolder viewHolder = (SampleViewHolder) getChildViewHolder(v);
                mOnItemClickListener.onItemClick(viewHolder.getChild());
            }
        }
    };
    private OnLongClickListener mOnLongClickListenerProxy = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (mOnItemLongClickListener != null) {
                SampleViewHolder viewHolder = (SampleViewHolder) getChildViewHolder(v);
                mOnItemLongClickListener.onItemLongClick(viewHolder.getChild());
                return true;
            }
            return false;
        }
    };
    private OnItemLongClickListener mOnItemLongClickListener;
    private OnItemClickListener mOnItemClickListener;


    private Adapter mAdapter;
    private List<SampleGroup> mSampleGroups = new ArrayList<>();

    public SampleScriptListRecyclerView(Context context) {
        super(context);
        init();
    }

    public SampleScriptListRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SampleScriptListRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setSamples(List<com.stardust.scriptdroid.scripts.sample.SampleGroup> samples) {
        mSampleGroups.clear();
        for (com.stardust.scriptdroid.scripts.sample.SampleGroup sampleGroup : samples) {
            mSampleGroups.add(new SampleGroup(sampleGroup));
        }
        mAdapter = new Adapter(mSampleGroups);
        setAdapter(mAdapter);
    }

    private void init() {
        setLayoutManager(new LinearLayoutManager(getContext()));
        addItemDecoration(new DividerItemDecoration(getContext(), VERTICAL));
    }

    private class SampleGroup implements Parent<Sample> {

        private com.stardust.scriptdroid.scripts.sample.SampleGroup mSampleGroup;

        SampleGroup(com.stardust.scriptdroid.scripts.sample.SampleGroup sampleGroup) {
            mSampleGroup = sampleGroup;
        }

        String getGroupName() {
            return mSampleGroup.name;
        }

        @Override
        public List<Sample> getChildList() {
            return mSampleGroup.sampleList;
        }

        @Override
        public boolean isInitiallyExpanded() {
            return false;
        }
    }

    private class SampleViewHolder extends ChildViewHolder<Sample> {

        TextView name;

        SampleViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            ((LevelBeamView) itemView.findViewById(R.id.level)).setLevel(2);
            itemView.setOnClickListener(mOnItemClickListenerProxy);
            itemView.setOnLongClickListener(mOnLongClickListenerProxy);
        }

        void bind(Sample sample) {
            name.setText(sample.name);
        }

    }

    private class SampleGroupViewHolder extends ParentViewHolder<SampleGroup, Sample> {

        TextView name;

        SampleGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            ((LevelBeamView) itemView.findViewById(R.id.level)).setLevel(0);
        }

        void bind(SampleGroup group) {
            name.setText(group.getGroupName());
        }

    }

    private class Adapter extends ExpandableRecyclerAdapter<SampleGroup, Sample, SampleGroupViewHolder, SampleViewHolder> {

        public Adapter(@NonNull List<SampleGroup> parentList) {
            super(parentList);
        }

        @NonNull
        @Override
        public SampleGroupViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.sample_recycler_view_group, parentViewGroup, false);
            return new SampleGroupViewHolder(view);
        }

        @NonNull
        @Override
        public SampleViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.sample_recycler_view_item, childViewGroup, false);
            return new SampleViewHolder(view);
        }

        @Override
        public void onBindParentViewHolder(@NonNull SampleGroupViewHolder parentViewHolder, int parentPosition, @NonNull SampleGroup parent) {
            parentViewHolder.bind(parent);
        }

        @Override
        public void onBindChildViewHolder(@NonNull SampleViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull Sample child) {
            childViewHolder.bind(child);
        }
    }
}
