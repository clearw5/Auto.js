package com.stardust.autojs.core.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.stardust.autojs.core.ui.inflater.DynamicLayoutInflater;
import com.stardust.autojs.core.ui.inflater.ViewInflater;
import com.stardust.autojs.runtime.ScriptRuntime;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Map;

/**
 * Created by Stardust on 2018/3/28.
 */

public class JsListView extends RecyclerView {

    public interface DataSourceAdapter {

        int getItemCount(Object dataSource);

        Object getItem(Object dataSource, int i);

        void setDataSource(Object dataSource);
    }

    private Node mItemTemplate;
    private DynamicLayoutInflater mDynamicLayoutInflater;
    private ScriptRuntime mScriptRuntime;
    private Object mDataSource;
    private DataSourceAdapter mDataSourceAdapter;

    public JsListView(Context context, ScriptRuntime scriptRuntime) {
        super(context);
        mScriptRuntime = scriptRuntime;
        init();
    }

    private void init() {
        setAdapter(new Adapter());
        setLayoutManager(new LinearLayoutManager(getContext()));
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

    private class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(mDynamicLayoutInflater.inflate(mItemTemplate, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Object oldCtx = mScriptRuntime.ui.getBindingContext();
            mScriptRuntime.ui.setBindingContext(mDataSourceAdapter.getItem(mDataSource, position));
            applyDynamicAttrs(mItemTemplate, holder.itemView, (ViewGroup) holder.itemView.getParent());
            mScriptRuntime.ui.setBindingContext(oldCtx);
        }

        private void applyDynamicAttrs(Node node, View itemView, ViewGroup parent) {
            mDynamicLayoutInflater.applyAttributes(itemView, mDynamicLayoutInflater.getAttributesMap(node), parent);
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
