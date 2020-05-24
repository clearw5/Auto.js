package org.autojs.autojs.ui.floating;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.makeramen.roundedimageview.RoundedImageView;
import com.stardust.app.DialogUtils;
import com.stardust.app.OperationDialogBuilder;
import com.stardust.autojs.core.record.Recorder;
import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.enhancedfloaty.FloatyWindow;

import org.autojs.autojs.Pref;
import org.autojs.autojs.R;
import org.autojs.autojs.autojs.AutoJs;
import org.autojs.autojs.autojs.record.GlobalActionRecorder;
import org.autojs.autojs.model.explorer.ExplorerDirPage;
import org.autojs.autojs.model.explorer.Explorers;
import org.autojs.autojs.model.script.Scripts;
import org.autojs.autojs.tool.AccessibilityServiceTool;
import org.autojs.autojs.tool.RootTool;
import org.autojs.autojs.ui.common.NotAskAgainDialog;
import org.autojs.autojs.ui.floating.layoutinspector.LayoutBoundsFloatyWindow;
import org.autojs.autojs.ui.floating.layoutinspector.LayoutHierarchyFloatyWindow;
import org.autojs.autojs.ui.main.MainActivity_;
import org.autojs.autojs.ui.explorer.ExplorerView;
import org.autojs.autojs.theme.dialog.ThemeColorMaterialDialogBuilder;

import com.stardust.util.ClipboardUtil;
import com.stardust.util.Func1;
import com.stardust.view.accessibility.AccessibilityService;
import com.stardust.view.accessibility.LayoutInspector;
import com.stardust.view.accessibility.NodeInfo;

import org.greenrobot.eventbus.EventBus;
import org.jdeferred.Deferred;
import org.jdeferred.impl.DeferredObject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Stardust on 2017/10/18.
 */

public class CircularMenu implements Recorder.OnStateChangedListener, LayoutInspector.CaptureAvailableListener {


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

    private static final int IC_ACTION_VIEW = R.drawable.ic_android_eat_js;

    CircularMenuWindow mWindow;
    private int mState;
    private RoundedImageView mActionViewIcon;
    private Context mContext;
    private GlobalActionRecorder mRecorder;
    private MaterialDialog mSettingsDialog;
    private MaterialDialog mLayoutInspectDialog;
    private String mRunningPackage, mRunningActivity;
    private Deferred<NodeInfo, Void, Void> mCaptureDeferred;

    public CircularMenu(Context context) {
        mContext = new ContextThemeWrapper(context, R.style.AppTheme);
        initFloaty();
        setupListeners();
        mRecorder = GlobalActionRecorder.getSingleton(context);
        mRecorder.addOnStateChangedListener(this);
        AutoJs.getInstance().getLayoutInspector().addCaptureAvailableListener(this);
    }

    private void setupListeners() {
        mWindow.setOnActionViewClickListener(v -> {
            if (mState == STATE_RECORDING) {
                stopRecord();
            } else if (mWindow.isExpanded()) {
                mWindow.collapse();
            } else {
                mCaptureDeferred = new DeferredObject<>();
                AutoJs.getInstance().getLayoutInspector().captureCurrentWindow();
                mWindow.expand();
            }
        });
    }

    private void initFloaty() {
        mWindow = new CircularMenuWindow(mContext, new CircularMenuFloaty() {

            @Override
            public View inflateActionView(FloatyService service, CircularMenuWindow window) {
                View actionView = View.inflate(service, R.layout.circular_action_view, null);
                mActionViewIcon = actionView.findViewById(R.id.icon);
                return actionView;
            }

            @Override
            public CircularActionMenu inflateMenuItems(FloatyService service, CircularMenuWindow window) {
                CircularActionMenu menu = (CircularActionMenu) View.inflate(new ContextThemeWrapper(service, R.style.AppTheme), R.layout.circular_action_menu, null);
                ButterKnife.bind(CircularMenu.this, menu);
                return menu;
            }
        });
        mWindow.setKeepToSideHiddenWidthRadio(0.25f);
        FloatyService.addWindow(mWindow);
    }


    @Optional
    @OnClick(R.id.script_list)
    void showScriptList() {
        mWindow.collapse();
        ExplorerView explorerView = new ExplorerView(mContext);
        explorerView.setExplorer(Explorers.workspace(), ExplorerDirPage.createRoot(Pref.getScriptDirPath()));
        explorerView.setDirectorySpanSize(2);
        final MaterialDialog dialog = new ThemeColorMaterialDialogBuilder(mContext)
                .title(R.string.text_run_script)
                .customView(explorerView, false)
                .positiveText(R.string.cancel)
                .build();
        explorerView.setOnItemOperatedListener(file -> dialog.dismiss());
        explorerView.setOnItemClickListener((view, item) -> Scripts.INSTANCE.run(item.toScriptFile()));
        DialogUtils.showDialog(dialog);

    }

    @Optional
    @OnClick(R.id.record)
    void startRecord() {
        mWindow.collapse();
        if (!RootTool.isRootAvailable()) {
            DialogUtils.showDialog(new NotAskAgainDialog.Builder(mContext, "CircularMenu.root")
                    .title(R.string.text_device_not_rooted)
                    .content(R.string.prompt_device_not_rooted)
                    .neutralText(R.string.text_device_rooted)
                    .positiveText(R.string.ok)
                    .onNeutral(((dialog, which) -> mRecorder.start()))
                    .build());
        } else {
            mRecorder.start();

        }
    }

