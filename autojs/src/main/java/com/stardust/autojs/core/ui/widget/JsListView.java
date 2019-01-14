package com.stardust.autojs.core.ui.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.stardust.autojs.core.ui.ViewExtras;
import com.stardust.autojs.core.ui.inflater.DynamicLayoutInflater;
import com.stardust.autojs.core.ui.nativeview.NativeView;
import com.stardust.autojs.core.ui.nativeview.ViewPrototype;
import com.stardust.autojs.runtime.ScriptRuntime;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.stardust.autojs.workground.WrapContentLinearLayoutManager;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Stardust on 2018/3/28.
 */

public class JsListView extends RecyclerView {

    public interface DataSourceAdapter {

        int getItemCount(Object dataSource);

        Object getItem(Object dataSource, int i);

        void setDataSource(Object dataSource);
    }

    public interface OnItemTouchListener {
        void onItemClick(JsListView listView, View itemView, Object item, int pos);

        boolean onItemLongClick(JsListView listView, View itemView, Object item, int pos);
    }

    private Node mItemTemplate;
    private DynamicLayoutInflater mDynamicLayoutInflater;
    private ScriptRuntime mScriptRuntime;
    private Object mDataSource;
    private DataSourceAdapter mDataSourceAdapter;
    private OnItemTouchListener mOnItemTouchListener;

    public JsListView(Context context, ScriptRuntime scriptRuntime) {
        super(context);
        mScriptRuntime = scriptRuntime;
        init();
    }

    protected void init() {
        setAdapter(new Adapter());
        setLayoutManager(new WrapContentLinearLayoutManager(getContext()));
    }

    protected ScriptRuntime getScriptRuntime() {
        return mScriptRuntime;
    }

    public void setOnItemTouchListener(OnItemTouchListener onItemTouchListener) {
        mOnItemTouchListener = onItemTouchListener;
    }

    public void setDataSourceAdapter(DataSourceAdapter dataSourceAdapter) {
        mDataSourceAdapter = dataSourceAdapter;
        getAdapter().notifyDataSetChanged();
    }

    public Object getDataSource() {
        return mDataSource;
    }

    public void setDataSource(Object dataSource) {
        mDataSource = dataSource;
        if (mDataSourceAdapter != null)
            mDataSourceAdapter.setDataSource(dataSource);
        getAdapter().notifyDataSetChanged();
    }

    public void setItemTemplate(DynamicLayoutInflater inflater, Node itemTemplate) {
        mDynamicLayoutInflater = inflater;
        mItemTemplate = itemTemplate;
    }


    public static class ItemHolder {
        private final ViewHolder mViewHolder;

        ItemHolder(ViewHolder viewHolder) {
            mViewHolder = viewHolder;
        }

        public int getPosition() {
            return mViewHolder.getAdapterPosition();
        }

        public Object getItem() {
            return mViewHolder.item;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        Object item = null;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(v -> {
                if (mOnItemTouchListener != null) {
                    int pos = getAdapterPosition();
                    mOnItemTouchListener.onItemClick(JsListView.this, itemView, mDataSourceAdapter.getItem(mDataSource, pos), pos);
                }
            });
            itemView.setOnLongClickListener(v -> {
                if (mOnItemTouchListener == null)
                    return false;
                int pos = getAdapterPosition();
                return mOnItemTouchListener.onItemLongClick(JsListView.this, itemView, mDataSourceAdapter.getItem(mDataSource, pos), pos);
            });
            NativeView nativeView = ViewExtras.getNativeView(JsListView.this);
            if (nativeView != null) {
                ViewPrototype prototype = nativeView.getViewPrototype();
                prototype.emit("item_bind", itemView, new ItemHolder(this));
            }
        }


    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            try {
                mDynamicLayoutInflater.setInflateFlags(DynamicLayoutInflater.FLAG_IGNORES_DYNAMIC_ATTRS);
                return new ViewHolder(mDynamicLayoutInflater.inflate(mDynamicLayoutInflater.newInflateContext(), mItemTemplate, parent, false));
            } catch (Exception e) {
                mScriptRuntime.exit(e);
                return new ViewHolder(new View(parent.getContext()));
            } finally {
                mDynamicLayoutInflater.setInflateFlags(DynamicLayoutInflater.FLAG_DEFAULT);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            try {
                Object oldCtx = mScriptRuntime.ui.getBindingContext();
                Object item = mDataSourceAdapter.getItem(mDataSource, position);
                holder.item = item;
                mScriptRuntime.ui.setBindingContext(item);
                mDynamicLayoutInflater.setInflateFlags(DynamicLayoutInflater.FLAG_JUST_DYNAMIC_ATTRS);
                applyDynamicAttrs(mItemTemplate, holder.itemView, JsListView.this);
                mScriptRuntime.ui.setBindingContext(oldCtx);
            } catch (Exception e) {
                mScriptRuntime.exit(e);
            } finally {
                mDynamicLayoutInflater.setInflateFlags(DynamicLayoutInflater.FLAG_DEFAULT);
            }
        }

        private void applyDynamicAttrs(Node node, View itemView, ViewGroup parent) {
            mDynamicLayoutInflater.applyAttributes(mDynamicLayoutInflater.newInflateContext(), itemView, mDynamicLayoutInflater.getAttributesMap(node), parent);
            if (!(itemView instanceof ViewGroup))
                return;
            ViewGroup viewGroup = (ViewGroup) itemView;
            NodeList nodeList = node.getChildNodes();
            int j = 0;
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node child = nodeList.item(i);
                if (child.getNodeType() != Node.ELEMENT_NODE) continue;
                applyDynamicAttrs(child, viewGroup.getChildAt(j), viewGroup);
                j++;
            }
        }

        @Override
        public int getItemCount() {
            return mDataSource == null ? 0
                    : mDataSourceAdapter == null ? 0
                    : mDataSourceAdapter.getItemCount(mDataSource);
        }
    }

}
