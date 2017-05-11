package com.stardust.scriptdroid.external.floating_window.menu.layout_inspector.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.external.floating_window.menu.layout_inspector.NodeInfo;
import com.stardust.util.ViewUtil;
import com.stardust.widget.LevelBeamView;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import pl.openrnd.multilevellistview.ItemInfo;
import pl.openrnd.multilevellistview.MultiLevelListAdapter;
import pl.openrnd.multilevellistview.MultiLevelListView;
import pl.openrnd.multilevellistview.NestType;
import pl.openrnd.multilevellistview.OnItemClickListener;

/**
 * Created by Stardust on 2017/3/10.
 */

public class LayoutHierarchyView extends MultiLevelListView {

    private Adapter mAdapter;
    private OnNodeInfoSelectListener mOnNodeInfoSelectListener;
    private AdapterView.OnItemLongClickListener mOnItemLongClickListenerProxy = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if (mOnNodeInfoSelectListener != null) {
                mOnNodeInfoSelectListener.onNodeSelect(((ViewHolder) view.getTag()).nodeInfo);
                return true;
            }
            return false;
        }

    };

    private Paint mPaint;
    private int mStatusBarHeight;
    private NodeInfo mClickedNodeInfo;
    private View mClickedView;
    private Drawable mOriginalBackground;

    private boolean mShowClickedNodeBounds;
    private int mClickedColor = 0x77c4c4c4;

    public LayoutHierarchyView(Context context) {
        super(context);
        init();
    }

    public LayoutHierarchyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LayoutHierarchyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setShowClickedNodeBounds(boolean showClickedNodeBounds) {
        mShowClickedNodeBounds = showClickedNodeBounds;
    }

    public void setClickedColor(int clickedColor) {
        mClickedColor = clickedColor;
    }


    private void init() {
        mAdapter = new Adapter();
        setAdapter(mAdapter);
        setNestType(NestType.MULTIPLE);
        ((ListView) getChildAt(0)).setOnItemLongClickListener(mOnItemLongClickListenerProxy);
        setWillNotDraw(false);
        initPaint();
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClicked(MultiLevelListView parent, View view, Object item, ItemInfo itemInfo) {
                setClickedItem(view, (NodeInfo) item);
            }

            @Override
            public void onGroupItemClicked(MultiLevelListView parent, View view, Object item, ItemInfo itemInfo) {
                setClickedItem(view, (NodeInfo) item);
            }
        });
    }

    private void setClickedItem(View view, NodeInfo item) {
        mClickedNodeInfo = item;
        if (mClickedView == null) {
            mOriginalBackground = view.getBackground();
        } else {
            mClickedView.setBackground(mOriginalBackground);
        }
        view.setBackgroundColor(mClickedColor);
        mClickedView = view;
        invalidate();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(Color.DKGRAY);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(3);
        mStatusBarHeight = ViewUtil.getStatusBarHeight(getContext());
    }

    public Paint getBoundsPaint() {
        return mPaint;
    }

    public void setRootNode(NodeInfo rootNodeInfo) {
        mAdapter.setDataItems(Collections.singletonList(rootNodeInfo));
        mClickedNodeInfo = null;
    }

    public void setOnNodeInfoLongClickListener(final OnNodeInfoSelectListener onNodeInfoSelectListener) {
        mOnNodeInfoSelectListener = onNodeInfoSelectListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mShowClickedNodeBounds && mClickedNodeInfo != null) {
            LayoutBoundsView.drawRect(canvas, mClickedNodeInfo.getBoundsInScreen(), mStatusBarHeight, mPaint);
        }
    }

    private class ViewHolder {
        TextView nameView;
        TextView infoView;
        ImageView arrowView;
        LevelBeamView levelBeamView;
        NodeInfo nodeInfo;

        ViewHolder(View view) {
            infoView = (TextView) view.findViewById(R.id.dataItemInfo);
            nameView = (TextView) view.findViewById(R.id.dataItemName);
            arrowView = (ImageView) view.findViewById(R.id.dataItemArrow);
            levelBeamView = (LevelBeamView) view.findViewById(R.id.dataItemLevelBeam);
        }
    }


    private class Adapter extends MultiLevelListAdapter {


        @Override
        public List<?> getSubObjects(Object object) {
            return ((NodeInfo) object).getChildren();
        }

        @Override
        public boolean isExpandable(Object object) {
            return !((NodeInfo) object).getChildren().isEmpty();
        }

        @Override
        public View getViewForObject(Object object, View convertView, ItemInfo itemInfo) {
            NodeInfo nodeInfo = (NodeInfo) object;
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_hierarchy_view_item, LayoutHierarchyView.this, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.nameView.setText(simplifyClassName(nodeInfo.className));
            viewHolder.nodeInfo = nodeInfo;
            if (viewHolder.infoView.getVisibility() == VISIBLE)
                viewHolder.infoView.setText(getItemInfoDsc(itemInfo));

            if (itemInfo.isExpandable() && !isAlwaysExpanded()) {
                viewHolder.arrowView.setVisibility(View.VISIBLE);
                viewHolder.arrowView.setImageResource(itemInfo.isExpanded() ?
                        R.drawable.arrow_up : R.drawable.arrow_down);
            } else {
                viewHolder.arrowView.setVisibility(View.GONE);
            }

            viewHolder.levelBeamView.setLevel(itemInfo.getLevel());

            return convertView;
        }

        private String simplifyClassName(CharSequence className) {
            if (className == null)
                return null;
            String s = className.toString();
            if (s.startsWith("android.widget.")) {
                s = s.substring(15);
            }
            return s;
        }


        private String getItemInfoDsc(ItemInfo itemInfo) {
            StringBuilder builder = new StringBuilder();
            builder.append(String.format(Locale.getDefault(), "level[%d], idx in level[%d/%d]",
                    itemInfo.getLevel() + 1, /*Indexing starts from 0*/
                    itemInfo.getIdxInLevel() + 1 /*Indexing starts from 0*/,
                    itemInfo.getLevelSize()));

            if (itemInfo.isExpandable()) {
                builder.append(String.format(", expanded[%b]", itemInfo.isExpanded()));
            }
            return builder.toString();
        }
    }

}
