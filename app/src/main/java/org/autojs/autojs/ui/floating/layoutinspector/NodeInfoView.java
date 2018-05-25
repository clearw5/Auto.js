package org.autojs.autojs.ui.floating.layoutinspector;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.autojs.autojs.R;
import com.stardust.view.accessibility.NodeInfo;
import com.stardust.util.ClipboardUtil;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.lang.reflect.Field;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Stardust on 2017/3/10.
 */

public class NodeInfoView extends RecyclerView {

    private static final String[] FIELD_NAMES = {
            "id",
            "bounds",
            "depth",
            "desc",
            "className",
            "packageName",
            "text",
            "drawingOrder",
            "accessibilityFocused",
            "checked",
            "clickable",
            "contextClickable",
            "dismissable",
            "editable",
            "enabled",
            "focusable",
            "indexInParent",
            "longClickable",
            "row",
            "rowCount",
            "rowSpan",
            "column",
            "columnCount",
            "columnSpan",
            "selected",
            "scrollable",
    };
    private static final Field[] FIELDS = new Field[FIELD_NAMES.length];

    static {
        Arrays.sort(FIELD_NAMES);
        for (int i = 0; i < FIELD_NAMES.length; i++) {
            try {
                FIELDS[i] = NodeInfo.class.getDeclaredField(FIELD_NAMES[i]);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String[][] mData = new String[FIELDS.length + 1][2];

    public NodeInfoView(Context context) {
        super(context);
        init();
    }

    public NodeInfoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NodeInfoView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setNodeInfo(NodeInfo nodeInfo) {
        for (int i = 0; i < FIELDS.length; i++) {
            try {
                Object value = FIELDS[i].get(nodeInfo);
                mData[i + 1][1] = value == null ? "" : value.toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        getAdapter().notifyDataSetChanged();
    }

    private void init() {
        initData();
        setAdapter(new Adapter());
        setLayoutManager(new LinearLayoutManager(getContext()));
        addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext())
                .color(0x1e000000)
                .size(2)
                .build());
    }

    private void initData() {
        mData[0][0] = getResources().getString(R.string.text_attribute);
        mData[0][1] = getResources().getString(R.string.text_value);
        for (int i = 1; i < mData.length; i++) {
            mData[i][0] = FIELD_NAMES[i - 1];
            mData[i][1] = "";
        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        final int VIEW_TYPE_HEADER = 0;
        final int VIEW_TYPE_ITEM = 1;


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int layoutRes = viewType == VIEW_TYPE_HEADER ? R.layout.node_info_view_header : R.layout.node_info_view_item;
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.attrName.setText(mData[position][0]);
            holder.attrValue.setText(mData[position][1]);
        }

        @Override
        public int getItemCount() {
            return mData.length;
        }

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.name)
        TextView attrName;

        @BindView(R.id.value)
        TextView attrValue;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Optional
        @OnClick(R.id.item)
        void onItemClick() {
            int pos = getAdapterPosition();
            if (pos < 1 || pos >= mData.length)
                return;
            ClipboardUtil.setClip(getContext(), mData[pos][0] + " = " + mData[pos][1]);
            Toast.makeText(getContext(), R.string.text_already_copy_to_clip, Toast.LENGTH_SHORT).show();
        }
    }

}