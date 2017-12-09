package com.stardust.scriptdroid.ui.main.drawer;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.stardust.notification.NotificationListenerService;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.Pref;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.network.GlideApp;
import com.stardust.scriptdroid.pluginclient.DevPluginClient;
import com.stardust.scriptdroid.ui.floating.CircularMenu;
import com.stardust.scriptdroid.ui.floating.FloatyWindowManger;
import com.stardust.scriptdroid.network.NodeBB;
import com.stardust.scriptdroid.network.VersionService;
import com.stardust.scriptdroid.network.api.UserApi;
import com.stardust.scriptdroid.network.entity.user.User;
import com.stardust.scriptdroid.network.entity.VersionInfo;
import com.stardust.scriptdroid.tool.SimpleObserver;
import com.stardust.scriptdroid.ui.user.LoginActivity_;
import com.stardust.scriptdroid.ui.settings.SettingsActivity;
import com.stardust.scriptdroid.ui.update.UpdateInfoDialogBuilder;
import com.stardust.scriptdroid.ui.user.WebActivity;
import com.stardust.scriptdroid.ui.user.WebActivity_;
import com.stardust.scriptdroid.ui.widget.AvatarView;
import com.stardust.theme.ThemeColorManager;
import com.stardust.theme.ThemeColorManagerCompat;
import com.stardust.view.accessibility.AccessibilityService;
import com.stardust.scriptdroid.pluginclient.DevPluginService;
import com.stardust.scriptdroid.tool.AccessibilityServiceTool;
import com.stardust.scriptdroid.tool.WifiTool;
import com.stardust.util.IntentUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Stardust on 2017/1/30.
 * TODO 侧拉菜单用RecyclerView
 */
@EFragment(R.layout.fragment_drawer)
public class DrawerFragment extends android.support.v4.app.Fragment {

    private static final String URL_SUBLIME_PLUGIN_HELP = "https://github.com/hyb1996/AutoJs-Sublime-Plugin/blob/master/Readme.md";

    @ViewById(R.id.debug)
    DrawerMenuItem mConnectionItem;

    @ViewById(R.id.accessibility_service)
    DrawerMenuItem mAccessibilityServiceItem;

    @ViewById(R.id.notification_service)
    DrawerMenuItem mNotificationPermissionItem;

    @ViewById(R.id.floating_window)
    DrawerMenuItem mFloatingWindowItem;

    @ViewById(R.id.check_for_updates)
    DrawerMenuItem mCheckForUpdatesItem;

    @ViewById(R.id.header)
    View mHeaderView;

    @ViewById(R.id.username)
    TextView mUserName;

    @ViewById(R.id.avatar)
    AvatarView mAvatar;

    @ViewById(R.id.shadow)
    View mShadow;

    @ViewById(R.id.default_cover)
    View mDefaultCover;

    private Disposable mConnectionStateDisposable;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConnectionStateDisposable = DevPluginService.getInstance().getConnection()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(state -> {
                    if (mConnectionItem != null) {
                        mConnectionItem.getSwitchCompat().setChecked(state.getState() == DevPluginClient.State.CONNECTED, false);
                        mConnectionItem.setProgress(state.getState() == DevPluginClient.State.CONNECTING);
                    }
                    if (state.getException() != null) {
                        showMessage(state.getException().getMessage());
                    }
                });
        EventBus.getDefault().register(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        syncSwitchState();
        syncUserInfo();
    }

    @AfterViews
    void setUpViews() {
        ThemeColorManager.addViewBackground(mHeaderView);
        if (Pref.isFloatingMenuShown()) {
            mFloatingWindowItem.getSwitchCompat().setChecked(true);
        }
        mConnectionItem.getSwitchCompat().setChecked(DevPluginService.getInstance().isConnected(), false);
    }

