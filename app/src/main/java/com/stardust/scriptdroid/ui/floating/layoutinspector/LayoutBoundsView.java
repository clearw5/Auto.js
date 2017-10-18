package com.stardust.scriptdroid.ui.floating.layoutinspector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.stardust.view.accessibility.NodeInfo;
import com.stardust.util.ViewUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Stardust on 2017/3/10.
 */

public class LayoutBoundsView extends View {

    private static final int COLOR_SHADOW = 0x6a000000;
    private NodeInfo mRootNode;
    private NodeInfo mTouchedNode;
    private Paint mBoundsPaint;
    private Paint mFillingPaint;
    private int mStatusBarHeight;
    private OnNodeInfoSelectListener mOnNodeInfoSelectListener;
    private int mTouchedNodeBoundsColor = Color.RED;
    private int mNormalNodeBoundsColor = Color.GREEN;
    private Rect mTouchedNodeBounds;

    public LayoutBoundsView(Context context) {
        super(context);
        init();
    }

    public LayoutBoundsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LayoutBoundsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LayoutBoundsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    public void setOnNodeInfoSelectListener(OnNodeInfoSelectListener onNodeInfoSelectListener) {
        mOnNodeInfoSelectListener = onNodeInfoSelectListener;
    }

    public void setRootNode(NodeInfo rootNode) {
        mRootNode = rootNode;
        mTouchedNode = null;
    }


    private void init() {
        mBoundsPaint = new Paint();
        mBoundsPaint.setStyle(Paint.Style.STROKE);
        mFillingPaint = new Paint();
        mFillingPaint.setStyle(Paint.Style.FILL);
        mFillingPaint.setColor(COLOR_SHADOW);
        mStatusBarHeight = ViewUtil.getStatusBarHeight(getContext());
        setWillNotDraw(false);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mTouchedNode != null) {
            canvas.save();
            if (mTouchedNodeBounds == null) {
                mTouchedNodeBounds = new Rect(mTouchedNode.getBoundsInScreen());
                mTouchedNodeBounds.offset(0, -mStatusBarHeight);
            }
            canvas.clipRect(mTouchedNodeBounds, Region.Op.DIFFERENCE);
        }
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mFillingPaint);
        if (mTouchedNode != null) {
            canvas.restore();
        }
        mBoundsPaint.setColor(mNormalNodeBoundsColor);
        draw(canvas, mRootNode);
        if (mTouchedNode != null) {
            mBoundsPaint.setColor(mTouchedNodeBoundsColor);
            drawRect(canvas, mTouchedNode.getBoundsInScreen(), mStatusBarHeight, mBoundsPaint);
        }
    }

    public Paint getBoundsPaint() {
        return mBoundsPaint;
    }

    public Paint getFillingPaint() {
        return mFillingPaint;
    }

    public int getStatusBarHeight() {
        return mStatusBarHeight;
    }

    public void setTouchedNodeBoundsColor(int touchedNodeBoundsColor) {
        mTouchedNodeBoundsColor = touchedNodeBoundsColor;
    }

    public void setNormalNodeBoundsColor(int normalNodeBoundsColor) {
        mNormalNodeBoundsColor = normalNodeBoundsColor;
    }

    private void draw(Canvas canvas, NodeInfo node) {
        if (node == null)
            return;
        drawRect(canvas, node.getBoundsInScreen(), mStatusBarHeight, mBoundsPaint);
        for (NodeInfo child : node.getChildren()) {
            draw(canvas, child);
        }
    }

    static void drawRect(Canvas canvas, Rect rect, int statusBarHeight, Paint paint) {
        Rect offsetRect = new Rect(rect);
        offsetRect.offset(0, -statusBarHeight);
        canvas.drawRect(offsetRect, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mRootNode != null) {
            setSelectedNode(findNodeAt(mRootNode, (int) event.getRawX(), (int) event.getRawY()));
        }
        if (event.getAction() == MotionEvent.ACTION_UP && mTouchedNode != null) {
            onNodeInfoClick(mTouchedNode);
            return true;
        }
        return super.onTouchEvent(event);

    }

    private void onNodeInfoClick(NodeInfo nodeInfo) {
        if (mOnNodeInfoSelectListener != null) {
            mOnNodeInfoSelectListener.onNodeSelect(nodeInfo);
        }
    }

    private NodeInfo findNodeAt(NodeInfo node, int x, int y) {
        ArrayList<NodeInfo> list = new ArrayList<>();
        findNodeAt(node, x, y, list);
        if (list.isEmpty()) {
            return null;
        }
        return Collections.min(list, new Comparator<NodeInfo>() {
            @Override
            public int compare(NodeInfo o1, NodeInfo o2) {
                return o1.getBoundsInScreen().width() * o1.getBoundsInScreen().height() -
                        o2.getBoundsInScreen().width() * o2.getBoundsInScreen().height();
            }
        });
    }


    private void findNodeAt(NodeInfo node, int x, int y, List<NodeInfo> list) {
        for (NodeInfo child : node.getChildren()) {
            if (child != null && child.getBoundsInScreen().contains(x, y)) {
                list.add(child);
                findNodeAt(child, x, y, list);
            }
        }
    }

    public void setSelectedNode(NodeInfo selectedNode) {
        mTouchedNode = selectedNode;
        mTouchedNodeBounds = null;
        invalidate();
    }
}
