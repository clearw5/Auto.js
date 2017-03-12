package com.stardust.scriptdroid.external.floating_window;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.stardust.hover.HoverMenuBuilder;
import com.stardust.hover.SimpleHoverMenuTransitionListener;
import com.stardust.hover.WindowHoverMenu;
import com.stardust.scriptdroid.external.floating_window.view.FloatingLayoutBoundsView;
import com.stardust.scriptdroid.external.floating_window.view.FloatingLayoutHierarchyView;
import com.stardust.scriptdroid.layout_inspector.LayoutInspector;
import com.stardust.scriptdroid.layout_inspector.NodeInfo;
import com.stardust.theme.ThemeColorManagerCompat;
import com.stardust.util.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;

import io.mattcarroll.hover.HoverMenu;
import io.mattcarroll.hover.defaulthovermenu.window.WindowViewController;

/**
 * Created by Stardust on 2017/3/11.
 */

public class HoverMenuService extends Service {

    public static final String MESSAGE_SHOW_AND_EXPAND_MENU = "MESSAGE_SHOW_AND_EXPAND_MENU";
    public static final String MESSAGE_SHOW_LAYOUT_HIERARCHY = "MESSAGE_SHOW_LAYOUT_HIERARCHY";
    public static final String MESSAGE_SHOW_LAYOUT_BOUNDS = "MESSAGE_SHOW_LAYOUT_BOUNDS";
    public static final String MESSAGE_COLLAPSE_MENU = "MESSAGE_COLLAPSE_MENU";
    public static final String MESSAGE_MENU_COLLAPSING = "MESSAGE_MENU_COLLAPSING";

    private static WeakReference<HoverMenuService> service;

    public static boolean isServiceRunning() {
        return service != null && service.get() != null;
    }

    public static void stopService() {
        if (isServiceRunning()) {
            service.get().stopSelf();
        }
    }


    private static final String TAG = "HoverMenuService";

    private static final String PREF_FILE = "hover_menu";
    private static final String PREF_HOVER_MENU_VISUAL_STATE = "hover_menu_visual_state";

    private boolean mIsRunning;
    private SharedPreferences mPrefs;

    private WindowViewController mWindowViewController;

    private FloatingLayoutHierarchyView mFloatingLayoutHierarchyView;
    private FloatingLayoutBoundsView mFloatingLayoutBoundsView;

    private WindowHoverMenu mWindowHoverMenu;
    private HoverMenu.OnExitListener mWindowHoverMenuMenuExitListener = new HoverMenu.OnExitListener() {
        @Override
        public void onExitByUserRequest() {
            savePreferredLocation();
            mWindowHoverMenu.hide();
            stopSelf();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        if (isServiceRunning()) {
            stopSelf();
        } else {
            service = new WeakReference<>(this);
        }
        EventBus.getDefault().register(this);
        mPrefs = getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        initViews();
    }

    private void initViews() {
        mFloatingLayoutHierarchyView = new FloatingLayoutHierarchyView(this);
        mFloatingLayoutBoundsView = new FloatingLayoutBoundsView(this);
        initWindowMenu();
        mWindowViewController.addView(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true, mFloatingLayoutHierarchyView);
        mWindowViewController.addView(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true, mFloatingLayoutBoundsView);
    }

    private void initWindowMenu() {
        mWindowHoverMenu = (WindowHoverMenu) new HoverMenuBuilder(this)
                .displayWithinWindow()
                .useAdapter(new HoverMenuAdapter(this))
                .restoreVisualState(loadPreferredLocation())
                .build();
        mWindowHoverMenu.getHoverMenuView().setContentBackgroundColor(ThemeColorManagerCompat.getColorPrimary());
        mWindowHoverMenu.addOnExitListener(mWindowHoverMenuMenuExitListener);
        mWindowViewController = mWindowHoverMenu.getWindowViewController();
        mWindowHoverMenu.setHoverMenuTransitionListener(new SimpleHoverMenuTransitionListener() {
            @Override
            public void onExpanding() {
                captureCurrentWindow();
            }

            @Override
            public void onCollapsing() {
                LayoutInspector.getInstance().clearCapture();
                EventBus.getDefault().post(new MessageEvent(MESSAGE_MENU_COLLAPSING));
            }
        });
    }

    private void captureCurrentWindow() {
        NodeInfo nodeInfo = LayoutInspector.getInstance().captureCurrentWindow();
        if (nodeInfo == null)
            return;
        mFloatingLayoutHierarchyView.setRootNode(nodeInfo);
        mFloatingLayoutBoundsView.setRootNode(nodeInfo);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mIsRunning) {
            mIsRunning = true;
            mWindowHoverMenu.show();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mWindowHoverMenu.hide();
        mIsRunning = false;
        if (isServiceRunning() && service.get() == this)
            service.clear();
    }


    private void savePreferredLocation() {
        String memento = mWindowHoverMenu.getVisualState();
        mPrefs.edit().putString(PREF_HOVER_MENU_VISUAL_STATE, memento).apply();
    }

    private String loadPreferredLocation() {
        return mPrefs.getString(PREF_HOVER_MENU_VISUAL_STATE, null);
    }

    @Subscribe
    public void onMessageEvent(MessageEvent event) {
        switch (event.message) {
            case MESSAGE_SHOW_AND_EXPAND_MENU:
                mWindowHoverMenu.show();
                break;
            case MESSAGE_SHOW_LAYOUT_HIERARCHY:
                mWindowHoverMenu.hide();
                showView(mFloatingLayoutHierarchyView);
                break;
            case MESSAGE_SHOW_LAYOUT_BOUNDS:
                mWindowHoverMenu.hide();
                showView(mFloatingLayoutBoundsView);
                break;
            case MESSAGE_COLLAPSE_MENU:
                mWindowHoverMenu.collapseMenu();
                break;
        }
    }

    private void showView(View view) {
        view.setVisibility(View.VISIBLE);
        mWindowViewController.makeTouchable(view);
    }

}
