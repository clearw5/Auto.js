package com.stardust.scriptdroid.external.floatingwindow.menu;


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
import com.stardust.scriptdroid.external.floatingwindow.FloatingWindowManger;
import com.stardust.scriptdroid.external.floatingwindow.menu.layout_inspector.NodeInfo;
import com.stardust.scriptdroid.external.floatingwindow.menu.view.FloatingLayoutBoundsView;
import com.stardust.scriptdroid.external.floatingwindow.menu.view.FloatingLayoutHierarchyView;
import com.stardust.scriptdroid.tool.AccessibilityServiceTool;
import com.stardust.theme.ThemeColorManagerCompat;
import com.stardust.util.MessageEvent;
import com.stardust.util.MessageIntent;

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

    public static final String ACTION_SHOW_AND_EXPAND_MENU = "ACTION_SHOW_AND_EXPAND_MENU";
    public static final String ACTION_SHOW_LAYOUT_HIERARCHY = "ACTION_SHOW_LAYOUT_HIERARCHY";
    public static final String ACTION_SHOW_LAYOUT_BOUNDS = "ACTION_SHOW_LAYOUT_BOUNDS";
    public static final String ACTION_COLLAPSE_MENU = "ACTION_COLLAPSE_MENU";
    public static final String ACTION_MENU_COLLAPSING = "ACTION_MENU_COLLAPSING";
    public static final String ACTION_MENU_EXPANDING = "ACTION_MENU_EXPANDING";
    public static final String ACTION_MENU_EXIT = "ACTION_MENU_EXIT";
    public static final String ACTION_SHOW_NODE_LAYOUT_HIERARCHY = "ACTION_SHOW_NODE_LAYOUT_HIERARCHY";
    public static final String ACTION_SHOW_NODE_LAYOUT_BOUNDS = "ACTION_SHOW_NODE_LAYOUT_BOUNDS";

    public static final String EXTRA_NODE_INFO = "EXTRA_NODE_INFO";


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

    public static void postIntent(Intent intent) {
        eventBus.post(new MessageIntent(intent));
    }

    public static void postMessageIntent(MessageIntent intent) {
        eventBus.post(intent);
    }


    public static EventBus getEventBus() {
        return eventBus;
    }

    private static final String TAG = "HoverMenuService";

    private static final String PREF_FILE = "hover_menu";
    private static final String PREF_HOVER_MENU_VISUAL_STATE = "hover_menu_visual_state";

    private SharedPreferences mPrefs;

    private WindowViewController mWindowViewController;

    private ContextThemeWrapper mThemeWrapper;
    private FloatingLayoutHierarchyView mFloatingLayoutHierarchyView;
    private FloatingLayoutBoundsView mFloatingLayoutBoundsView;

    private WindowHoverMenu mWindowHoverMenu;
    private HoverMenu.OnExitListener mWindowHoverMenuMenuExitListener = new HoverMenu.OnExitListener() {
        @Override
        public void onExitByUserRequest() {
            eventBus.post(new MessageEvent(ACTION_MENU_EXIT));
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
        mThemeWrapper = new ContextThemeWrapper(this, R.style.AppTheme);
        mFloatingLayoutHierarchyView = new FloatingLayoutHierarchyView(mThemeWrapper);
        mFloatingLayoutBoundsView = new FloatingLayoutBoundsView(mThemeWrapper);
        initWindowMenu();
        mWindowViewController.addView(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true, mFloatingLayoutHierarchyView);
        mWindowViewController.addView(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true, mFloatingLayoutBoundsView);
    }

    private void initWindowMenu() {
        mWindowHoverMenu = (WindowHoverMenu) new HoverMenuBuilder(mThemeWrapper)
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
                eventBus.post(new MessageEvent(ACTION_MENU_EXPANDING));
                captureCurrentWindow();
            }

            @Override
            public void onCollapsing() {
                AutoJs.getInstance().getLayoutInspector().clearCapture();
                eventBus.post(new MessageEvent(ACTION_MENU_COLLAPSING));
            }
        });
    }

    private void captureCurrentWindow() {
        AutoJs.getInstance().getLayoutInspector().captureCurrentWindow();
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
        mWindowViewController.removeView(mFloatingLayoutBoundsView);
        mWindowViewController.removeView(mFloatingLayoutHierarchyView);
    }


    private void savePreferredLocation() {
        String memento = mWindowHoverMenu.getVisualState();
        mPrefs.edit().putString(PREF_HOVER_MENU_VISUAL_STATE, memento).apply();
    }

    private String loadPreferredLocation() {
        return mPrefs.getString(PREF_HOVER_MENU_VISUAL_STATE, null);
    }

    @Subscribe
    public void handleMessageIntent(MessageIntent intent) {
        switch (intent.getAction()) {
            case ACTION_SHOW_AND_EXPAND_MENU:
                showAndExpandMenu();
                break;
            case ACTION_SHOW_LAYOUT_HIERARCHY:
                showLayoutHierarchy();
                break;
            case ACTION_SHOW_LAYOUT_BOUNDS:
                showLayoutBounds();
                break;
            case ACTION_COLLAPSE_MENU:
                mWindowHoverMenu.collapseMenu();
                break;
            case ACTION_SHOW_NODE_LAYOUT_BOUNDS:
                mFloatingLayoutHierarchyView.setVisibility(View.GONE);
                showLayoutBounds();
                mFloatingLayoutBoundsView.setSelectedNode((NodeInfo) intent.getObjectExtra(EXTRA_NODE_INFO));
                break;
            case ACTION_SHOW_NODE_LAYOUT_HIERARCHY:
                mFloatingLayoutBoundsView.setVisibility(View.GONE);
                showLayoutHierarchy();
                mFloatingLayoutHierarchyView.setSelectedNode((NodeInfo) intent.getObjectExtra(EXTRA_NODE_INFO));
                break;
        }
    }

    private void showLayoutBounds() {
        mFloatingLayoutBoundsView.setRootNode(AutoJs.getInstance().getLayoutInspector().getCapture());
        mWindowHoverMenu.getHoverMenuView().setVisibility(View.GONE);
        showView(mFloatingLayoutBoundsView);
    }

    public void showLayoutHierarchy() {
        mFloatingLayoutHierarchyView.setRootNode(AutoJs.getInstance().getLayoutInspector().getCapture());
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
