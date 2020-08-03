package org.autojs.autojs.ui.floating.layoutinspector;

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

import org.autojs.autojs.R;
import com.stardust.view.accessibility.NodeInfo;
import com.stardust.util.ViewUtil;
import org.autojs.autojs.ui.widget.LevelBeamView;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Stack;

import pl.openrnd.multilevellistview.ItemInfo;
import pl.openrnd.multilevellistview.MultiLevelListAdapter;
import pl.openrnd.multilevellistview.MultiLevelListView;
import pl.openrnd.multilevellistview.NestType;
import pl.openrnd.multilevellistview.OnItemClickListener;

/**
 * Created by Stardust on 2017/3/10.
 */

public class LayoutHierarchyView extends MultiLevelListView {

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, NodeInfo nodeInfo);
    }

    private Adapter mAdapter;
    private OnItemLongClickListener mOnItemLongClickListener;
    private AdapterView.OnItemLongClickListener mOnItemLongClickListenerProxy = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if (mOnItemLongClickListener != null) {
                mOnItemLongClickListener.onItemLongClick(view, ((ViewHolder) view.getTag()).nodeInfo);
                return true;
            }
            return false;
        }

    };

    private Paint mPaint;
    private int[] mBoundsInScreen;
    private int mStatusBarHeight;
    private NodeInfo mClickedNodeInfo;
    private View mClickedView;
    private Drawable mOriginalBackground;

    private boolean mShowClickedNodeBounds;
    private int mClickedColor = 0x99b2b3b7;
    private NodeInfo mRootNode;
    private Set<NodeInfo> mInitiallyExpandedNodes = new HashSet<>();

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
        mRootNode = rootNodeInfo;
        mAdapter.setDataItems(Collections.singletonList(rootNodeInfo));
        mClickedNodeInfo = null;
        mInitiallyExpandedNodes.clear();
    }

    public void setOnItemLongClickListener(final OnItemLongClickListener onNodeInfoSelectListener) {
        mOnItemLongClickListener = onNodeInfoSelectListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBoundsInScreen == null) {
            mBoundsInScreen = new int[4];
            getLocationOnScreen(mBoundsInScreen);
            mStatusBarHeight = mBoundsInScreen[1];
        }
        if (mShowClickedNodeBounds && mClickedNodeInfo != null) {
            LayoutBoundsView.drawRect(canvas, mClickedNodeInfo.getBoundsInScreen(), mStatusBarHeight, mPaint);
        }
    }

    public void setSelectedNode(NodeInfo selectedNode) {
        mInitiallyExpandedNodes.clear();
        Stack<NodeInfo> parents = new Stack<>();
        searchNodeParents(selectedNode, mRootNode, parents);
        mClickedNodeInfo = parents.peek();
        mInitiallyExpandedNodes.addAll(parents);
        mAdapter.reloadData();
    }

    private boolean searchNodeParents(NodeInfo nodeInfo, NodeInfo rootNode, Stack<NodeInfo> stack) {
        stack.push(rootNode);
        if (nodeInfo == rootNode) {
            return true;
        }
        boolean found = false;
        for (NodeInfo child : rootNode.getChildren()) {
            if (searchNodeParents(nodeInfo, child, stack)) {
                found = true;
                break;
            }
        }
        if (!found) {
            stack.pop();
        }
        return found;
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
        protected List<?> getSubObjects(Object object) {
            return ((NodeInfo) object).getChildren();
        }

        @Override
        protected boolean isExpandable(Object object) {
            return !((NodeInfo) object).getChildren().isEmpty();
        }

        @Override
        protected boolean isInitiallyExpanded(Object object) {
            return mInitiallyExpandedNodes.contains((NodeInfo) object);
        }

        @Override
        public View getViewForObject(Object object, View convertView, ItemInfo itemInfo) {
            NodeInfo nodeInfo = (NodeInfo) object;
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_hierarchy_view_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.nameView.setText(simplifyClassName(nodeInfo.getClassName()));
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

            if (nodeInfo == mClickedNodeInfo) {
                setClickedItem(convertView, nodeInfo);
            }
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
