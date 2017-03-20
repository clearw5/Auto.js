package com.stardust.scriptdroid.external.floating_window.content;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.accessibility.AccessibilityInfoProvider;
import com.stardust.scriptdroid.droid.Droid;
import com.stardust.scriptdroid.external.floating_window.HoverMenuService;
import com.stardust.scriptdroid.layout_inspector.LayoutInspector;
import com.stardust.scriptdroid.tool.ClipboardTool;
import com.stardust.scriptdroid.ui.main.MainActivity;
import com.stardust.util.MessageEvent;
import com.stardust.view.ViewBinder;
import com.stardust.view.ViewBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.mattcarroll.hover.Navigator;
import io.mattcarroll.hover.NavigatorContent;
import io.mattcarroll.hover.defaulthovermenu.menus.Menu;
import io.mattcarroll.hover.defaulthovermenu.menus.MenuAction;
import io.mattcarroll.hover.defaulthovermenu.menus.MenuItem;
import io.mattcarroll.hover.defaulthovermenu.menus.MenuListNavigatorContent;

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
        EventBus.getDefault().register(this);
    }

    @ViewBinding.Click(R.id.layout_hierarchy)
    private void showLayoutHierarchy() {
        if (LayoutInspector.getInstance().getCapture() == null) {
            Toast.makeText(mView.getContext(), R.string.text_no_accessibility_permission_to_capture, Toast.LENGTH_SHORT).show();
        } else {
            EventBus.getDefault().post(new MessageEvent(HoverMenuService.MESSAGE_SHOW_LAYOUT_HIERARCHY));
        }
    }

    @ViewBinding.Click(R.id.layout_bounds)
    private void showLayoutBounds() {
        if (LayoutInspector.getInstance().getCapture() == null) {
            Toast.makeText(mView.getContext(), R.string.text_no_accessibility_permission_to_capture, Toast.LENGTH_SHORT).show();
        } else {
            EventBus.getDefault().post(new MessageEvent(HoverMenuService.MESSAGE_SHOW_LAYOUT_BOUNDS));
        }
    }

    @ViewBinding.Click(R.id.stop_all_running_scripts)
    private void stopAllRunningScripts() {
        Droid.getInstance().stopAllAndToast();
    }

    @ViewBinding.Click(R.id.open_launcher)
    private void openMainActivity() {
        App.getApp().startActivity(new Intent(App.getApp(), MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK));
        EventBus.getDefault().post(new MessageEvent(HoverMenuService.MESSAGE_COLLAPSE_MENU));
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
        mCurrentPackage = AccessibilityInfoProvider.getInstance().getLatestPackage();
        mCurrentActivity = AccessibilityInfoProvider.getInstance().getLatestActivity();
        mCurrentActivityTextView.setText(mContext.getString(R.string.text_current_activity) + mCurrentActivity);
        mCurrentPackageTextView.setText(mContext.getString(R.string.text_current_package) + mCurrentPackage);
    }

    @ViewBinding.Click(R.id.current_activity)
    private void copyCurrentActivity() {
        ClipboardTool.setClip(mCurrentActivity);
        Toast.makeText(mContext, R.string.text_copied, Toast.LENGTH_SHORT).show();
    }

    @ViewBinding.Click(R.id.current_package)
    private void copyCurrentPackage() {
        ClipboardTool.setClip(mCurrentPackage);
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
            EventBus.getDefault().unregister(this);
        }
    }

    public View findViewById(int id) {
        return mView.findViewById(id);
    }

}
