package com.stardust.scriptdroid.external.floating_window.content;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.external.floating_window.HoverMenuService;
import com.stardust.scriptdroid.layout_inspector.LayoutInspector;
import com.stardust.util.MessageEvent;
import com.stardust.view.ViewBinder;
import com.stardust.view.ViewBinding;

import org.greenrobot.eventbus.EventBus;

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


    public MainMenuNavigatorContent(Context context) {
        mView = View.inflate(context, R.layout.floating_window_main_menu, null);
        ViewBinder.bind(this);
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

    @NonNull
    @Override
    public View getView() {
        return mView;
    }

    @Override
    public void onShown(@NonNull Navigator navigator) {

    }

    @Override
    public void onHidden() {

    }

    public View findViewById(int id) {
        return mView.findViewById(id);
    }

}