    private void syncUserInfo() {
        NodeBB.getInstance().getRetrofit()
                .create(UserApi.class)
                .me()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setUpUserInfo, error -> {
                    error.printStackTrace();
                    setUpUserInfo(null);
                });
    }

    private void setUpUserInfo(@Nullable User user) {
        if (user == null) {
            mUserName.setText(R.string.not_login);
            mAvatar.setIcon(R.drawable.profile_avatar_placeholder);
        } else {
            mUserName.setText(user.getUsername());
            mAvatar.setUser(user);
        }
        setCoverImage(user);


    }

    private void setCoverImage(User user) {
        if (user == null || TextUtils.isEmpty(user.getCoverUrl()) || user.getCoverUrl().equals("/assets/images/cover-default.png")) {
            mDefaultCover.setVisibility(View.VISIBLE);
            mShadow.setVisibility(View.GONE);
            mHeaderView.setBackgroundColor(ThemeColorManagerCompat.getColorPrimary());
        } else {
            mDefaultCover.setVisibility(View.GONE);
            mShadow.setVisibility(View.VISIBLE);
            GlideApp.with(getContext())
                    .load(NodeBB.BASE_URL + user.getCoverUrl())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                            mHeaderView.setBackground(resource);
                        }
                    });
        }
    }

    @Click(R.id.avatar)
    void loginOrShowUserInfo() {
        NodeBB.getInstance().getRetrofit()
                .create(UserApi.class)
                .me()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((user -> {
                    WebActivity_.intent(this)
                            .extra(WebActivity.EXTRA_URL, NodeBB.url("user/" + user.getUserslug()))
                            .extra(Intent.EXTRA_TITLE, user.getUsername())
                            .start();
                }), error -> {
                    LoginActivity_.intent(getActivity()).start();
                });
    }


    @Click(R.id.accessibility_service)
    void enableOrDisableAccessibilityService() {
        boolean isAccessibilityServiceEnabled = isAccessibilityServiceEnabled();
        boolean checked = mAccessibilityServiceItem.getSwitchCompat().isChecked();
        if (checked && !isAccessibilityServiceEnabled) {
            enableAccessibilityService();
        } else if (!checked && isAccessibilityServiceEnabled) {
            if (!AccessibilityService.disable()) {
                AccessibilityServiceTool.goToAccessibilitySetting();
            }
        }
    }

    @Click(R.id.notification_service)
    void goToNotificationServiceSettings() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            return;
        }
        boolean enabled = NotificationListenerService.getInstance() != null;
        boolean checked = mNotificationPermissionItem.getSwitchCompat().isChecked();
        if ((checked && !enabled) || (!checked && enabled)) {
            startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
        }
    }


    private boolean isAccessibilityServiceEnabled() {
        return AccessibilityServiceTool.isAccessibilityServiceEnabled(getActivity());
    }

    @Click(R.id.floating_window)
    void showOrDismissFloatingWindow() {
        boolean isFloatingWindowShowing = FloatyWindowManger.isCircularMenuShowing();
        boolean checked = mFloatingWindowItem.getSwitchCompat().isChecked();
        if (getActivity() != null && !getActivity().isFinishing()) {
            Pref.setFloatingMenuShown(checked);
        }
        if (checked && !isFloatingWindowShowing) {
            FloatyWindowManger.showCircularMenu();
            enableAccessibilityServiceByRootIfNeeded();
        } else if (!checked && isFloatingWindowShowing) {
            FloatyWindowManger.hideCircularMenu();
        }
    }

    @Click(R.id.theme_color)
    void openThemeColorSettings() {
        SettingsActivity.selectThemeColor(getActivity());
    }

    private void enableAccessibilityServiceByRootIfNeeded() {
        Observable.fromCallable(() -> Pref.shouldEnableAccessibilityServiceByRoot() && !isAccessibilityServiceEnabled())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(needed -> {
                    if (needed) {
                        enableAccessibilityServiceByRoot();
                    }
                });

    }

    @Click(R.id.debug)
    void connectOrDisconnectToRemote() {
        boolean checked = mConnectionItem.getSwitchCompat().isChecked();
        boolean connected = DevPluginService.getInstance().isConnected();
        if (checked && !connected) {
            inputRemoteHost();
        } else if (!checked && connected) {
            DevPluginService.getInstance().disconnectIfNeeded();
        }
    }

    private void inputRemoteHost() {
        String host = Pref.getServerAddressOrDefault(WifiTool.getRouterIp(getActivity()));
        new MaterialDialog.Builder(getActivity())
                .title(R.string.text_server_address)
                .input("", host, (dialog, input) -> {
                    Pref.saveServerAddress(input.toString());
                    connectToRemote(input.toString());
                })
                .neutralText(R.string.text_help)
                .onNeutral((dialog, which) -> {
                    mConnectionItem.getSwitchCompat().setChecked(false, false);
                    IntentUtil.browse(getActivity(), URL_SUBLIME_PLUGIN_HELP);
                })
                .cancelListener(dialog -> mConnectionItem.getSwitchCompat().setChecked(false, false))
                .show();
    }

    private void connectToRemote(String host) {
        DevPluginService.getInstance().connectToServer(host);
    }

    @Click(R.id.check_for_updates)
    void checkForUpdates() {
        mCheckForUpdatesItem.setProgress(true);
        VersionService.getInstance().checkForUpdates()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<VersionInfo>() {

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull VersionInfo versionInfo) {
                        if (getActivity() == null)
                            return;
                        if (versionInfo.isNewer()) {
                            new UpdateInfoDialogBuilder(getActivity(), versionInfo)
                                    .show();
                        } else {
                            Toast.makeText(App.getApp(), R.string.text_is_latest_version, Toast.LENGTH_SHORT).show();
                        }
                        mCheckForUpdatesItem.setProgress(false);
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(App.getApp(), R.string.text_check_update_error, Toast.LENGTH_SHORT).show();
                        mCheckForUpdatesItem.setProgress(false);
                    }
                });
    }

    @Subscribe
    public void onCircularMenuStateChange(CircularMenu.StateChangeEvent event) {
        mFloatingWindowItem.getSwitchCompat().setChecked(event.getCurrentState() != CircularMenu.STATE_CLOSED);
    }


    private void syncSwitchState() {
        mAccessibilityServiceItem.getSwitchCompat().setChecked(
                AccessibilityServiceTool.isAccessibilityServiceEnabled(getActivity()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mNotificationPermissionItem.getSwitchCompat().setChecked(NotificationListenerService.getInstance() != null);
        }

    }

    private void enableAccessibilityService() {
        if (!Pref.shouldEnableAccessibilityServiceByRoot()) {
            AccessibilityServiceTool.goToAccessibilitySetting();
            return;
        }
        enableAccessibilityServiceByRoot();
    }

    private void enableAccessibilityServiceByRoot() {
        mAccessibilityServiceItem.setProgress(true);
        Observable.fromCallable(() -> AccessibilityServiceTool.enableAccessibilityServiceByRootAndWaitFor(4000))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(succeed -> {
                    if (!succeed) {
                        Toast.makeText(getContext(), R.string.text_enable_accessibitliy_service_by_root_failed, Toast.LENGTH_SHORT).show();
                        AccessibilityServiceTool.goToAccessibilitySetting();
                    }
                    mAccessibilityServiceItem.setProgress(false);
                });
    }


    private void showMessage(CharSequence text) {
        if (getContext() == null)
            return;
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mConnectionStateDisposable.dispose();
        EventBus.getDefault().unregister(this);
    }

}
