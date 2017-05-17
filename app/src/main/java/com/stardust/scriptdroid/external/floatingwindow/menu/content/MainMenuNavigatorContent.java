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
import com.stardust.util.ClipboardUtil;
import com.stardust.scriptdroid.ui.main.MainActivity;
import com.stardust.util.MessageEvent;
import com.stardust.view.ViewBinder;
import com.stardust.view.ViewBinding;

import org.greenrobot.eventbus.Subscribe;

import io.mattcarroll.hover.Navigator;
import io.mattcarroll.hover.NavigatorContent;

/**
 * Created by Stardust on 2017/3/12.
 */

public class MainMenuNavigatorContent implements NavigatorContent {


    private View mView;
    @ViewBinding.Id(R.id.current_package)
    private TextView mCurrentPackageTextView;
    @ViewBinding.Id(R.id.current_activity)
    private TextView mCurrentActivityTextView;
    private String mCurrentPackage, mCurrentActivity;
    private Context mContext;

    public MainMenuNavigatorContent(Context context) {
        mContext = context;
        mView = View.inflate(context, R.layout.floating_window_main_menu, null);
        ViewBinder.bind(this);
        HoverMenuService.getEventBus().register(this);
    }

    @ViewBinding.Click(R.id.layout_hierarchy)
    private void showLayoutHierarchy() {
        if (!ensureCapture()) {
            return;
        }
        HoverMenuService.postEvent(new MessageEvent(HoverMenuService.MESSAGE_SHOW_LAYOUT_HIERARCHY));
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

    @ViewBinding.Click(R.id.layout_bounds)
    private void showLayoutBounds() {
        if (!ensureCapture()) {
            return;
        }
        HoverMenuService.postEvent(new MessageEvent(HoverMenuService.MESSAGE_SHOW_LAYOUT_BOUNDS));
    }

    @ViewBinding.Click(R.id.stop_all_running_scripts)
    private void stopAllRunningScripts() {
        AutoJs.getInstance().getScriptEngineService().stopAllAndToast();
    }

    @ViewBinding.Click(R.id.open_launcher)
    private void openMainActivity() {
        App.getApp().startActivity(new Intent(App.getApp(), MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK));
        HoverMenuService.postEvent(new MessageEvent(HoverMenuService.MESSAGE_COLLAPSE_MENU));
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

    @ViewBinding.Click(R.id.current_activity)
    private void copyCurrentActivity() {
        ClipboardUtil.setClip(mContext, mCurrentActivity);
        Toast.makeText(mContext, R.string.text_copied, Toast.LENGTH_SHORT).show();
    }

    @ViewBinding.Click(R.id.current_package)
    private void copyCurrentPackage() {
        ClipboardUtil.setClip(mContext, mCurrentPackage);
        Toast.makeText(mContext, R.string.text_copied, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onHidden() {

    }

    @Subscribe
    public void onMessageEvent(MessageEvent event) {
        if (event.message.equals(HoverMenuService.MESSAGE_MENU_EXPANDING)) {
            syncCurrentInfo();
        } else if (event.message.equals(HoverMenuService.MESSAGE_MENU_EXIT)) {
            HoverMenuService.getEventBus().unregister(this);
        }
    }

    public View findViewById(int id) {
        return mView.findViewById(id);
    }

}
