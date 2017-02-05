package com.stardust.scriptdroid.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.droid.assist.AssistClipList;
import com.stardust.scriptdroid.droid.assist.SharedPrefAssistClipList;
import com.stardust.scriptdroid.widget.ExpandableRecyclerView;

/**
 * Created by Stardust on 2017/2/4.
 */

public class AssistClipListRecyclerView extends ExpandableRecyclerView {

    public interface OnClipClickListener {
        void onClick(String clip, int position);
    }

    private AssistClipList mAssistClipList = SharedPrefAssistClipList.getInstance();
    private OnClipClickListener mOnClipClickListener;

    public AssistClipListRecyclerView(Context context) {
        super(context);
        init();
    }

    public AssistClipListRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AssistClipListRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setLayoutManager(new LinearLayoutManager(getContext()));
        setAdapter(new Adapter());
        setOnChildClickListener(new OnChildClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (mOnClipClickListener != null) {
                    String clip = (String) ((ChildViewHolder) getChildViewHolder(view)).mTextView.getText();
                    mOnClipClickListener.onClick(clip, position);
                }
            }
        });
        mAssistClipList.setOnClipChangedListener(new AssistClipList.OnClipChangedListener() {
            @Override
            public void onClipRemove(int position) {
                getAdapter().notifyItemRemoved(position);
            }

            @Override
            public void onClipInsert(int position) {
                getAdapter().notifyItemInserted(position);
            }

            @Override
            public void onChange() {
                getAdapter().notifyDataSetChanged();
            }
        });
    }

    public void setOnClipClickListener(OnClipClickListener onClipClickListener) {
        mOnClipClickListener = onClipClickListener;
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //防止因mAssistClipList持有OnClipChangedListener引用从而导致这个RecyclerView及其Context被引用，内存泄漏
        mAssistClipList.setOnClipChangedListener(null);
    }

    private class Adapter extends ExpandableRecyclerView.DefaultTitleAdapter {

        Adapter() {
            setIcon(R.drawable.ic_robot_head_green);
            setTitle(R.string.text_assist_clip);
        }

        @Override
        protected RecyclerView.ViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
            return new ChildViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.assist_clip_list_recycler_view_item, parent, false));
        }

        @Override
        protected void onBindChildViewHolder(RecyclerView.ViewHolder holder, int position) {
            ChildViewHolder viewHolder = (ChildViewHolder) holder;
            viewHolder.mTextView.setText(mAssistClipList.get(position));
        }

        @Override
        protected int getChildItemCount() {
            return mAssistClipList.size();
        }

        @Override
        protected int getChildItemViewType(int position) {
            return 0;
        }

    }

    private class ChildViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView;

        ChildViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.clip);
        }

    }
}
