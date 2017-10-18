package com.stardust.scriptdroid.ui.floating;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.floatingcircularactionmenu.CircularActionMenu;
import com.stardust.floatingcircularactionmenu.CircularActionMenuFloatingWindow;
import com.stardust.floatingcircularactionmenu.CircularActionMenuFloaty;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.accessibility.AccessibilityService;
import com.stardust.scriptdroid.autojs.AutoJs;
import com.stardust.scriptdroid.ui.floating.layoutinspector.LayoutBoundsFloatyWindow;
import com.stardust.scriptdroid.ui.floating.layoutinspector.LayoutHierarchyFloatyWindow;
import com.stardust.scriptdroid.ui.floating.layoutinspector.LayoutHierarchyView;
import com.stardust.view.accessibility.LayoutInspector;

import org.androidannotations.annotations.Click;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Stardust on 2017/10/18.
 */

public class CircularMenu {


    private static final int STATE_NORMAL = 0;
    private static final int STATE_RECORDING = 1;

    CircularActionMenuFloatingWindow mWindow;
    private int mState;
    private ImageView mActionViewIcon;
    private Context mContext;


    public CircularMenu(Context context) {
        mContext = context;
        initFloaty();
        setupListeners();
        FloatyService.addWindow(mWindow);
    }

    private void setupListeners() {
        mWindow.setOnActionViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mState == STATE_RECORDING) {
                    stopRecord();
                } else if (mWindow.isExpanded()) {
                    mWindow.collapse();
                } else {
                    AutoJs.getInstance().getLayoutInspector().captureCurrentWindow();
                    mWindow.expand();
                }
            }


        });
    }

    private void initFloaty() {
        mWindow = new CircularActionMenuFloatingWindow(new CircularActionMenuFloaty() {

            @Override
            public View inflateActionView(FloatyService service, CircularActionMenuFloatingWindow window) {
                View actionView = View.inflate(service, R.layout.circular_action_view, null);
                mActionViewIcon = (ImageView) actionView.findViewById(R.id.icon);
                return actionView;
            }

            @Override
            public CircularActionMenu inflateMenuItems(FloatyService service, CircularActionMenuFloatingWindow window) {
                CircularActionMenu menu = (CircularActionMenu) View.inflate(new ContextThemeWrapper(service, R.style.AppTheme), R.layout.circular_action_menu, null);
                ButterKnife.bind(CircularMenu.this, menu);
                return menu;
            }
        });

    }


    @OnClick(R.id.script_list)
    void showScriptList() {
        mWindow.collapse();
    }

    @OnClick(R.id.record)
    void startRecord() {
        mWindow.collapse();
        mState = STATE_RECORDING;
        mActionViewIcon.setImageResource(R.drawable.ic_ali_record);
        mActionViewIcon.setBackgroundResource(R.drawable.circle_red);
        mActionViewIcon.setPadding(28, 28, 28, 28);
    }

    private void stopRecord() {
        mWindow.collapse();
        mState = STATE_NORMAL;
        mActionViewIcon.setImageResource(R.drawable.autojs_logo);
        mActionViewIcon.setBackground(null);
        mActionViewIcon.setPadding(0, 0, 0, 0);
    }

    @OnClick(R.id.layout_bounds)
    void showLayoutBounds() {
        mWindow.collapse();
        if (!ensureCapture()) {
            return;
        }
        LayoutBoundsFloatyWindow window = new LayoutBoundsFloatyWindow(
                AutoJs.getInstance().getLayoutInspector().getCapture()
        );
        FloatyService.addWindow(window);
    }

    @OnClick(R.id.layout_hierarchy)
    void showLayoutHierarchy() {
        mWindow.collapse();
        if (!ensureCapture()) {
            return;
        }
        LayoutHierarchyFloatyWindow window = new LayoutHierarchyFloatyWindow(
                AutoJs.getInstance().getLayoutInspector().getCapture()
        );
        FloatyService.addWindow(window);
    }

    @OnClick(R.id.settings)
    void settings() {
        mWindow.collapse();

    }

    private boolean ensureCapture() {
        LayoutInspector inspector = AutoJs.getInstance().getLayoutInspector();
        if (inspector.isDumping()) {
            Toast.makeText(mContext, R.string.text_layout_inspector_is_dumping, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (AccessibilityService.getInstance() == null) {
            Toast.makeText(mContext, R.string.text_no_accessibility_permission_to_capture, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (inspector.getCapture() == null) {
            Toast.makeText(mContext, R.string.text_inspect_failed, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    public void close() {
        mWindow.close();
    }
}