    private void setState(int state) {
        int previousState = mState;
        mState = state;
        mActionViewIcon.setImageResource(mState == STATE_RECORDING ? R.drawable.ic_ali_record :
                IC_ACTION_VIEW);
        //  mActionViewIcon.setBackgroundColor(mState == STATE_RECORDING ? mContext.getResources().getColor(R.color.color_red) :
        //        Color.WHITE);
        mActionViewIcon.setBackgroundResource(mState == STATE_RECORDING ? R.drawable.circle_red :
                R.drawable.circle_white);
        int padding = (int) mContext.getResources().getDimension(mState == STATE_RECORDING ?
                R.dimen.padding_circular_menu_recording : R.dimen.padding_circular_menu_normal);
        mActionViewIcon.setPadding(padding, padding, padding, padding);
        EventBus.getDefault().post(new StateChangeEvent(mState, previousState));

    }

    private void stopRecord() {
        mRecorder.stop();
    }

    @Optional
    @OnClick(R.id.layout_inspect)
    void inspectLayout() {
        mWindow.collapse();
        mLayoutInspectDialog = new OperationDialogBuilder(mContext)
                .item(R.id.layout_bounds, R.drawable.ic_circular_menu_bounds, R.string.text_inspect_layout_bounds)
                .item(R.id.layout_hierarchy, R.drawable.ic_circular_menu_hierarchy,
                        R.string.text_inspect_layout_hierarchy)
                .bindItemClick(this)
                .title(R.string.text_inspect_layout)
                .build();
        DialogUtils.showDialog(mLayoutInspectDialog);
    }

    @Optional
    @OnClick(R.id.layout_bounds)
    void showLayoutBounds() {
        inspectLayout(LayoutBoundsFloatyWindow::new);
    }


    @Optional
    @OnClick(R.id.layout_hierarchy)
    void showLayoutHierarchy() {
        inspectLayout(LayoutHierarchyFloatyWindow::new);
    }

    private void inspectLayout(Func1<NodeInfo, FloatyWindow> windowCreator) {
        if (mLayoutInspectDialog != null) {
            mLayoutInspectDialog.dismiss();
            mLayoutInspectDialog = null;
        }
        if (AccessibilityService.Companion.getInstance() == null) {
            Toast.makeText(mContext, R.string.text_no_accessibility_permission_to_capture, Toast.LENGTH_SHORT).show();
            AccessibilityServiceTool.goToAccessibilitySetting();
            return;
        }
        MaterialDialog progress = DialogUtils.showDialog(new ThemeColorMaterialDialogBuilder(mContext)
                .content(R.string.text_layout_inspector_is_dumping)
                .canceledOnTouchOutside(false)
                .progress(true, 0)
                .build());
        mCaptureDeferred.promise()
                .then(capture -> {
                    mActionViewIcon.post(() -> {
                                if (!progress.isCancelled()) {
                                    progress.dismiss();
                                    FloatyService.addWindow(windowCreator.call(capture));
                                }
                            }
                    );
                }, err -> mActionViewIcon.post(progress::dismiss));
    }


    @Optional
    @OnClick(R.id.stop_all_scripts)
    void stopAllScripts() {
        mWindow.collapse();
        AutoJs.getInstance().getScriptEngineService().stopAllAndToast();
    }


    @Override
    public void onCaptureAvailable(NodeInfo capture) {
        if (mCaptureDeferred != null && mCaptureDeferred.isPending())
            mCaptureDeferred.resolve(capture);
    }


    @Optional
    @OnClick(R.id.settings)
    void settings() {
        mWindow.collapse();
        mRunningPackage = AutoJs.getInstance().getInfoProvider().getLatestPackageByUsageStatsIfGranted();
        mRunningActivity = AutoJs.getInstance().getInfoProvider().getLatestActivity();
        mSettingsDialog = new OperationDialogBuilder(mContext)
                .item(R.id.accessibility_service, R.drawable.ic_service_green, R.string.text_accessibility_settings)
                .item(R.id.package_name, R.drawable.ic_ali_app,
                        mContext.getString(R.string.text_current_package) + mRunningPackage)
                .item(R.id.class_name, R.drawable.ic_ali_android,
                        mContext.getString(R.string.text_current_activity) + mRunningActivity)
                .item(R.id.open_launcher, R.drawable.ic_android_eat_js, R.string.text_open_main_activity)
                .item(R.id.pointer_location, R.drawable.ic_zoom_out_map_white_24dp, R.string.text_pointer_location)
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
    @OnClick(R.id.class_name)
    void copyActivityName() {
        dismissSettingsDialog();
        if (TextUtils.isEmpty(mRunningActivity))
            return;
        ClipboardUtil.setClip(mContext, mRunningActivity);
        Toast.makeText(mContext, R.string.text_already_copy_to_clip, Toast.LENGTH_SHORT).show();
    }

    @Optional
    @OnClick(R.id.open_launcher)
    void openLauncher() {
        dismissSettingsDialog();
        mContext.startActivity(new Intent(mContext, MainActivity_.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Optional
    @OnClick(R.id.pointer_location)
    void togglePointerLocation() {
        dismissSettingsDialog();
        RootTool.togglePointerLocation();
    }

    @Optional
    @OnClick(R.id.exit)
    public void close() {
        dismissSettingsDialog();
        try {
            mWindow.close();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            EventBus.getDefault().post(new StateChangeEvent(STATE_CLOSED, mState));
            mState = STATE_CLOSED;
        }
        mRecorder.removeOnStateChangedListener(this);
        AutoJs.getInstance().getLayoutInspector().removeCaptureAvailableListener(this);
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
