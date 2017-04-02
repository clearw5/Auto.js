package com.stardust.scriptdroid.layout_inspector.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.stardust.scriptdroid.layout_inspector.NodeInfo;
import com.stardust.util.ViewUtil;

/**
 * Created by Stardust on 2017/3/10.
 */

public class LayoutBoundsView extends View {

    private NodeInfo mRootNode;


    private Paint mPaint;
    private int mStatusBarHeight;
    private OnNodeInfoSelectListener mOnNodeInfoSelectListener;

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
    }


    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mStatusBarHeight = ViewUtil.getStatusBarHeight(getContext());
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        draw(canvas, mRootNode);
    }

    public Paint getPaint() {
        return mPaint;
    }


    private void draw(Canvas canvas, NodeInfo node) {
        if (node == null)
            return;
        drawRect(canvas, node.getBoundsInScreen(), mStatusBarHeight, mPaint);
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
        if (event.getAction() == MotionEvent.ACTION_DOWN && mRootNode != null) {
            NodeInfo nodeInfo = findNodeAt(mRootNode, (int) event.getRawX(), (int) event.getRawY());
            onNodeInfoClick(nodeInfo);
        }
        return super.onTouchEvent(event);

    }

    private void onNodeInfoClick(NodeInfo nodeInfo) {
        if (mOnNodeInfoSelectListener != null) {
            mOnNodeInfoSelectListener.onNodeSelect(nodeInfo);
        }
    }

    private NodeInfo findNodeAt(NodeInfo node, int x, int y) {
        for (NodeInfo child : node.getChildren()) {
            if (child != null && child.getBoundsInScreen().contains(x, y)) {
                return findNodeAt(child, x, y);
            }
        }
        return node;
    }

}
