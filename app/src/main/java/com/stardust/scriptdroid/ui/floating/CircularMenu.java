package com.stardust.scriptdroid.ui.floating;

import android.content.Context;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.app.DialogUtils;
import com.stardust.app.OperationDialogBuilder;
import com.stardust.autojs.core.record.Recorder;
import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.floatingcircularactionmenu.CircularActionMenu;
import com.stardust.floatingcircularactionmenu.CircularActionMenuFloatingWindow;
import com.stardust.floatingcircularactionmenu.CircularActionMenuFloaty;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.accessibility.AccessibilityService;
import com.stardust.scriptdroid.autojs.AutoJs;
import com.stardust.scriptdroid.autojs.record.GlobalRecorder;
import com.stardust.scriptdroid.script.ScriptFile;
import com.stardust.scriptdroid.tool.AccessibilityServiceTool;
import com.stardust.scriptdroid.ui.floating.layoutinspector.LayoutBoundsFloatyWindow;
import com.stardust.scriptdroid.ui.floating.layoutinspector.LayoutHierarchyFloatyWindow;
import com.stardust.scriptdroid.ui.main.scripts.ScriptListView;
import com.stardust.theme.dialog.ThemeColorMaterialDialogBuilder;
import com.stardust.util.ClipboardUtil;
import com.stardust.view.accessibility.LayoutInspector;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Stardust on 2017/10/18.
 */

public class CircularMenu implements Recorder.OnStateChangedListener {

    public static class StateChangeEvent {
        private int currentState;
        private int previousState;

        public StateChangeEvent(int currentState, int previousState) {
            this.currentState = currentState;
            this.previousState = previousState;
        }

        public int getCurrentState() {
            return currentState;
        }

        public int getPreviousState() {
            return previousState;
        }
    }

    public static final int STATE_CLOSED = -1;
    public static final int STATE_NORMAL = 0;
    public static final int STATE_RECORDING = 1;

    CircularActionMenuFloatingWindow mWindow;
    private int mState;
    private ImageView mActionViewIcon;
    private Context mContext;
    private GlobalRecorder mRecorder;
    private MaterialDialog mSettingsDialog;
    private String mRunningPackage, mRunningActivity;

    public CircularMenu(Context context) {
        mContext = new ContextThemeWrapper(context, R.style.AppTheme);
        initFloaty();
        setupListeners();
        mRecorder = GlobalRecorder.getSingleton(context);
        mRecorder.addOnStateChangedListener(this);
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
        mWindow.setKeepToSideHiddenWidthRadio(0.2f);
        FloatyService.addWindow(mWindow);
    }


    @Optional
    @OnClick(R.id.script_list)
    void showScriptList() {
        mWindow.collapse();
        ScriptListView listView = new ScriptListView(mContext);
        listView.setDirectorySpanSize(2);
        final MaterialDialog dialog = new ThemeColorMaterialDialogBuilder(mContext)
                .title(R.string.text_run_script)
                .customView(listView, false)
                .positiveText(R.string.cancel)
                .build();
        listView.setOnItemOperatedListener(new ScriptListView.OnItemOperatedListener() {
            @Override
            public void OnItemOperated(ScriptFile file) {
                dialog.dismiss();
            }
        });
        DialogUtils.showDialog(dialog);
    }

    @Optional
    @OnClick(R.id.record)
    void startRecord() {
        mWindow.collapse();
        mRecorder.start();
    }

    private void setState(int state) {
        int previousState = mState;
        mState = state;
        mActionViewIcon.setImageResource(mState == STATE_RECORDING ? R.drawable.ic_ali_record :
                R.drawable.autojs_logo);
        mActionViewIcon.setBackgroundResource(mState == STATE_RECORDING ? R.drawable.circle_red :
                0);
        if (state == STATE_RECORDING) {
            mActionViewIcon.setPadding(28, 28, 28, 28);
        } else {
            mActionViewIcon.setPadding(0, 0, 0, 0);
        }
        EventBus.getDefault().post(new StateChangeEvent(mState, previousState));

    }

    private void stopRecord() {
        mRecorder.stop();
    }

    @Optional
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

    @Optional
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


    @Optional
    @OnClick(R.id.settings)
    void settings() {
        mWindow.collapse();
        mRunningPackage = AutoJs.getInstance().getInfoProvider().getLatestPackage();
        mRunningActivity = AutoJs.getInstance().getInfoProvider().getLatestActivity();
        mSettingsDialog = new OperationDialogBuilder(mContext)
                .item(R.id.accessibility_service, R.drawable.ic_service_green, R.string.text_accessibility_settings)
                .item(R.id.package_name, R.drawable.ic_ali_app,
                        mContext.getString(R.string.text_current_package) + mRunningPackage)
                .item(R.id.class_name, R.drawable.ic_ali_android,
                        mContext.getString(R.string.text_current_activity) + mRunningActivity)
                .item(R.id.exit, R.drawable.ic_close_white_48dp, R.string.text_exit_floating_window)
                .bindItemClick(this)
                .title(R.string.text_more)
                .build();
        DialogUtils.showDialog(mSettingsDialog);
    }


    @Optional
    @OnClick(R.id.accessibility_service)
    void enableAccessibilityService() {
        dismissSettingsDialog();
        AccessibilityServiceTool.enableAccessibilityService();
    }

    private void dismissSettingsDialog() {
        if (mSettingsDialog == null)
            return;
        mSettingsDialog.dismiss();
        mSettingsDialog = null;
    }

    @Optional
    @OnClick(R.id.package_name)
    void copyPackageName() {
        dismissSettingsDialog();
        if (TextUtils.isEmpty(mRunningPackage))
            return;
        ClipboardUtil.setClip(mContext, mRunningPackage);
        Toast.makeText(mContext, R.string.text_already_copy_to_clip, Toast.LENGTH_SHORT).show();
    }

    @Optional
    @OnClick(R.id.package_name)
    void copyActivityName() {
        dismissSettingsDialog();
        if (TextUtils.isEmpty(mRunningActivity))
            return;
        ClipboardUtil.setClip(mContext, mRunningActivity);
        Toast.makeText(mContext, R.string.text_already_copy_to_clip, Toast.LENGTH_SHORT).show();
    }

    @Optional
    @OnClick(R.id.exit)
    public void close() {
        dismissSettingsDialog();
        mWindow.close();
        mRecorder.removeOnStateChangedListener(this);
        EventBus.getDefault().post(new StateChangeEvent(STATE_CLOSED, mState));
        mState = STATE_CLOSED;
    }


    @Override
    public void onStart() {
        setState(STATE_RECORDING);
    }

    @Override
    public void onStop() {
        setState(STATE_NORMAL);
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }
}
