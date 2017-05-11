package com.stardust.scriptdroid.external.floating_window.menu.view;

import android.content.Context;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.stardust.scriptdroid.external.floating_window.menu.HoverMenuService;
import com.stardust.scriptdroid.external.floating_window.menu.layout_inspector.NodeInfo;
import com.stardust.scriptdroid.external.floating_window.menu.layout_inspector.view.LayoutBoundsView;
import com.stardust.scriptdroid.external.floating_window.menu.layout_inspector.view.NodeInfoView;
import com.stardust.scriptdroid.external.floating_window.menu.layout_inspector.view.OnNodeInfoSelectListener;
import com.stardust.util.MessageEvent;

/**
 * Created by Stardust on 2017/3/12.
 */

public class FloatingLayoutBoundsView extends LayoutBoundsView {

    private MaterialDialog mNodeInfoDialog;
    private NodeInfoView mNodeInfoView;

    public FloatingLayoutBoundsView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setOnNodeInfoSelectListener(new OnNodeInfoSelectListener() {
            @Override
            public void onNodeSelect(NodeInfo info) {
                showNodeInfo(info);
            }
        });
        setVisibility(GONE);
        getPaint().setColor(0xFF222222);
        getPaint().setStrokeWidth(2f);
    }


    private void showNodeInfo(NodeInfo info) {
        ensureDialog();
        mNodeInfoView.setNodeInfo(info);
        mNodeInfoDialog.show();
    }

    private void ensureDialog() {
        if (mNodeInfoDialog == null) {
            mNodeInfoView = new NodeInfoView(getContext());
            mNodeInfoDialog = new MaterialDialog.Builder(getContext())
                    .customView(mNodeInfoView, false)
                    .theme(Theme.LIGHT)
                    .build();
            mNodeInfoDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            HoverMenuService.postEvent(new MessageEvent(HoverMenuService.MESSAGE_SHOW_AND_EXPAND_MENU));
            setVisibility(GONE);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


}
