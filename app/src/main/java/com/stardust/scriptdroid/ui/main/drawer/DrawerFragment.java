package com.stardust.scriptdroid.ui.main.drawer;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.Pref;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.network.GlideApp;
import com.stardust.scriptdroid.network.UserService;
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
import com.stardust.scriptdroid.ui.widget.AvatarView;
import com.stardust.theme.ThemeColorManager;
import com.stardust.theme.ThemeColorManagerCompat;
import com.stardust.view.accessibility.AccessibilityService;
import com.stardust.scriptdroid.sublime.SublimePluginService;
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
 */
@EFragment(R.layout.fragment_drawer)
public class DrawerFragment extends android.support.v4.app.Fragment {

    private static final String URL_SUBLIME_PLUGIN_HELP = "https://github.com/hyb1996/AutoJs-Sublime-Plugin/blob/master/Readme.md";

    @ViewById(R.id.debug)
    DrawerMenuItem mConnectionItem;

    @ViewById(R.id.accessibility_service)
    DrawerMenuItem mAccessibilityServiceItem;

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
        mConnectionStateDisposable = SublimePluginService.getConnectionState()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(connected -> mConnectionItem.getSwitchCompat().setChecked(connected))
                .subscribe();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        syncSwitchState();
    }

    @Override
    public void onResume() {
        super.onResume();
        syncUserInfo();
    }

    @AfterViews
    void setUpViews() {
        ThemeColorManager.addViewBackground(mHeaderView);
    }

    private void syncUserInfo() {
        NodeBB.getInstance().getRetrofit()
                .create(UserApi.class)
                .me()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setUpUserInfo, Throwable::printStackTrace);
    }

    private void setUpUserInfo(User user) {
        mUserName.setText(user.getUsername());
        mAvatar.setUser(user);
        setCoverImage(user);


    }

    private void setCoverImage(User user) {
        String coverUrl = user.getCoverUrl();
        if (TextUtils.isEmpty(coverUrl) || coverUrl.equals("/assets/images/cover-default.png")) {
            mDefaultCover.setVisibility(View.VISIBLE);
            mShadow.setVisibility(View.GONE);
            mHeaderView.setBackgroundColor(ThemeColorManagerCompat.getColorPrimary());
        } else {
            mDefaultCover.setVisibility(View.GONE);
            mShadow.setVisibility(View.VISIBLE);
            GlideApp.with(getContext())
                    .load(NodeBB.BASE_URL + coverUrl)
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
        LoginActivity_.intent(getActivity()).start();
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

    private boolean isAccessibilityServiceEnabled() {
        return AccessibilityServiceTool.isAccessibilityServiceEnabled(getActivity());
    }

    @Click(R.id.floating_window)
    void showOrDismissFloatingWindow() {
        boolean isFloatingWindowShowing = FloatyWindowManger.isCircularMenuShowing();
        boolean checked = mFloatingWindowItem.getSwitchCompat().isChecked();
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
        Observable.fromCallable(() -> Pref.enableAccessibilityServiceByRoot() && !isAccessibilityServiceEnabled())
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
        boolean connected = SublimePluginService.isConnected();
        if (checked && !connected) {
            inputRemoteHost();
        } else if (!checked && connected) {
            SublimePluginService.disconnectIfNeeded();
        }
    }

    private void inputRemoteHost() {
        String host = Pref.getServerAddressOrDefault(WifiTool.getWifiAddress(getActivity()));
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
        mConnectionItem.setProgress(true);
        Toast.makeText(App.getApp(), R.string.text_connecting, Toast.LENGTH_SHORT).show();
        SublimePluginService.connect(host)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<Void>() {

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        if (isHidden()) {
                            return;
                        }
                        Toast.makeText(App.getApp(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        mConnectionItem.getSwitchCompat().setChecked(false, false);
                        mConnectionItem.setProgress(false);
                    }

                    @Override
                    public void onComplete() {
                        mConnectionItem.setProgress(false);
                    }
                });
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
    }

    private void enableAccessibilityService() {
        if (!Pref.enableAccessibilityServiceByRoot()) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mConnectionStateDisposable.dispose();
        EventBus.getDefault().unregister(this);
    }

}
