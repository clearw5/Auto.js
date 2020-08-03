package org.autojs.autojs.ui.floating.layoutinspector;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.stardust.app.DialogUtils;
import com.stardust.enhancedfloaty.FloatyService;

import org.autojs.autojs.R;
import org.autojs.autojs.ui.codegeneration.CodeGenerateDialog;
import org.autojs.autojs.ui.floating.FloatyWindowManger;
import org.autojs.autojs.ui.floating.FullScreenFloatyWindow;

import com.stardust.view.accessibility.LayoutInspector;
import com.stardust.view.accessibility.NodeInfo;

import org.autojs.autojs.ui.widget.BubblePopupMenu;

import java.util.Arrays;

/**
 * Created by Stardust on 2017/3/12.
 */

public class LayoutBoundsFloatyWindow extends FullScreenFloatyWindow {

    private LayoutBoundsView mLayoutBoundsView;
    private MaterialDialog mNodeInfoDialog;
    private BubblePopupMenu mBubblePopMenu;
    private NodeInfoView mNodeInfoView;
    private NodeInfo mSelectedNode;
    private Context mContext;
    private NodeInfo mRootNode;

    public LayoutBoundsFloatyWindow(NodeInfo rootNode) {
        mRootNode = rootNode;
    }

    public static void capture(LayoutInspector inspector, Context context) {
        LayoutInspector.CaptureAvailableListener listener = new LayoutInspector.CaptureAvailableListener() {
            @Override
            public void onCaptureAvailable(NodeInfo capture) {
                inspector.removeCaptureAvailableListener(this);
                LayoutBoundsFloatyWindow window = new LayoutBoundsFloatyWindow(capture);
                FloatyWindowManger.addWindow(context, window);
            }
        };
        inspector.addCaptureAvailableListener(listener);
        if (!inspector.captureCurrentWindow()) {
            inspector.removeCaptureAvailableListener(listener);
        }
    }

    @Override
    protected View onCreateView(FloatyService floatyService) {
        mContext = new ContextThemeWrapper(floatyService, R.style.AppTheme);
        mLayoutBoundsView = new LayoutBoundsView(mContext) {
            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    close();
                    return true;
                }
                return super.dispatchKeyEvent(event);
            }
        };
        return mLayoutBoundsView;
    }

    protected void onViewCreated(View v) {
        mLayoutBoundsView.setOnNodeInfoSelectListener(info -> {
            mSelectedNode = info;
            ensureOperationPopMenu();
            if (mBubblePopMenu.getContentView().getMeasuredWidth() <= 0)
                mBubblePopMenu.preMeasure();
            mBubblePopMenu.showAsDropDownAtLocation(mLayoutBoundsView, info.getBoundsInScreen().height(), info.getBoundsInScreen().centerX() - mBubblePopMenu.getContentView().getMeasuredWidth() / 2, info.getBoundsInScreen().bottom - mLayoutBoundsView.getStatusBarHeight());
        });
        mLayoutBoundsView.getBoundsPaint().setStrokeWidth(2f);
        mLayoutBoundsView.setRootNode(mRootNode);
        if (mSelectedNode != null)
            mLayoutBoundsView.setSelectedNode(mSelectedNode);
    }


    private void showNodeInfo() {
        ensureDialog();
        mNodeInfoView.setNodeInfo(mSelectedNode);
        mNodeInfoDialog.show();
    }

    private void ensureOperationPopMenu() {
        if (mBubblePopMenu != null)
            return;
        mBubblePopMenu = new BubblePopupMenu(mContext, Arrays.asList(
                mContext.getString(R.string.text_show_widget_infomation),
                mContext.getString(R.string.text_show_layout_hierarchy),
                mContext.getString(R.string.text_generate_code)));
        mBubblePopMenu.setOnItemClickListener((view, position) -> {
            mBubblePopMenu.dismiss();
            if (position == 0) {
                showNodeInfo();
            } else if (position == 1) {
                showLayoutHierarchy();
            } else {
                generateCode();
            }
        });
        mBubblePopMenu.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mBubblePopMenu.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void generateCode() {
        DialogUtils.showDialog(new CodeGenerateDialog(mContext, mRootNode, mSelectedNode)
                .build());
    }

    private void showLayoutHierarchy() {
        close();
        LayoutHierarchyFloatyWindow window = new LayoutHierarchyFloatyWindow(mRootNode);
        window.setSelectedNode(mSelectedNode);
        FloatyService.addWindow(window);
    }

    private void ensureDialog() {
        if (mNodeInfoDialog == null) {
            mNodeInfoView = new NodeInfoView(mContext);
            mNodeInfoDialog = new MaterialDialog.Builder(mContext)
                    .customView(mNodeInfoView, false)
                    .theme(Theme.LIGHT)
                    .build();
            mNodeInfoDialog.getWindow().setType(FloatyWindowManger.getWindowType());
        }
    }

    public void setSelectedNode(NodeInfo selectedNode) {
        mSelectedNode = selectedNode;
    }
}
