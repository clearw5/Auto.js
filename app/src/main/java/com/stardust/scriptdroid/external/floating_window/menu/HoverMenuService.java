package com.stardust.scriptdroid.external.floating_window.menu;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.stardust.hover.HoverMenuBuilder;
import com.stardust.hover.SimpleHoverMenuTransitionListener;
import com.stardust.hover.WindowHoverMenu;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.autojs.AutoJs;
import com.stardust.scriptdroid.external.floating_window.FloatingWindowManger;
import com.stardust.scriptdroid.external.floating_window.menu.view.FloatingLayoutBoundsView;
import com.stardust.scriptdroid.external.floating_window.menu.view.FloatingLayoutHierarchyView;
import com.stardust.scriptdroid.layout_inspector.NodeInfo;
import com.stardust.scriptdroid.tool.AccessibilityServiceTool;
import com.stardust.theme.ThemeColorManagerCompat;
import com.stardust.util.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.mattcarroll.hover.HoverMenu;
import io.mattcarroll.hover.defaulthovermenu.window.WindowViewController;

/**
 * Created by Stardust on 2017/3/11.
 */

public class HoverMenuService extends Service {


    public static class ServiceStateChangedEvent {
        ServiceStateChangedEvent(boolean state) {
            this.state = state;
        }

        public boolean state;
    }

    public static final String MESSAGE_SHOW_AND_EXPAND_MENU = "MESSAGE_SHOW_AND_EXPAND_MENU";
    public static final String MESSAGE_SHOW_LAYOUT_HIERARCHY = "MESSAGE_SHOW_LAYOUT_HIERARCHY";
    public static final String MESSAGE_SHOW_LAYOUT_BOUNDS = "MESSAGE_SHOW_LAYOUT_BOUNDS";
    public static final String MESSAGE_COLLAPSE_MENU = "MESSAGE_COLLAPSE_MENU";
    public static final String MESSAGE_MENU_COLLAPSING = "MESSAGE_MENU_COLLAPSING";
    public static final String MESSAGE_MENU_EXPANDING = "MESSAGE_MENU_EXPANDING";
    public static final String MESSAGE_MENU_EXIT = "MESSAGE_MENU_EXIT";

    private static boolean sIsRunning;
    private static EventBus eventBus = new EventBus();

    public static void startService(Context context) {
        context.startService(new Intent(context, HoverMenuService.class));
        setIsRunning(true);
    }

    public static boolean isServiceRunning() {
        return sIsRunning;
    }

    private static void setIsRunning(boolean isRunning) {
        sIsRunning = isRunning;
        eventBus.post(new ServiceStateChangedEvent(sIsRunning));
    }

    public static void postEvent(MessageEvent event) {
        eventBus.post(event);
    }


    public static EventBus getEventBus() {
        return eventBus;
    }

    private static final String TAG = "HoverMenuService";

    private static final String PREF_FILE = "hover_menu";
    private static final String PREF_HOVER_MENU_VISUAL_STATE = "hover_menu_visual_state";

    private SharedPreferences mPrefs;

    private WindowViewController mWindowViewController;

    private FloatingLayoutHierarchyView mFloatingLayoutHierarchyView;
    private FloatingLayoutBoundsView mFloatingLayoutBoundsView;

    private WindowHoverMenu mWindowHoverMenu;
    private HoverMenu.OnExitListener mWindowHoverMenuMenuExitListener = new HoverMenu.OnExitListener() {
        @Override
        public void onExitByUserRequest() {
            eventBus.post(new MessageEvent(MESSAGE_MENU_EXIT));
            savePreferredLocation();
            mWindowHoverMenu.hide();
            stopSelf();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        eventBus.register(this);
        mPrefs = getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        tryInitViews();
    }

    private void tryInitViews() {
        try {
            initViews();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.text_no_floating_window_permission, Toast.LENGTH_SHORT).show();
            FloatingWindowManger.goToFloatingWindowPermissionSetting();
        }
    }

    private void initViews() {
        mFloatingLayoutHierarchyView = new FloatingLayoutHierarchyView(this);
        mFloatingLayoutBoundsView = new FloatingLayoutBoundsView(this);
        initWindowMenu();
        mWindowViewController.addView(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true, mFloatingLayoutHierarchyView);
        mWindowViewController.addView(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true, mFloatingLayoutBoundsView);
    }

    private void initWindowMenu() {
        mWindowHoverMenu = (WindowHoverMenu) new HoverMenuBuilder(new ContextThemeWrapper(this, R.style.AppTheme))
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
                eventBus.post(new MessageEvent(MESSAGE_MENU_EXPANDING));
                captureCurrentWindow();
            }

            @Override
            public void onCollapsing() {
                AutoJs.getInstance().getLayoutInspector().clearCapture();
                eventBus.post(new MessageEvent(MESSAGE_MENU_COLLAPSING));
            }
        });
    }

    private void captureCurrentWindow() {
        NodeInfo nodeInfo = AutoJs.getInstance().getLayoutInspector().captureCurrentWindow();
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
        if (mWindowHoverMenu == null)
            tryInitViews();
        if (mWindowHoverMenu != null)
            mWindowHoverMenu.show();
        AccessibilityServiceTool.enableAccessibilityServiceByRootIfNeeded();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mWindowHoverMenu != null)
            mWindowHoverMenu.hide();
        if (eventBus.isRegistered(this))
            eventBus.unregister(this);
        setIsRunning(false);
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
                showAndExpandMenu();
                break;
            case MESSAGE_SHOW_LAYOUT_HIERARCHY:
                showLayoutHierarchy();
                break;
            case MESSAGE_SHOW_LAYOUT_BOUNDS:
                showLayoutBounds();
                break;
            case MESSAGE_COLLAPSE_MENU:
                mWindowHoverMenu.collapseMenu();
                break;
        }
    }

    private void showLayoutBounds() {
        mWindowHoverMenu.getHoverMenuView().setVisibility(View.GONE);
        showView(mFloatingLayoutBoundsView);
    }

    public void showLayoutHierarchy() {
        mWindowHoverMenu.getHoverMenuView().setVisibility(View.GONE);
        showView(mFloatingLayoutHierarchyView);
    }

    public void showAndExpandMenu() {
        showView(mWindowHoverMenu.getHoverMenuView());
    }


    private void showView(View view) {
        view.setVisibility(View.VISIBLE);
        mWindowViewController.makeTouchable(view);
    }

}
