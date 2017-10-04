package com.stardust.scriptdroid.ui.main.drawer;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.Pref;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.external.floatingwindow.HoverMenuManger;
import com.stardust.scriptdroid.external.floatingwindow.menu.HoverMenuService;
import com.stardust.scriptdroid.network.NodeBB;
import com.stardust.scriptdroid.network.VersionService;
import com.stardust.scriptdroid.network.api.UserApi;
import com.stardust.scriptdroid.network.entity.User;
import com.stardust.scriptdroid.network.entity.VersionInfo;
import com.stardust.scriptdroid.tool.SimpleObserver;
import com.stardust.scriptdroid.ui.login.LoginActivity;
import com.stardust.scriptdroid.ui.login.LoginActivity_;
import com.stardust.scriptdroid.ui.settings.SettingsActivity;
import com.stardust.scriptdroid.ui.update.UpdateInfoDialogBuilder;
import com.stardust.theme.ThemeColorManager;
import com.stardust.view.accessibility.AccessibilityService;
import com.stardust.scriptdroid.sublime.SublimePluginService;
import com.stardust.scriptdroid.tool.AccessibilityServiceTool;
import com.stardust.scriptdroid.tool.WifiTool;
import com.stardust.util.IntentUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.WebSocket;


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
    ImageView mAvatar;


    private Disposable mConnectionStateDisposable;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConnectionStateDisposable = SublimePluginService.getConnectionState()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Boolean>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Boolean connected) throws Exception {
                        mConnectionItem.getSwitchCompat().setChecked(connected);
                    }
                })
                .subscribe();
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
                .subscribe(new SimpleObserver<User>() {
                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull User user) {
                        setUpUserInfo(user);
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    private void setUpUserInfo(User user) {
        mUserName.setText(user.getUsername());
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
        boolean isFloatingWindowShowing = HoverMenuManger.isHoverMenuShowing();
        boolean checked = mFloatingWindowItem.getSwitchCompat().isChecked();
        if (checked && !isFloatingWindowShowing) {
            HoverMenuManger.showHoverMenu();
            enableAccessibilityServiceByRootIfNeeded();
        } else if (!checked && isFloatingWindowShowing) {
            HoverMenuManger.hideHoverMenu();
        }
    }

    @Click(R.id.theme_color)
    void openThemeColorSettings() {
        SettingsActivity.selectThemeColor(getActivity());
    }

    private void enableAccessibilityServiceByRootIfNeeded() {
        Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return Pref.enableAccessibilityServiceByRoot() && !isAccessibilityServiceEnabled();
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Boolean needed) throws Exception {
                        if (needed) {
                            enableAccessibilityServiceByRoot();
                        }
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
                .input("", host, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        Pref.saveServerAddress(input.toString());
                        connectToRemote(input.toString());
                    }
                })
                .neutralText(R.string.text_help)
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mConnectionItem.getSwitchCompat().setChecked(false, false);
                        IntentUtil.browse(getActivity(), URL_SUBLIME_PLUGIN_HELP);
                    }
                })
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mConnectionItem.getSwitchCompat().setChecked(false, false);
                    }
                })
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


    private void syncSwitchState() {
        mAccessibilityServiceItem.getSwitchCompat().setChecked(
                AccessibilityServiceTool.isAccessibilityServiceEnabled(getActivity()));
        mFloatingWindowItem.getSwitchCompat().setChecked(HoverMenuManger.isHoverMenuShowing());
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
        Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return AccessibilityServiceTool.enableAccessibilityServiceByRootAndWaitFor(4000);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Boolean succeed) throws Exception {
                        if (!succeed) {
                            Toast.makeText(getContext(), R.string.text_enable_accessibitliy_service_by_root_failed, Toast.LENGTH_SHORT).show();
                            AccessibilityServiceTool.goToAccessibilitySetting();
                        }
                        mAccessibilityServiceItem.setProgress(false);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mConnectionStateDisposable.dispose();
    }

    @Subscribe
    public void onHoverMenuServiceStateChanged(HoverMenuService.ServiceStateChangedEvent event) {
        mAccessibilityServiceItem.getSwitchCompat().setChecked(event.state);
    }
}
