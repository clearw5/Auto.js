package com.stardust.scriptdroid.layout_inspector.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ViewSwitcher;

import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.layout_inspector.LayoutInspector;
import com.stardust.scriptdroid.layout_inspector.NodeInfo;

/**
 * Created by Stardust on 2017/3/10.
 */

public class LayoutInspectView extends FrameLayout {

    private ViewSwitcher mViewSwitcher;
    private NodeInfoView mNodeInfoView;
    private LayoutHierarchyView mLayoutBoundsView;
    private View mCurrentView;

    public LayoutInspectView(@NonNull Context context) {
        super(context);
        init();
    }

    public LayoutInspectView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LayoutInspectView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LayoutInspectView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.floating_window_expand, this);
        mNodeInfoView = (NodeInfoView) findViewById(R.id.node_info);
        mLayoutBoundsView = (LayoutHierarchyView) findViewById(R.id.bounds_view);
        mViewSwitcher = (ViewSwitcher) findViewById(R.id.view_switcher);
        mCurrentView = mNodeInfoView;
        mLayoutBoundsView.setOnNodeInfoLongClickListener(new OnNodeInfoSelectListener() {
            @Override
            public void onNodeSelect(NodeInfo info) {
                mNodeInfoView.setNodeInfo(info);
                showNodeInfoView();
            }
        });
    }

    public void showNodeInfoView() {
        if (mCurrentView != mNodeInfoView) {
            mViewSwitcher.showPrevious();
            mCurrentView = mNodeInfoView;
        }
    }

    public void showLayoutBoundsView() {
        mLayoutBoundsView.setRootNode(LayoutInspector.getInstance().captureCurrentWindow());
        backToLayoutBoundsView();
    }

    public boolean isLayoutBoundsViewShowing() {
        return mCurrentView == mLayoutBoundsView;
    }

    public boolean isNodeInfoViewShowing() {
        return mCurrentView == mNodeInfoView;
    }

    public void backToLayoutBoundsView() {
        if (mCurrentView != mLayoutBoundsView) {
            mViewSwitcher.showNext();
            mCurrentView = mLayoutBoundsView;
        }
    }
}
