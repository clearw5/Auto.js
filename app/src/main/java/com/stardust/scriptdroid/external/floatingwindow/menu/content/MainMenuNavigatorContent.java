package com.stardust.scriptdroid.external.floatingwindow.menu.content;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.autojs.AutoJs;
import com.stardust.scriptdroid.external.floatingwindow.menu.HoverMenuService;
import com.stardust.scriptdroid.external.floatingwindow.menu.layout_inspector.LayoutInspector;
import com.stardust.scriptdroid.ui.main.MainActivity_;
import com.stardust.util.ClipboardUtil;
import com.stardust.util.MessageEvent;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.mattcarroll.hover.Navigator;
import io.mattcarroll.hover.NavigatorContent;

/**
 * Created by Stardust on 2017/3/12.
 */

public class MainMenuNavigatorContent implements NavigatorContent {

    private View mView;
    @BindView(R.id.current_package)
    TextView mCurrentPackageTextView;
    @BindView(R.id.current_activity)
    TextView mCurrentActivityTextView;
    private String mCurrentPackage, mCurrentActivity;
    private Context mContext;

    public MainMenuNavigatorContent(Context context) {
        mContext = context;
        mView = View.inflate(context, R.layout.floating_window_main_menu, null);
        ButterKnife.bind(this, mView);
        HoverMenuService.getEventBus().register(this);
    }

    @OnClick(R.id.layout_hierarchy)
    void showLayoutHierarchy() {
        if (!ensureCapture()) {
            return;
        }
        HoverMenuService.postIntent(new Intent(HoverMenuService.ACTION_SHOW_LAYOUT_HIERARCHY));
    }

    private boolean ensureCapture() {
        LayoutInspector inspector = AutoJs.getInstance().getLayoutInspector();
        if (inspector.isDumping()) {
            Toast.makeText(mView.getContext(), R.string.text_layout_inspector_is_dumping, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (inspector.getCapture() == null) {
            Toast.makeText(mView.getContext(), R.string.text_no_accessibility_permission_to_capture, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @OnClick(R.id.layout_bounds)
    void showLayoutBounds() {
        if (!ensureCapture()) {
            return;
        }
        HoverMenuService.postIntent(new Intent(HoverMenuService.ACTION_SHOW_LAYOUT_BOUNDS));
    }

    @OnClick(R.id.stop_all_running_scripts)
    void stopAllRunningScripts() {
        AutoJs.getInstance().getScriptEngineService().stopAllAndToast();
    }

    @OnClick(R.id.open_launcher)
    void openMainActivity() {
        App.getApp().startActivity(new Intent(App.getApp(), MainActivity_.class)
                .addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK));
        HoverMenuService.postIntent(new Intent(HoverMenuService.ACTION_COLLAPSE_MENU));
    }

    @NonNull
    @Override
    public View getView() {
        return mView;
    }

    @Override
    public void onShown(@NonNull Navigator navigator) {
    }

    @SuppressLint("SetTextI18n")
    private void syncCurrentInfo() {
        mCurrentPackage = AutoJs.getInstance().getInfoProvider().getLatestPackage();
        mCurrentActivity = AutoJs.getInstance().getInfoProvider().getLatestActivity();
        mCurrentActivityTextView.setText(mContext.getString(R.string.text_current_activity) + mCurrentActivity);
        mCurrentPackageTextView.setText(mContext.getString(R.string.text_current_package) + mCurrentPackage);
    }

    @OnClick(R.id.current_activity)
    void copyCurrentActivity() {
        ClipboardUtil.setClip(mContext, mCurrentActivity);
        Toast.makeText(mContext, R.string.text_copied, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.current_package)
    void copyCurrentPackage() {
        ClipboardUtil.setClip(mContext, mCurrentPackage);
        Toast.makeText(mContext, R.string.text_copied, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onHidden() {

    }

    @Subscribe
    public void onMessageEvent(MessageEvent event) {
        if (event.message.equals(HoverMenuService.ACTION_MENU_EXPANDING)) {
            syncCurrentInfo();
        } else if (event.message.equals(HoverMenuService.ACTION_MENU_EXIT)) {
            HoverMenuService.getEventBus().unregister(this);
        }
    }

}
