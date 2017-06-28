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
import com.stardust.scriptdroid.external.floatingwindow.menu.layout_inspector.view.LayoutBoundsView;
import com.stardust.scriptdroid.external.floatingwindow.menu.layout_inspector.view.NodeInfoView;
import com.stardust.scriptdroid.external.floatingwindow.menu.layout_inspector.view.OnNodeInfoSelectListener;
import com.stardust.util.MessageIntent;
import com.stardust.widget.BubblePopupMenu;

import java.util.Arrays;

/**
 * Created by Stardust on 2017/3/12.
 */

public class FloatingLayoutBoundsView extends LayoutBoundsView {

    private MaterialDialog mNodeInfoDialog;
    private BubblePopupMenu mBubblePopMenu;
    private NodeInfoView mNodeInfoView;
    private NodeInfo mSelectedNode;

    public FloatingLayoutBoundsView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setOnNodeInfoSelectListener(new OnNodeInfoSelectListener() {
            @Override
            public void onNodeSelect(NodeInfo info) {
                mSelectedNode = info;
                ensureOperationPopMenu();
                if (mBubblePopMenu.getContentView().getMeasuredWidth() <= 0)
                    mBubblePopMenu.preMeasure();
                mBubblePopMenu.showAsDropDownAtLocation(FloatingLayoutBoundsView.this, info.getBoundsInScreen().height(), info.getBoundsInScreen().centerX() - mBubblePopMenu.getContentView().getMeasuredWidth() / 2, info.getBoundsInScreen().bottom - getStatusBarHeight());
            }
        });
        setVisibility(GONE);
        getBoundsPaint().setStrokeWidth(2f);
    }


    private void showNodeInfo() {
        ensureDialog();
        mNodeInfoView.setNodeInfo(mSelectedNode);
        mNodeInfoDialog.show();
    }

    private void ensureOperationPopMenu() {
        if (mBubblePopMenu != null)
            return;
        mBubblePopMenu = new BubblePopupMenu(getContext(), Arrays.asList(
                getResources().getString(R.string.text_show_widget_infomation),
                getResources().getString(R.string.text_show_layout_hierarchy)));
        mBubblePopMenu.setOnItemClickListener(new BubblePopupMenu.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                mBubblePopMenu.dismiss();
                if (position == 0) {
                    showNodeInfo();
                } else {
                    HoverMenuService.postMessageIntent(new MessageIntent(HoverMenuService.ACTION_SHOW_NODE_LAYOUT_HIERARCHY)
                            .putExtra(HoverMenuService.EXTRA_NODE_INFO, mSelectedNode));
                }
            }
        });
        mBubblePopMenu.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mBubblePopMenu.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void ensureDialog() {
        if (mNodeInfoDialog == null) {
            mNodeInfoView = new NodeInfoView(getContext());
            mNodeInfoDialog = new MaterialDialog.Builder(getContext())
                    .customView(mNodeInfoView, false)
                    .theme(Theme.LIGHT)
                    .build();
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
