package org.autojs.autojs.ui.widget;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ThemeColorRecyclerView;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.autojs.autojs.R;

/**
 * Created by Stardust on 2017/2/4.
 */

public class ExpandableRecyclerView extends ThemeColorRecyclerView {

    public void setOnChildClickListener(OnChildClickListener onChildClickListener) {
        mOnChildClickListener = onChildClickListener;
    }

    public interface OnChildClickListener {

        void onClick(View view, int position);
    }


    private OnClickListener mOnTitleClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            toggle();
        }

    };
    private OnClickListener mOnChildClickListenerWrapper = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mOnChildClickListener != null) {
                mOnChildClickListener.onClick(v, getChildViewHolder(v).getAdapterPosition() - 1);
            }
        }
    };
    private boolean mExpanded;
    private OnChildClickListener mOnChildClickListener;

    public ExpandableRecyclerView(Context context) {
        super(context);
        init();
    }

    public ExpandableRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExpandableRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {

    }

    public void toggle() {
        if (isExpanded()) {
            collapse();
        } else {
            expand();
        }
    }


    public boolean isExpanded() {
        return mExpanded;
    }

    public void expand() {
        if (mExpanded)
            return;
        mExpanded = true;
        ((Adapter) getAdapter()).notifyExpanded();
    }

    public void collapse() {
        if (!mExpanded)
            return;
        mExpanded = false;
        ((Adapter) getAdapter()).notifyCollapsed();
    }

    public abstract class Adapter extends RecyclerView.Adapter<ViewHolder> {

        protected static final int VIEW_TYPE_TITLE = 1;

        public void notifyExpanded() {
            notifyItemChanged(0);
            notifyItemRangeInserted(1, getChildItemCount());
        }

        public void notifyCollapsed() {
            notifyItemRangeRemoved(1, getChildItemCount());
            notifyItemChanged(0);
        }

        @Override
        public int getItemCount() {
            return mExpanded ? getChildItemCount() + 1 : 1;
        }

        protected abstract int getChildItemCount();

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? VIEW_TYPE_TITLE : getChildItemViewType(position - 1);
        }

        protected abstract int getChildItemViewType(int position);

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_TITLE) {
                ViewHolder holder = onCreateTitleViewHolder(parent);
                holder.itemView.setOnClickListener(mOnTitleClickListener);
                return holder;
            } else {
                ViewHolder holder = onCreateChildViewHolder(parent, viewType);
                holder.itemView.setOnClickListener(mOnChildClickListenerWrapper);
                return holder;
            }
        }

        protected abstract ViewHolder onCreateTitleViewHolder(ViewGroup parent);

        protected abstract ViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType);

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (holder.getItemViewType() == VIEW_TYPE_TITLE) {
                onBindTitleViewHolder(holder);
            } else {
                onBindChildViewHolder(holder, position - 1);
            }
        }

        protected abstract void onBindChildViewHolder(ViewHolder holder, int position);

        protected abstract void onBindTitleViewHolder(ViewHolder holder);

    }

    public abstract class DefaultTitleAdapter extends Adapter {

        private int mIconResId = -1;
        private String mTitle;

        public void setIcon(int iconResId) {
            mIconResId = iconResId;
        }

        public void setTitle(int resId) {
            mTitle = getContext().getString(resId);
        }

        public void setTitle(String title) {
            mTitle = title;
        }

        @Override
        protected RecyclerView.ViewHolder onCreateTitleViewHolder(ViewGroup parent) {
            return new TitleViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.expanded_recycler_view_default_title, parent, false));
        }


        @Override
        protected void onBindTitleViewHolder(RecyclerView.ViewHolder holder) {
            TitleViewHolder viewHolder = (TitleViewHolder) holder;
            viewHolder.mExpandHint.setImageResource(isExpanded() ? R.drawable.ic_expanded : R.drawable.ic_collapsed);
        }


        private class TitleViewHolder extends RecyclerView.ViewHolder {

            ImageView mExpandHint;

            TitleViewHolder(View itemView) {
                super(itemView);
                mExpandHint = (ImageView) itemView.findViewById(R.id.expand_hint);
                ((TextView) itemView.findViewById(R.id.title)).setText(mTitle);
                if (mIconResId != -1) {
                    ((ImageView) itemView.findViewById(R.id.icon)).setImageResource(mIconResId);
                } else {
                    itemView.findViewById(R.id.icon).setVisibility(GONE);
                }
            }

        }

    }


}
