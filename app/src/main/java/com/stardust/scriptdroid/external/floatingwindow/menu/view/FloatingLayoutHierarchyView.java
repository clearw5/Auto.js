package com.stardust.scriptdroid.external.floatingwindow.menu.view;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.external.floatingwindow.menu.HoverMenuService;
import com.stardust.scriptdroid.external.floatingwindow.menu.layout_inspector.NodeInfo;
import com.stardust.scriptdroid.external.floatingwindow.menu.layout_inspector.view.LayoutHierarchyView;
import com.stardust.scriptdroid.external.floatingwindow.menu.layout_inspector.view.NodeInfoView;
import com.stardust.util.MessageIntent;
import com.stardust.widget.BubblePopupMenu;

import java.util.Arrays;

/**
 * Created by Stardust on 2017/3/12.
 */

public class FloatingLayoutHierarchyView extends LayoutHierarchyView {

    private static final String TAG = "FloatingHierarchyView";
    private static final int COLOR_SHADOW = 0xddffffff;

    private MaterialDialog mNodeInfoDialog;
    private BubblePopupMenu mBubblePopMenu;
    private NodeInfoView mNodeInfoView;
    private NodeInfo mSelectedNodeInfo;

    public FloatingLayoutHierarchyView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setBackgroundColor(COLOR_SHADOW);
        setVisibility(GONE);
        setShowClickedNodeBounds(true);
        getBoundsPaint().setStrokeWidth(3);
        getBoundsPaint().setColor(0xFFD32F2F);
        setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, NodeInfo nodeInfo) {
                mSelectedNodeInfo = nodeInfo;
                ensureOperationPopMenu();
                if (mBubblePopMenu.getContentView().getMeasuredWidth() <= 0)
                    mBubblePopMenu.preMeasure();
                mBubblePopMenu.showAsDropDown(view, view.getWidth() / 2 - mBubblePopMenu.getContentView().getMeasuredWidth() / 2, 0);
            }
        });
    }

    private void ensureOperationPopMenu() {
        if (mBubblePopMenu != null)
            return;
        mBubblePopMenu = new BubblePopupMenu(getContext(), Arrays.asList(
                getResources().getString(R.string.text_show_widget_infomation),
                getResources().getString(R.string.text_show_layout_bounds)));
        mBubblePopMenu.setOnItemClickListener(new BubblePopupMenu.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                mBubblePopMenu.dismiss();
                if (position == 0) {
                    showNodeInfo();
                } else {
                    HoverMenuService.postMessageIntent(new MessageIntent(HoverMenuService.ACTION_SHOW_NODE_LAYOUT_BOUNDS)
                            .putExtra(HoverMenuService.EXTRA_NODE_INFO, mSelectedNodeInfo));
                }
            }
        });
        mBubblePopMenu.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mBubblePopMenu.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    void showNodeInfo() {
        ensureNodeInfoDialog();
        mNodeInfoView.setNodeInfo(mSelectedNodeInfo);
        mNodeInfoDialog.show();
    }

    private void ensureNodeInfoDialog() {
        if (mNodeInfoDialog == null) {
            mNodeInfoView = new NodeInfoView(getContext());
            mNodeInfoDialog = new MaterialDialog.Builder(getContext())
                    .customView(mNodeInfoView, false)
                    .theme(Theme.LIGHT)
                    .build();
            if (mNodeInfoDialog.getWindow() != null)
                mNodeInfoDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            HoverMenuService.postIntent(new Intent(HoverMenuService.ACTION_SHOW_AND_EXPAND_MENU));
            setVisibility(GONE);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


}
